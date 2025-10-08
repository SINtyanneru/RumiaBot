package su.rumishistem.rumiabot.System;

import static su.rumishistem.rumiabot.System.Main.DISCORD_BOT;

import java.sql.SQLException;
import java.util.concurrent.CountDownLatch;

import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.User;
import net.dv8tion.jda.api.entities.channel.concrete.TextChannel;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import su.rumishistem.rumi_java_lib.REON4213.REON4213Parser;
import su.rumishistem.rumi_java_lib.REON4213.Type.VBlock;
import su.rumishistem.rumiabot.System.MODULE.AdminManager;
import su.rumishistem.rumiabot.System.MODULE.BlockManager;
import su.rumishistem.rumiabot.System.TYPE.SourceType;
import su.rumishistem.rumiabot.System.TYPE.ThreadPoolStatus;

public class Admin {
	public static void discord(MessageReceivedEvent e) {
		if (!AdminManager.IsAdmin(SourceType.Discord, e.getMember().getUser().getId())) return;
		String result = parse(e.getMessage().getContentRaw(), e.getAuthor().getId(), SourceType.Discord, e);
		if (result == null) return;

		e.getMessage().reply(result).queue();
	}

	//TODO:Misskey

	private static String parse(String text, String user_id, SourceType source, MessageReceivedEvent de) {
		REON4213Parser P = new REON4213Parser(text);
		if (P.GetHacudouShi() != null) {
			if (P.GetCls().get("RB") != null) {
				for (VBlock V:P.GetCls().get("RB")) {
					switch (V.GetVerb()) {
						case "Block": {
							User U = DISCORD_BOT.getUserById(V.GetObject());
							if (U == null) {
								return V.GetObject() + "というユーザーが見つからなかった";
							}
							try {
								BlockManager.addBlock(source, user_id);
								return V.GetObject() + "をブロックした";
							} catch (SQLException EX) {
								EX.printStackTrace();
								return V.GetObject() + "をブロックできんかった！";
							}
						}

						case "Show": {
							ThreadPoolStatus status = null;
							switch (V.GetObject()) {
								case "command v pool":
									status = ThreadPool.get_command_status();
									break;

								case "message v pool":
									status = ThreadPool.get_message_status();
									break;

								case "discord v pool":
									status = ThreadPool.get_discord_status();
									break;

								default:
									return "そのオブジェクトは存在しません";
							}

							return status.max + "\\" + status.active;
						}

						case "List": {
							switch (V.GetObject()) {
								case "discord v guild":
									StringBuilder sb = new StringBuilder();
									for (Guild g:DISCORD_BOT.getGuilds()) {
										sb.append(g.getName());
										sb.append("("+g.getId()+")");
										sb.append("\n");
									}
									return sb.toString();
								default:
									return "？";
							}
						}

						case "Invite": {
							Guild g = DISCORD_BOT.getGuildById(V.GetObject());
							if (g == null) return "サーバーが見つかりませんでした";
							if (g.getTextChannels().size() == 0) return "チャンネルが無いようです";

							CountDownLatch cdl = new CountDownLatch(1);
							String[] invite_url = {null};
							TextChannel ch = g.getTextChannels().get(0);
							ch.createInvite().setMaxAge(0).setMaxUses(0).setTemporary(false).queue(inv -> {
								invite_url[0] = inv.getUrl();
								cdl.countDown();
							});

							try {
								cdl.await();
							} catch (InterruptedException EX) {
								EX.printStackTrace();
							}

							if (invite_url[0] == null) {
								return "失敗！";
							} else {
								return invite_url[0];
							}
						}

						default: {
							return "未定義動作";
						}
					}
				}
			}
		}

		return null;
	}
}
