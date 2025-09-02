package su.rumishistem.rumiabot.Rank;

import static su.rumishistem.rumiabot.System.FunctionModuleLoader.AddCommand;

import java.io.ByteArrayInputStream;
import java.net.URL;
import java.sql.SQLException;
import java.util.Random;

import javax.imageio.ImageIO;

import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.User;
import su.rumishistem.rumi_java_lib.ArrayNode;
import su.rumishistem.rumi_java_lib.SQL;
import su.rumishistem.rumi_java_lib.RESOURCE.RESOURCE_MANAGER;
import su.rumishistem.rumiabot.System.Discord.MODULE.NameParse;
import su.rumishistem.rumiabot.System.TYPE.CommandData;
import su.rumishistem.rumiabot.System.TYPE.CommandInteraction;
import su.rumishistem.rumiabot.System.TYPE.CommandOption;
import su.rumishistem.rumiabot.System.TYPE.FunctionClass;
import su.rumishistem.rumiabot.System.TYPE.ReceiveMessageEvent;
import su.rumishistem.rumiabot.System.TYPE.SourceType;

public class Main implements FunctionClass{
	private static final String FUNCTION_NAME = "ランキング";
	private static final String FUNCTION_VERSION = "1.0";
	private static final String FUNCTION_AUTOR = "Rumisan";

	@Override
	public String FUNCTION_NAME() {
		return FUNCTION_NAME;
	}
	@Override
	public String FUNCTION_VERSION() {
		return FUNCTION_VERSION;
	}
	@Override
	public String FUNCTION_AUTOR() {
		return FUNCTION_AUTOR;
	}

	@Override
	public void Init() {
		try {
			SelfRank.background_image = ImageIO.read(new ByteArrayInputStream(new RESOURCE_MANAGER(Main.class).getResourceData("/self_rank.png")));
		} catch (Exception EX) {
			EX.printStackTrace();
			throw new RuntimeException("画像ロードエラー");
		}

		AddCommand(new CommandData("rank", new CommandOption[] {}, false));
		AddCommand(new CommandData("ranking", new CommandOption[] {}, false));
	}

	@Override
	public void ReceiveMessage(ReceiveMessageEvent e) {
		try {
			if (e.GetSource() == SourceType.Discord) {
				String user_id = e.GetUser().GetID();
				String guild_id = e.GetDiscordMessage().getGuildId();
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
		} catch (SQLException EX) {
			EX.printStackTrace();
		}
	}

	@Override
	public boolean GetAllowCommand(String Name) {
		return (Name.equals("rank") || Name.equals("ranking"));
	}

	@Override
	public void RunCommand(CommandInteraction CI) throws Exception {
		//TODO:Misskey対応
		if (CI.GetSource() != SourceType.Discord) {
			CI.Reply("Discord以外の対応はしばらくおまちください");
			return;
		}

		if (CI.GetCommand().GetName().equals("rank")) {
			//ランク
			User user = CI.GetDiscordInteraction().getUser();
			String user_id = user.getId();
			String guild_id = CI.GetDiscordInteraction().getGuild().getId();

			CI.AddFile(SelfRank.image_gen(
				user_id,
				guild_id,
				ImageIO.read(new URL(user.getEffectiveAvatarUrl())),
				user.getName()
			));
			CI.Reply("貴様のランク");
		} else if (CI.GetCommand().GetName().equals("ranking")) {
			//ランキング
			String guild_id = CI.GetDiscordInteraction().getGuild().getId();
			ArrayNode result = SQL.RUN("SELECT `UID`, `EXP`, `LEVEL` FROM `DISCORD_RANK` WHERE `GUILD` = ? ORDER BY `LEVEL` DESC, `EXP` DESC;", new Object[] {
				guild_id
			});

			StringBuilder sb = new StringBuilder();

			for (int i = 0; i < result.length(); i++) {
				ArrayNode row = result.get(i);
				String name = row.getData("UID").asString();

				Member member = CI.GetDiscordInteraction().getGuild().getMemberById(row.getData("UID").asString());
				if (member != null) {
					name = new NameParse(member).getDisplayName();
				}

				name = name.replace("@", "[AD]");

				sb.append((i+1)+"位 レベル["+row.getData("LEVEL").asLong()+"]:" + name).append("\n");
			}

			CI.Reply(sb.toString());
		}
	}
}
