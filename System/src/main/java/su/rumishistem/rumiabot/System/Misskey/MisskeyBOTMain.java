package su.rumishistem.rumiabot.System.Misskey;

import static su.rumishistem.rumiabot.System.Main.MisskeyBOT;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import static su.rumishistem.rumiabot.System.Main.CONFIG_DATA;
import static su.rumishistem.rumiabot.System.Main.FunctionModuleList;
import static su.rumishistem.rumi_java_lib.LOG_PRINT.Main.LOG;

import su.rumishistem.rumi_java_lib.EXCEPTION_READER;
import su.rumishistem.rumi_java_lib.LOG_PRINT.LOG_TYPE;
import su.rumishistem.rumi_java_lib.Misskey.MisskeyClient;
import su.rumishistem.rumi_java_lib.Misskey.Builder.NoteBuilder;
import su.rumishistem.rumi_java_lib.Misskey.Event.DisconnectEvent;
import su.rumishistem.rumi_java_lib.Misskey.Event.EVENT_LISTENER;
import su.rumishistem.rumi_java_lib.Misskey.Event.NewFollower;
import su.rumishistem.rumi_java_lib.Misskey.Event.NewNoteEvent;
import su.rumishistem.rumi_java_lib.Misskey.RESULT.LOGIN_RESULT;
import su.rumishistem.rumiabot.System.MODULE.SearchCommand;
import su.rumishistem.rumiabot.System.TYPE.CommandData;
import su.rumishistem.rumiabot.System.TYPE.CommandInteraction;
import su.rumishistem.rumiabot.System.TYPE.CommandOption;
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
					try {
						LOG(LOG_TYPE.OK, "MisskeyBOT:ready!");

						NoteBuilder NB = new NoteBuilder();
						NB.setTEXT("接続しました");
						MisskeyBOT.PostNote(NB.Build());
					} catch (Exception EX) {
						EX.printStackTrace();
					}
				}
				
				@Override
				public void onNewNote(NewNoteEvent e) {
					try {
						//メンションされていればコマンドとして処理
						if (e.getNOTE().isKaiMention()) {
							String Text = e.getNOTE().getTEXT().replaceAll("^@[^@\\s]+(?:@[^@\\s]+)?", "").replaceAll("^ ", "");
							//先頭が>ならコマンド
							if (Text.startsWith(">")) {
								String[] CMD = Text.replaceAll("^>", "").split(" ");
								HashMap<String, Object> MisskeyOption = new HashMap<String, Object>();

								//オプションを集計
								for (int I = 1; I < CMD.length; I++) {
									if (CMD[I].split("=").length == 2) {
										MisskeyOption.put(CMD[I].split("=")[0], CMD[I].split("=")[1]);
									} else {
										return;
									}
								}

								CommandData Command = SearchCommand.Command(CMD[0]);
								List<CommandOption> OptionList = new ArrayList<CommandOption>();
								FunctionClass Function = SearchCommand.Function(CMD[0]);
								if (Command != null && Function != null) {
									//オプションを加工してばーん
									for (CommandOption Option:Command.GetOptionList()) {
										if (MisskeyOption.get(Option.GetName()) != null) {
											OptionList.add(
												new CommandOption(
													Option.GetName(),
													Option.GetType(),
													MisskeyOption.get(Option.GetName()),
													Option.isRequire()
												)
											);
										} else if (Option.isRequire()) {
											//されていない＆必要ならエラー
											NoteBuilder NB = new NoteBuilder();
											NB.setTEXT("必要なオプションが不足しています:" + Option.GetName());
											NB.setREPLY(e.getNOTE());
											MisskeyBOT.PostNote(NB.Build());
											return;
										}
									}
									Command.SetOptionList(OptionList.toArray(new CommandOption[0]));

									//リアクション
									MisskeyBOT.CreateReaction(e.getNOTE(), ":1039992459209490513:");

									//実行
									new Thread(new Runnable() {
										@Override
										public void run() {
											try {
												Function.RunCommand(new CommandInteraction(SourceType.Misskey, e.getNOTE(), Command));
											} catch (Exception EX) {
												EX.printStackTrace();
												try {
													NoteBuilder NB = new NoteBuilder();
													NB.setTEXT("エラー\n```\n" + EXCEPTION_READER.READ(EX)+ "\n```");
													NB.setREPLY(e.getNOTE());
													MisskeyBOT.PostNote(NB.Build());
												} catch (Exception EX2) {
													//もみ消す
												}
											}
										}
									}).start();
									return;
								} else {
									NoteBuilder NB = new NoteBuilder();
									NB.setTEXT("コマンドがありません");
									NB.setREPLY(e.getNOTE());
									MisskeyBOT.PostNote(NB.Build());
									return;
								}
							}
						}

						//イベント着火(コマンドでは無い)
						for (FunctionClass Function:FunctionModuleList) {
							Function.ReceiveMessage(new ReceiveMessageEvent(
								SourceType.Misskey,
								new MessageUser(
									null,
									e.getUSER()
								),
								null, null,
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

				@Override
				public void onDisconnect(DisconnectEvent e) {
					System.out.println("切断されましたあああああああああああああ");
				}
			});
		} else {
			throw new Error("Misskeyにログインできませんでした");
		}
	}
}
