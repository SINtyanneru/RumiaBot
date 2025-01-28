package su.rumishistem.rumiabot.Misskey;

import static com.rumisystem.rumi_java_lib.LOG_PRINT.Main.LOG;
import static su.rumishistem.rumiabot.Main.CONFIG_DATA;
import static su.rumishistem.rumiabot.Main.MisskeyBOT;

import com.rumisystem.rumi_java_lib.Misskey.Event.EVENT_LISTENER;
import com.rumisystem.rumi_java_lib.Misskey.Event.NewFollower;
import com.rumisystem.rumi_java_lib.Misskey.Event.NewNoteEvent;
import com.rumisystem.rumi_java_lib.LOG_PRINT.LOG_TYPE;
import com.rumisystem.rumi_java_lib.Misskey.MisskeyClient;
import com.rumisystem.rumi_java_lib.Misskey.Builder.NoteBuilder;
import com.rumisystem.rumi_java_lib.Misskey.RESULT.LOGIN_RESULT;

import su.rumishistem.rumiabot.Discord.COMMAND.ip;
import su.rumishistem.rumiabot.MODULE.COMMAND_INTERACTION;
import su.rumishistem.rumiabot.MODULE.ISHITEGAWA.ISHITEGAWA_DAM;
import su.rumishistem.rumiabot.Misskey.FUNCTION.DAM_NOTE;

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
						try {
							if (!E.getNOTE().isRN()) {
								//ノート
								LOG(LOG_TYPE.INFO, E.getUSER().getNAME() + "さんのノート「" + E.getNOTE().getTEXT() + "」");

								//メンション=コマンド
								if (E.getNOTE().isKaiMention()) {
									MisskeyBOT.CreateReaction(E.getNOTE(), ":1039992459209490513:");

									COMMAND_INTERACTION CI = new COMMAND_INTERACTION(E.getNOTE());

									switch(CI.GetNAME()) {
										case "test": {
											CI.SetTEXT("あいうえお");
											CI.Reply();
											break;
										}

										case "ip":{
											ip.Main(CI);
											break;
										}

										default: {
											NoteBuilder NB = new NoteBuilder();
											NB.setTEXT("こゃーん");
											NB.setREPLY(E.getNOTE());
											MisskeyBOT.PostNote(NB.Build());
										}
									}
								}

								if (E.getUSER().getUID().equals("Rumisan") && E.getNOTE().getTEXT().equals("@rumiabot 石手川ダム")) {
									try {
										DAM_NOTE.Main();
									} catch (Exception EX) {
										EX.printStackTrace();
									}
								}
							} else {
								//リノート
								LOG(LOG_TYPE.INFO, E.getUSER().getNAME() + "さんがリノートしました");
							}
						} catch (Exception EX) {
							EX.printStackTrace();
						}
					}

					@Override
					public void onNewFollower(NewFollower E) {
						LOG(LOG_TYPE.INFO, "新しいフォロワー");
						E.getUser().Follow();
					}
				});
			} else {
				LOG(LOG_TYPE.FAILED, "ログイン失敗");
			}
		} catch (Exception EX) {
			EX.printStackTrace();
		}
	}
}
