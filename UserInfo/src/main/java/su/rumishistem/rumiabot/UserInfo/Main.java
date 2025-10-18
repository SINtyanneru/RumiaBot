package su.rumishistem.rumiabot.UserInfo;

import java.sql.SQLException;
import java.time.Instant;
import java.time.OffsetDateTime;
import java.time.ZoneOffset;

import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.User;
import su.rumishistem.rumi_java_lib.ArrayNode;
import su.rumishistem.rumi_java_lib.SQL;
import su.rumishistem.rumiabot.System.CommandRegister;
import su.rumishistem.rumiabot.System.Module.DateFormat;
import su.rumishistem.rumiabot.System.Type.CommandInteraction;
import su.rumishistem.rumiabot.System.Type.CommandOptionRegist;
import su.rumishistem.rumiabot.System.Type.FunctionClass;
import su.rumishistem.rumiabot.System.Type.OptionType;
import su.rumishistem.rumiabot.System.Type.RunCommand;
import su.rumishistem.rumiabot.System.Type.SourceType;

public class Main implements FunctionClass{
	@Override
	public String function_name() {
		return "ユーザー情報をぶちまけよう";
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
		CommandRegister.add_command("userinfo", new CommandOptionRegist[] {
			new CommandOptionRegist("user", OptionType.User, true)
		}, false, new RunCommand() {
			@Override
			public void run(CommandInteraction e) throws Exception {
				if (e.get_source() == SourceType.Discord) {
					Member member = (Member) e.get_option("user");
					if (member == null) {
						e.reply("エラー");
					}

					ArrayNode InviteSQL = SearchInvite(member.getId(), e.get_discprd_event().getGuild().getId());

					//埋め込みを生成
					EmbedBuilder EB = new EmbedBuilder();
					EB.setThumbnail(member.getEffectiveAvatarUrl());
					EB.setTitle(member.getEffectiveName());

					//登録日
					try {
						EB.addField("登録日", DateFormat._12H(ParseSnowFlake(member.getUser())), false);
					} catch (Exception EX) {
						//EX.printStackTrace();
					}

					//サーバー参加日
					EB.addField("参加日", DateFormat._12H(e.get_discprd_event().getGuild().getMemberById(member.getId()).getTimeJoined()), false);

					//招待コード
					if (InviteSQL != null) {
						EB.addField("使用した招待コード", InviteSQL.getData("INVITE_CODE").asString(), true);
						EB.addField("招待した人", "<@" + InviteSQL.getData("INVITE_UID").asString() + ">", true);
					}

					e.get_discprd_event().getHook().editOriginalEmbeds(EB.build()).queue();
				} else if (e.get_source() == SourceType.Misskey) {
					e.reply("Misskey未実装");
				}
			}
		});
	}


	private ArrayNode SearchInvite(String UID, String GID) throws SQLException {
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
