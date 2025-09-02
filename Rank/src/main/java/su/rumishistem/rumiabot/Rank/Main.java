package su.rumishistem.rumiabot.Rank;

import static su.rumishistem.rumiabot.System.FunctionModuleLoader.AddCommand;

import java.sql.SQLException;
import java.util.Random;

import net.dv8tion.jda.api.entities.Member;
import su.rumishistem.rumi_java_lib.ArrayNode;
import su.rumishistem.rumi_java_lib.SQL;
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
						-- 経験値(レベルアップしたら0にもどす)
						`EXP` = CASE
							WHEN `EXP` + ? >= (100 * `LEVEL`) THEN 0
							ELSE `EXP` + ?
						END,
						-- レベルアップ処理
						`LEVEL` = CASE
							WHEN `EXP` + ? >= (100 * `LEVEL`) THEN `LEVEL` + 1
							ELSE `LEVEL`
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
					//TODO:レベルアップ時の処理
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
			String user_id = CI.GetDiscordInteraction().getMember().getUser().getId();
			String guild_id = CI.GetDiscordInteraction().getGuild().getId();

			ArrayNode result = SQL.RUN("SELECT `EXP`, `LEVEL` FROM `DISCORD_RANK` WHERE `UID` = ? AND `GUILD` = ?;", new Object[] {
				user_id, guild_id
			});

			if (result.length() == 0) {
				CI.Reply("データなし");
				return;
			}

			long exp = result.get(0).getData("EXP").asLong();
			long level = result.get(0).getData("LEVEL").asLong();

			CI.Reply("貴様のレベル:" + level + "\n経験値:" + exp + "\nレベルアップに必要な経験値:" + (100 * level));
		} else if (CI.GetCommand().GetName().equals("ranking")) {
			//ランキング
			String guild_id = CI.GetDiscordInteraction().getGuild().getId();
			ArrayNode result = SQL.RUN("SELECT `UID`, `EXP`, `LEVEL` FROM `DISCORD_RANK` WHERE `GUILD` = ? ORDER BY `LEVEL`, `EXP` DESC;", new Object[] {
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
