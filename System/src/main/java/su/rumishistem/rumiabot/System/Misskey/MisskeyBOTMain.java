package su.rumishistem.rumiabot.System.Misskey;

import static su.rumishistem.rumiabot.System.Main.MisskeyBOT;
import static su.rumishistem.rumiabot.System.Main.CONFIG_DATA;
import static su.rumishistem.rumi_java_lib.LOG_PRINT.Main.LOG;

import su.rumishistem.rumi_java_lib.LOG_PRINT.LOG_TYPE;
import su.rumishistem.rumi_java_lib.Misskey.MisskeyClient;
import su.rumishistem.rumi_java_lib.Misskey.Builder.NoteBuilder;
import su.rumishistem.rumi_java_lib.Misskey.Event.EVENT_LISTENER;
import su.rumishistem.rumi_java_lib.Misskey.Event.NewFollower;
import su.rumishistem.rumi_java_lib.Misskey.Event.NewNoteEvent;
import su.rumishistem.rumi_java_lib.Misskey.RESULT.LOGIN_RESULT;

public class MisskeyBOTMain {
	public static void Init() {
		String DOMAIN = CONFIG_DATA.get("MISSKEY").getData("DOMAIN").asString();
		String TOKEN = CONFIG_DATA.get("MISSKEY").getData("TOKEN").asString();
		MisskeyBOT = new MisskeyClient(DOMAIN);
		if (MisskeyBOT.TOKEN_LOGIN(TOKEN) == LOGIN_RESULT.DONE) {
			MisskeyBOT.SET_EVENT_LISTENER(new EVENT_LISTENER() {
				@Override
				public void onReady() {
					LOG(LOG_TYPE.OK, "MisskeyBOT:ready!");
				}
				
				@Override
				public void onNewNote(NewNoteEvent e) {
					try {
						if (e.getNOTE().isKaiMention()) {
							MisskeyBOT.CreateReaction(e.getNOTE(), ":1039992459209490513:");

							//ping
							if (e.getNOTE().getTEXT().equals("@rumiabot ping")) {
								NoteBuilder NB = new NoteBuilder();
								NB.setTEXT("f**k");
								NB.setREPLY(e.getNOTE());
								MisskeyBOT.PostNote(NB.Build());
								return;
							}

							//コマンド
						}
					} catch (Exception EX) {
						EX.printStackTrace();
					}
				}
				
				@Override
				public void onNewFollower(NewFollower e) {
					e.getUser().Follow();
					LOG(LOG_TYPE.INFO, "フォローされたのでフォロバしました");
				}
			});
		} else {
			throw new Error("Misskeyにログインできませんでした");
		}
	}
}
