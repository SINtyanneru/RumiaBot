package su.rumishistem.rumiabot.System;

import static su.rumishistem.rumiabot.System.Main.DISCORD_BOT;

import java.sql.SQLException;

import net.dv8tion.jda.api.entities.User;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import su.rumishistem.rumi_java_lib.REON4213.REON4213Parser;
import su.rumishistem.rumi_java_lib.REON4213.Type.VBlock;
import su.rumishistem.rumiabot.System.MODULE.AdminManager;
import su.rumishistem.rumiabot.System.MODULE.BlockManager;
import su.rumishistem.rumiabot.System.TYPE.SourceType;

public class Admin {
	public static void discord(MessageReceivedEvent e) {
		if (!AdminManager.IsAdmin(SourceType.Discord, e.getMember().getUser().getId())) return;
		String result = parse(e.getMessage().getContentRaw(), e.getAuthor().getId(), SourceType.Discord);
		if (result == null) return;

		e.getMessage().reply(result).queue();
	}

	//TODO:Misskey

	private static String parse(String text, String user_id, SourceType source) {
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
