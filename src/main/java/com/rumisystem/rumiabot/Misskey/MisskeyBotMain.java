package com.rumisystem.rumiabot.Misskey;

import static com.rumisystem.rumi_java_lib.LOG_PRINT.Main.LOG;
import static com.rumisystem.rumiabot.Main.CONFIG_DATA;
import static com.rumisystem.rumiabot.Main.MisskeyBOT;

import com.rumisystem.rumi_java_lib.Misskey.Event.EVENT_LISTENER;
import com.rumisystem.rumi_java_lib.Misskey.Event.NewFollower;
import com.rumisystem.rumi_java_lib.Misskey.Event.NewNoteEvent;
import com.rumisystem.rumi_java_lib.LOG_PRINT.LOG_TYPE;
import com.rumisystem.rumi_java_lib.Misskey.MisskeyClient;
import com.rumisystem.rumi_java_lib.Misskey.RESULT.LOGIN_RESULT;

public class MisskeyBotMain {
	public static void Main() {
		try {
			MisskeyBOT = new MisskeyClient(CONFIG_DATA.get("MISSKEY").asString("DOMAIN"));
			if (MisskeyBOT.TOKEN_LOGIN(CONFIG_DATA.get("MISSKEY").asString("TOKEN")) == LOGIN_RESULT.DONE) {
				MisskeyBOT.SET_EVENT_LISTENER(new EVENT_LISTENER() {
					@Override
					public void onReady() {
						try {
							LOG(LOG_TYPE.OK, "Misskeyサーバーに接続した");
						} catch (Exception EX) {
							EX.printStackTrace();
						}
					}

					@Override
					public void onNewNote(NewNoteEvent E) {
						if (!E.getNOTE().isRN()) {
							if (!E.getNOTE().isKaiMention()) {
								LOG(LOG_TYPE.INFO, E.getUSER().getNAME() + "さんのノート「" + E.getNOTE().getTEXT() + "」");
							} else {
								LOG(LOG_TYPE.INFO, E.getUSER().getNAME() + "さんにメンションされました「" + E.getNOTE().getTEXT() + "」");
							}
						} else {
							LOG(LOG_TYPE.INFO, E.getUSER().getNAME() + "さんがリノートしました");
						}
					}

					@Override
					public void onNewFollower(NewFollower E) {
						LOG(LOG_TYPE.INFO, "新しいフォロワー");
					}
				});
			} else {
				System.out.println("ログイン失敗");
			}
		} catch (Exception EX) {
			EX.printStackTrace();
		}
	}
}
