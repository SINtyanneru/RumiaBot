package su.rumishistem.rumiabot.System.Misskey;

import static su.rumishistem.rumiabot.System.Main.MisskeyBOT;
import static su.rumishistem.rumiabot.System.Main.CONFIG_DATA;
import static su.rumishistem.rumiabot.System.Main.FunctionModuleList;
import static su.rumishistem.rumi_java_lib.LOG_PRINT.Main.LOG;

import su.rumishistem.rumi_java_lib.LOG_PRINT.LOG_TYPE;
import su.rumishistem.rumi_java_lib.Misskey.MisskeyClient;
import su.rumishistem.rumi_java_lib.Misskey.Event.EVENT_LISTENER;
import su.rumishistem.rumi_java_lib.Misskey.Event.NewFollower;
import su.rumishistem.rumi_java_lib.Misskey.Event.NewNoteEvent;
import su.rumishistem.rumi_java_lib.Misskey.RESULT.LOGIN_RESULT;
import su.rumishistem.rumiabot.System.TYPE.FunctionClass;
import su.rumishistem.rumiabot.System.TYPE.MessageData;
import su.rumishistem.rumiabot.System.TYPE.MessageUser;
import su.rumishistem.rumiabot.System.TYPE.ReceiveMessageEvent;
import su.rumishistem.rumiabot.System.TYPE.SourceType;

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
						//メンションされていればコマンドとして処理
						if (e.getNOTE().isKaiMention()) {
							MisskeyBOT.CreateReaction(e.getNOTE(), ":1039992459209490513:");
						}

						//イベント着火
						for (FunctionClass Function:FunctionModuleList) {
							Function.ReceiveMessage(new ReceiveMessageEvent(
								SourceType.Discord,
								new MessageUser(),
								new MessageData(
									e.getNOTE().getID(),
									e.getNOTE().getTEXT(),
									null,
									e.getNOTE(),
									e.getNOTE().isKaiMention()
								)
							));
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
