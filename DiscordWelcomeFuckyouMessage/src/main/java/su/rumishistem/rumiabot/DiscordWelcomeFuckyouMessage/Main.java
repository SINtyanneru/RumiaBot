package su.rumishistem.rumiabot.DiscordWelcomeFuckyouMessage;

import static su.rumishistem.rumiabot.System.Main.get_discord_bot;
import static su.rumishistem.rumi_java_lib.LOG_PRINT.Main.LOG;

import java.sql.*;
import java.time.*;
import java.util.HashMap;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.entities.*;
import net.dv8tion.jda.api.entities.channel.concrete.TextChannel;
import net.dv8tion.jda.api.events.guild.member.*;
import su.rumishistem.rumi_java_lib.*;
import su.rumishistem.rumi_java_lib.LOG_PRINT.LOG_TYPE;
import su.rumishistem.rumiabot.System.Type.*;
import su.rumishistem.rumiabot.System.Type.DiscordFunction.DiscordChannelFunction;

public class Main implements FunctionClass{
	protected static HashMap<String, HashMap<String, Integer>> invite_table = new HashMap<>();

	@Override
	public String function_name() {
		return "DiscordようこそFuckyouメッセージ";
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
		for (Guild g:get_discord_bot().get_primary_bot().getGuilds()) {
			invite_sync(g);
		}
	}

	protected static void invite_sync(Guild g) {
		if (!g.getSelfMember().hasPermission(Permission.MANAGE_SERVER) ) {
			LOG(LOG_TYPE.INFO, "[DiscordWelcomeFuckyouMessage] 招待コード取得、ただし権限なしのため無視：" + g.getId());
			return;
		}

		g.retrieveInvites().queue(invite_list->{
			HashMap<String, Integer> data = new HashMap<>();
			for (Invite inv:invite_list) {
				data.put(inv.getCode(), inv.getUses());
			}
			invite_table.put(g.getId(), data);

			LOG(LOG_TYPE.OK, "[DiscordWelcomeFuckyouMessage] 招待コード取得：" + g.getId());
		});
	}

	@Override
	public void event_receive(EventReceiveEvent e) {
		try {
			if (e.get_type() == EventReceiveType.DiscordGuildMemberJoin) {
				//参加
				Invite use_invite_code = JoinLog.join(e.get_as_discord_guild_member_join());

				GuildMemberJoinEvent JE = e.get_as_discord_guild_member_join();
				TextChannel Ch = GetChannel(JE.getGuild(), DiscordChannelFunction.welcomemessage);
				if (Ch != null) {
					//送信
					EmbedBuilder EB = new EmbedBuilder();
					EB.setTitle(JE.getUser().getName() + "が参加しました");
					EB.setThumbnail(JE.getUser().getAvatarUrl());
					Ch.sendMessageEmbeds(EB.build()).queue();

					//送信
					EmbedBuilder EB2 = new EmbedBuilder();
					EB2.addField("使用された招待コード", use_invite_code.getCode(), false);
					Ch.sendMessageEmbeds(EB2.build()).queue();
				}
			} else if (e.get_type() == EventReceiveType.DiscordGuildMemberLeave) {
				//脱退
				JoinLog.leave(e.get_as_discord_guild_member_leave());

				GuildMemberRemoveEvent RE = e.get_as_discord_guild_member_leave();
				TextChannel Ch = GetChannel(RE.getGuild(), DiscordChannelFunction.fuckyoumessage);
				if (Ch != null) {
					//参加記録を遡る
					ArrayNode JoinLogResult = SQL.RUN("SELECT * FROM `DISCORD_USER_JOIN` WHERE `GID` = ? AND `UID` = ?;", new Object[] {
						RE.getGuild().getId(),
						RE.getUser().getId()
					});

					EmbedBuilder EB = new EmbedBuilder();
					EB.setTitle(RE.getUser().getName() + "が脱退しました");

					//脱退RTA
					if (JoinLogResult.asArrayList().size() == 1) {
						OffsetDateTime NowDate = OffsetDateTime.now();
						OffsetDateTime JoinDate = ((Timestamp) JoinLogResult.get(0).getData("DATE").asObject()).toInstant().atOffset(ZoneOffset.ofHours(9));
						Duration Du = Duration.between(JoinDate, NowDate);
						String RTAText = "";

						if (Du.getSeconds() <= 1) {
							RTAText = Du.getSeconds() + "秒！早すぎる";
						} else if (Du.getSeconds() <= 5) {
							RTAText = Du.getSeconds() + "秒か、まあまあだな";
						} else {
							RTAText = Du.getSeconds() + "秒？おっそ、雑魚がよ";
						}
						EB.addField("脱退RTA", RTAText, false);
					}

					EB.setThumbnail(RE.getUser().getAvatarUrl());
					Ch.sendMessageEmbeds(EB.build()).queue();
				}
			}
		} catch (Exception ex) {
			ex.printStackTrace();
		}
	}

	private TextChannel GetChannel(Guild G, DiscordChannelFunction F) throws SQLException {
		ArrayNode SR = SQL.RUN("SELECT `CID` FROM `CONFIG` WHERE `GID` = ? AND `FUNC_ID` = ?;", new Object[] {G.getId(), F.name()});
		if (SR.asArrayList().size() == 1) {
			TextChannel Ch = G.getTextChannelById(SR.get(0).getData("CID").asString());
			if (Ch != null) {
				return Ch;
			} else {
				return null;
			}
		} else {
			return null;
		}
	}
}
