package su.rumishistem.rumiabot.DiscordWelcomeFuckyouMessage;

import java.sql.SQLException;
import java.sql.Timestamp;
import java.time.Duration;
import java.time.OffsetDateTime;
import java.time.ZoneOffset;

import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.Invite;
import net.dv8tion.jda.api.entities.channel.concrete.TextChannel;
import net.dv8tion.jda.api.events.guild.member.GuildMemberJoinEvent;
import net.dv8tion.jda.api.events.guild.member.GuildMemberRemoveEvent;
import su.rumishistem.rumi_java_lib.ArrayNode;
import su.rumishistem.rumi_java_lib.SQL;
import su.rumishistem.rumiabot.System.Discord.DiscordBOT;
import su.rumishistem.rumiabot.System.TYPE.CommandInteraction;
import su.rumishistem.rumiabot.System.TYPE.DiscordChannelFunction;
import su.rumishistem.rumiabot.System.TYPE.DiscordEvent;
import su.rumishistem.rumiabot.System.TYPE.DiscordEvent.EventType;
import su.rumishistem.rumiabot.System.TYPE.FunctionClass;
import su.rumishistem.rumiabot.System.TYPE.ReceiveMessageEvent;

public class Main implements FunctionClass{
	private static final String FUNCTION_NAME = "DiscordようこそFuckyouメッセージ";
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
	public void DiscordEventReceive(DiscordEvent e) throws Exception {
		if (e.GetType() == EventType.GuildMemberAdd) {
			//参加
			GuildMemberJoinEvent JE = (GuildMemberJoinEvent) e.GetEventClass();
			TextChannel Ch = GetChannel(e.GetGuild(), DiscordChannelFunction.welcomemessage);
			if (Ch != null) {
				//送信
				EmbedBuilder EB = new EmbedBuilder();
				EB.setTitle(JE.getUser().getName() + "が参加しました");
				EB.setThumbnail(JE.getUser().getAvatarUrl());
				Ch.sendMessageEmbeds(EB.build()).queue();

				//記録
				SQL.UP_RUN("INSERT INTO `DISCORD_USER_JOIN` (`GID`, `UID`, `DATE`, `INVITE_CODE`, `INVITE_UID`) VALUES (?, ?, NOW(), NULL, NULL)", new Object[] {
					e.GetGuild().getId(),
					JE.getUser().getId()
				});

				e.GetGuild().retrieveInvites().queue(InvList->{
					Invite UseInvCode = null;

					//使われた招待コードを探す
					for (Invite Inv:InvList) {
						int OldUse = DiscordBOT.InviteTable.get(e.GetGuild().getId()).get(Inv.getCode());
						int NewUse = Inv.getUses();
						if (NewUse > OldUse) {
							UseInvCode = Inv;
							break;
						}
					}

					//招待コード同期
					DiscordBOT.GetGuildInvite(e.GetGuild());

					//使った招待コードが判明したら処理
					if (UseInvCode != null) {
						try {
							//SQL書き換え
							SQL.UP_RUN("""
									UPDATE
										`DISCORD_USER_JOIN`
									SET
										`INVITE_CODE` = ?,
										`INVITE_UID` = ?
									WHERE
										`DISCORD_USER_JOIN`.`GID` = ?
									AND
										`DISCORD_USER_JOIN`.`UID` = ?;
								""", new Object[] {
								UseInvCode.getCode(),
								UseInvCode.getInviter().getId(),
								e.GetGuild().getId(),
								JE.getUser().getId()
							});
						} catch (Exception EX) {
							EX.printStackTrace();
						}

						//送信
						EmbedBuilder EB2 = new EmbedBuilder();
						EB2.addField("使用された招待コード", UseInvCode.getCode(), false);
						Ch.sendMessageEmbeds(EB2.build()).queue();
					}
				});
			}
		} else if (e.GetType() == EventType.GuildMemberRemove) {
			//脱退
			GuildMemberRemoveEvent RE = (GuildMemberRemoveEvent) e.GetEventClass();
			TextChannel Ch = GetChannel(e.GetGuild(), DiscordChannelFunction.fuckyoumessage);
			if (Ch != null) {
				//参加記録を遡る
				ArrayNode JoinLogResult = SQL.RUN("SELECT * FROM `DISCORD_USER_JOIN` WHERE `GID` = ? AND `UID` = ?;", new Object[] {
					RE.getGuild().getId(),
					RE.getUser().getId()
				});

				EmbedBuilder EB = new EmbedBuilder();
				EB.setTitle(RE.getUser().getName() + "が脱退しました");

				//削除
				SQL.UP_RUN("DELETE FROM `DISCORD_USER_JOIN` WHERE `GID` = ? AND `UID` = ?;", new Object[] {
					e.GetGuild().getId(),
					RE.getUser().getId()
				});

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

	@Override
	public void Init() {}

	@Override
	public void ReceiveMessage(ReceiveMessageEvent e) {}

	@Override
	public boolean GetAllowCommand(String Name) {return false;}

	@Override
	public void RunCommand(CommandInteraction CI) throws Exception {}
}
