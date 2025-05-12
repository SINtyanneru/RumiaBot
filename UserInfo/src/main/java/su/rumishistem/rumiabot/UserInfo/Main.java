package su.rumishistem.rumiabot.UserInfo;

import static su.rumishistem.rumiabot.System.FunctionModuleLoader.AddCommand;

import java.time.Instant;
import java.time.OffsetDateTime;
import java.time.ZoneOffset;

import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.User;
import su.rumishistem.rumi_java_lib.ArrayNode;
import su.rumishistem.rumi_java_lib.SQL;
import su.rumishistem.rumiabot.System.MODULE.DATE_FORMAT;
import su.rumishistem.rumiabot.System.TYPE.CommandData;
import su.rumishistem.rumiabot.System.TYPE.CommandInteraction;
import su.rumishistem.rumiabot.System.TYPE.CommandOption;
import su.rumishistem.rumiabot.System.TYPE.CommandOptionType;
import su.rumishistem.rumiabot.System.TYPE.FunctionClass;
import su.rumishistem.rumiabot.System.TYPE.ReceiveMessageEvent;
import su.rumishistem.rumiabot.System.TYPE.SourceType;

public class Main implements FunctionClass{
	private static final String FUNCTION_NAME = "ユーザー情報をぶちまけよう";
	private static final String FUNCTION_VERSION = "1.0";
	private static final String FUNCTION_AUTOR = "Rumisan";

	public static boolean Enabled = false;

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
		AddCommand(new CommandData("userinfo", new CommandOption[] {
			new CommandOption("user", CommandOptionType.User, null, true)
		}, false));
	}

	@Override
	public void ReceiveMessage(ReceiveMessageEvent e) {
	}

	@Override
	public boolean GetAllowCommand(String Name) {
		return Name.equals("userinfo");
	}

	@Override
	public void RunCommand(CommandInteraction CI) throws Exception {
		if (CI.GetSource() == SourceType.Discord) {
			try {
				User U = CI.GetDiscordInteraction().getOption("user").getAsUser();
				ArrayNode InviteSQL = SearchInvite(U.getId(), CI.GetDiscordInteraction().getGuild().getId());

				//埋め込みを生成
				EmbedBuilder EB = new EmbedBuilder();
				EB.setThumbnail(U.getAvatarUrl());
				EB.setTitle(U.getName());

				//登録日
				try {
					EB.addField("登録日", DATE_FORMAT.ZHUUNI_H(ParseSnowFlake(U)), false);
				} catch (Exception EX) {
					//EX.printStackTrace();
				}

				//サーバー参加日
				EB.addField("参加日", DATE_FORMAT.ZHUUNI_H(CI.GetDiscordInteraction().getGuild().getMemberById(U.getId()).getTimeJoined()), false);

				//招待コード
				if (InviteSQL != null) {
					EB.addField("使用した招待コード", InviteSQL.getData("INVITE_CODE").asString(), true);
					EB.addField("招待した人", "<@" + InviteSQL.getData("INVITE_UID").asString() + ">", true);
				}

				CI.GetDiscordInteraction().getHook().editOriginalEmbeds(EB.build()).queue();
			} catch (Exception EX) {
				EX.printStackTrace();
			}
		} else if (CI.GetSource() == SourceType.Misskey) {
			CI.Reply("Misskeyはまだ未対応です");
		} else {
			//？
		}
	}

	private ArrayNode SearchInvite(String UID, String GID) {
		ArrayNode R = SQL.RUN("SELECT * FROM `DISCORD_USER_JOIN` WHERE `UID` = ? AND `GID` = ?;", new Object[] {UID, GID});

		if (R.asArrayList().size() == 1) {
			return R.get(0);
		} else {
			return null;
		}
	}

	private OffsetDateTime ParseSnowFlake(User U) {
		long userId = Long.parseUnsignedLong(U.getId());
		long timestamp = (userId >> 22) + 1420070400000L;
		return Instant.ofEpochMilli(timestamp).atOffset(ZoneOffset.UTC);
	}
}
