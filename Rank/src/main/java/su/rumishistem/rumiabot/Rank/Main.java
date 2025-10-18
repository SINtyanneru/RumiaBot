package su.rumishistem.rumiabot.Rank;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.net.URL;
import java.sql.SQLException;
import java.util.Random;

import javax.imageio.ImageIO;

import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.User;
import su.rumishistem.rumi_java_lib.ArrayNode;
import su.rumishistem.rumi_java_lib.SQL;
import su.rumishistem.rumi_java_lib.RESOURCE.RESOURCE_MANAGER;
import su.rumishistem.rumiabot.System.CommandRegister;
import su.rumishistem.rumiabot.System.Module.NameParse;
import su.rumishistem.rumiabot.System.Type.CommandInteraction;
import su.rumishistem.rumiabot.System.Type.CommandOptionRegist;
import su.rumishistem.rumiabot.System.Type.FunctionClass;
import su.rumishistem.rumiabot.System.Type.ReceiveMessageEvent;
import su.rumishistem.rumiabot.System.Type.RunCommand;
import su.rumishistem.rumiabot.System.Type.SourceType;

public class Main implements FunctionClass{
	@Override
	public String function_name() {
		return "ランキング";
	}
	@Override
	public String function_version() {
		return "1.0";
	}
	@Override
	public String function_author() {
		return "るみ";
	}

	@Override
	public void init() {
		try {
			SelfRank.background_image = ImageIO.read(new ByteArrayInputStream(new RESOURCE_MANAGER(Main.class).getResourceData("/self_rank.png")));
		} catch (Exception EX) {
			EX.printStackTrace();
			throw new RuntimeException("画像ロードエラー");
		}

		CommandRegister.add_command("rank", new CommandOptionRegist[] {}, false, new RunCommand() {
			@Override
			public void run(CommandInteraction e) throws Exception {
				//ランク
				User user = e.get_discprd_event().getUser();
				String user_id = user.getId();
				String guild_id = e.get_discprd_event().getGuild().getId();

				File rank_file = SelfRank.image_gen(
					user_id,
					guild_id,
					ImageIO.read(new URL(user.getEffectiveAvatarUrl())),
					user.getName()
				);

				e.add_file(rank_file);
				e.reply("貴様のランク");

				rank_file.delete();
			}
		});

		CommandRegister.add_command("ranking", new CommandOptionRegist[] {}, false, new RunCommand() {
			@Override
			public void run(CommandInteraction e) throws Exception {
				//ランキング
				String guild_id = e.get_discprd_event().getGuild().getId();
				ArrayNode result = SQL.RUN("SELECT `UID`, `EXP`, `LEVEL` FROM `DISCORD_RANK` WHERE `GUILD` = ? ORDER BY `LEVEL` DESC, `EXP` DESC;", new Object[] {
					guild_id
				});

				StringBuilder sb = new StringBuilder();

				for (int i = 0; i < result.length(); i++) {
					ArrayNode row = result.get(i);
					String name = row.getData("UID").asString();

					Member member = e.get_discprd_event().getGuild().getMemberById(row.getData("UID").asString());
					if (member != null) {
						name = new NameParse(member).getDisplayName();
					}

					name = name.replace("@", "[AD]");

					sb.append((i+1)+"位 レベル["+row.getData("LEVEL").asLong()+"]:" + name).append("\n");
				}

				e.reply(sb.toString());
			}
		});
	}

	@Override
	public void message_receive(ReceiveMessageEvent e) {
		try {
			if (e.get_source() == SourceType.Discord) {
				String user_id = e.get_discord().getAuthor().getId();
				String guild_id = e.get_discord().getGuild().getId();
				long get_exp = new Random().nextLong(10) + 5;

				ArrayNode result = SQL.RUN("""
					INSERT
						INTO `DISCORD_RANK` (`UID`, `GUILD`, `EXP`, `LEVEL`)
					VALUES
						(?, ?, ?, 1)
					ON DUPLICATE KEY UPDATE
						-- レベルアップ処理
						`LEVEL` = CASE
							WHEN `EXP` + ? >= (100 * `LEVEL`) THEN `LEVEL` + 1
							ELSE `LEVEL`
						END,
						-- 経験値(レベルアップしたら0にもどす)
						`EXP` = CASE
							WHEN `EXP` + ? >= (100 * `LEVEL`) THEN 0
							ELSE `EXP` + ?
						END
					-- レベルが上がったら上がったレベルを返す
					RETURNING
						CASE
							WHEN `EXP` + ? >= (100 * `LEVEL`) THEN `LEVEL`
							ELSE 0
						END AS `LEVEL_UP`
				""", new Object[] {
					user_id, guild_id, get_exp,
					get_exp, get_exp,
					get_exp, get_exp
				});

				if (result.length() == 1) {
					long new_level = result.get(0).getData("LEVEL_UP").asLong();
					if (new_level == 0) return;
					SQL.UP_RUN("UPDATE `DISCORD_RANK` SET `EXP` = '0' WHERE `UID` = ? AND `GUILD` = ?; ", new Object[] {
						user_id, guild_id
					});
				}
			}
		} catch (SQLException ex) {
			ex.printStackTrace();
		}
	}
}
