package su.rumishistem.rumiabot.System.Misskey;

import java.io.IOException;
import java.util.HashMap;
import java.util.UUID;
import java.util.concurrent.CountDownLatch;

import su.rumishistem.rumi_java_lib.MisskeyBot.MisskeyClient;
import su.rumishistem.rumi_java_lib.MisskeyBot.Builder.NoteBuilder;
import su.rumishistem.rumi_java_lib.MisskeyBot.Event.MisskeyEventListener;
import su.rumishistem.rumi_java_lib.MisskeyBot.Event.NewBlockEvent;
import su.rumishistem.rumi_java_lib.MisskeyBot.Event.NewFollowEvent;
import su.rumishistem.rumi_java_lib.MisskeyBot.Event.NewNoteEvent;
import su.rumishistem.rumi_java_lib.MisskeyBot.Event.UnFollowEvent;
import su.rumishistem.rumiabot.System.CommandRegister;
import su.rumishistem.rumiabot.System.FunctionLoader;
import su.rumishistem.rumiabot.System.ThreadPool;
import su.rumishistem.rumiabot.System.Module.ErrorPrinter;
import su.rumishistem.rumiabot.System.Type.CommandData;
import su.rumishistem.rumiabot.System.Type.CommandInteraction;
import su.rumishistem.rumiabot.System.Type.FunctionClass;
import su.rumishistem.rumiabot.System.Type.ReceiveMessageEvent;

public class MisskeyBot {
	private MisskeyClient mk;
	public MisskeyBot(String host, String token) throws IOException, InterruptedException {
		mk = new MisskeyClient(host, token);

		CountDownLatch cdl = new CountDownLatch(1);

		mk.add_event_listener(new MisskeyEventListener() {
			@Override
			public void Ready() {
				cdl.countDown();

				NoteBuilder nb = new NoteBuilder();
				nb.set_text("接続しました。");
				mk.create_note(nb);
			}

			@Override
			public void Disconnected() {
				NoteBuilder nb = new NoteBuilder();
				nb.set_text("切断されました...");
				mk.create_note(nb);
			}

			@Override
			public void NewFollow(NewFollowEvent e) {
				try {
					mk.follow(e.get_user());

					NoteBuilder nb = new NoteBuilder();
					nb.set_text("@"+e.get_user().get_username()+"@"+e.get_user().get_host()+"さんにフォローされました！ありがと！");
					mk.create_note(nb);
				} catch (Exception ex) {
					ex.printStackTrace();
				}
			}

			@Override
			public void UnFollow(UnFollowEvent e) {
				try {
					mk.unfollow(e.get_user());

					NoteBuilder nb = new NoteBuilder();
					nb.set_text("@"+e.get_user().get_username()+"@"+e.get_user().get_host()+"さんにフォロー解除されちゃいました...");
					mk.create_note(nb);
				} catch (Exception ex) {
					ex.printStackTrace();
				}
			}

			@Override
			public void NewBlock(NewBlockEvent e) {
				try {
					NoteBuilder nb = new NoteBuilder();
					nb.set_text("@"+e.get_user().get_username()+"@"+e.get_user().get_host()+"さんにブロックされちゃいました...");
					mk.create_note(nb);
				} catch (Exception ex) {
					ex.printStackTrace();
				}
			}

			@Override
			public void NewNote(NewNoteEvent e) {
				try {
					//コマンドチェック
					if (e.get_note().is_mention()) {
						String text = e.get_note().get_text();
						int index = text.indexOf(">");
						if (index != -1) {
							//コマンドモード
							StringBuilder sb = new StringBuilder();
							for (int i = index + 1; i < text.length(); i++) {
								char c = text.charAt(i);
								if (c == '\n');
								sb.append(c);
							}

							mk.create_reaction(e.get_note(), "1039992459209490513");

							String cmd = sb.toString();
							String command_name = cmd.split(" ")[0];
							CommandData command = CommandRegister.get(command_name);
							HashMap<String, Object> option = new HashMap<>();

							//コマンドがない
							if (command == null) {
								NoteBuilder nb = new NoteBuilder();
								nb.set_text("コマンドがない");
								nb.set_reply(e.get_note());
								mk.create_note(nb);
								return;
							}

							//オプション解析
							String key = "";
							String value = "";
							boolean end_key = false;
							for (int i = command_name.length() + 1; i < cmd.length(); i++) {
								if (!end_key) {
									//キー
									if (cmd.charAt(i) == '=') {
										end_key = true;

										i += 1;
										continue;
									}

									key += cmd.charAt(i);
								} else {
									if (cmd.charAt(i) == '"' || cmd.charAt(i) == ' ') {
										option.put(key.toUpperCase(), value);

										key = "";
										value = "";
										end_key = false;
										i += 1;
										continue;
									}

									value += cmd.charAt(i);
								}
							}

							//実行
							ThreadPool.run_command(new Runnable() {
								@Override
								public void run() {
									try {
										command.get_task().run(new CommandInteraction(e.get_note(), option, command.is_private()));
									} catch (Exception ex) {
										String id = UUID.randomUUID().toString();
										ErrorPrinter.print(id, ex);

										NoteBuilder nb = new NoteBuilder();
										nb.set_text("エラー：" + ex.getMessage() + "\n["+id+"]");
										nb.set_reply(e.get_note());
										mk.create_note(nb);
									}
								}
							});
							return;
						}
					}

					//メッセージ受信モード
					for (FunctionClass f:FunctionLoader.get_list()) {
						ThreadPool.run_message_event(new Runnable() {
							@Override
							public void run() {
								try {
									f.message_receive(new ReceiveMessageEvent(e));
								} catch (Exception ex) {
									ErrorPrinter.print(UUID.randomUUID().toString(), ex);
								}
							}
						});
					}
				} catch (Exception ex) {
					ex.printStackTrace();
				}
			}
		});

		cdl.await();
	}

	public MisskeyClient get_client() {
		return mk;
	}
}
