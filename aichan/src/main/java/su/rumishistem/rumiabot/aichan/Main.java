package su.rumishistem.rumiabot.aichan;

import java.io.BufferedReader;
import java.io.File;
import java.io.InputStreamReader;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.LinkedHashMap;
import com.fasterxml.jackson.databind.ObjectMapper;
import su.rumishistem.rumiabot.System.TYPE.CommandInteraction;
import su.rumishistem.rumiabot.System.TYPE.DiscordFunction;
import su.rumishistem.rumiabot.System.TYPE.FunctionClass;
import su.rumishistem.rumiabot.System.TYPE.ReceiveMessageEvent;
import su.rumishistem.rumiabot.System.TYPE.SourceType;

import static su.rumishistem.rumiabot.aichan.MisskeyAPIModoki.SendWebSocket;
import static su.rumishistem.rumiabot.aichan.MisskeyAPIModoki.MainChannnelID;
import static su.rumishistem.rumiabot.aichan.MisskeyAPIModoki.HomeTLChannnelID;

public class Main implements FunctionClass {
	private static final String FUNCTION_NAME = "藍ちゃんを乗っとるやつ";
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
	public void Init() {
		try {
			String AIDir = Paths.get("").toAbsolutePath().toString() + "/ai/";
			if (Files.exists(Path.of(AIDir))) {
				ProcessBuilder PB = new ProcessBuilder("npm", "run", "start");
				PB.directory(new File(AIDir));
				PB.redirectErrorStream(true);

				System.out.println("藍を起動しています");

				//起動
				Process P = PB.start();
				Enabled = true;

				//MisskeyAPIもどき起動
				new Thread(new Runnable() {
					@Override
					public void run() {
						try {
							MisskeyAPIModoki.WebSocketStart();
						} catch (Exception EX) {
							EX.printStackTrace();
						}
					}
				}).start();
				new Thread(new Runnable() {
					@Override
					public void run() {
						try {
							MisskeyAPIModoki.HTTPStart();
						} catch (Exception EX) {
							EX.printStackTrace();
						}
					}
				}).start();

				//標準出力
				new Thread(new Runnable() {
					@Override
					public void run() {
						try {
							BufferedReader BR = new BufferedReader(new InputStreamReader(P.getInputStream()));
							String Line;
							while ((Line = BR.readLine()) != null) {
								System.out.println("[  藍  ][SysOut]" + Line);
							}
						} catch (Exception EX) {
							EX.printStackTrace();
						}
					}
				}).start();
			} else {
				System.out.println("藍が見つからなかったので特に何もしません");
			}
		} catch (Exception EX) {
			EX.printStackTrace();
			System.exit(1);
		}
	}
	@Override
	public void ReceiveMessage(ReceiveMessageEvent e) {
		if (!Enabled) {
			return;
		}

		//Discordなら機能が有効化されていることを確認
		if (e.GetSource() == SourceType.Discord) {
			if (!e.GetMessage().CheckDiscordGuildFunctionEnabled(DiscordFunction.aichan)) {
				return;
			}
		}

		try {
			LinkedHashMap<String, Object> WebSocketMessage = new LinkedHashMap<String, Object>();
			WebSocketMessage.put("type", "channel");
			//ぼでー
			LinkedHashMap<String, Object> Body = new LinkedHashMap<String, Object>();

			if (!e.GetMessage().isKaiMention()) {
				//ただのノート
				Body.put("id", HomeTLChannnelID);
				Body.put("type", "note");
			} else {
				//メンション
				Body.put("id", MainChannnelID);
				Body.put("type", "mention");
			}

			//ID
			String ID = "";
			if (e.GetSource() == SourceType.Discord) {
				ID = "D-" + e.GetMessage().GetDiscordChannel().getId() + "_" + e.GetMessage().GetID();
			} else if (e.GetSource() == SourceType.Misskey) {
				ID = "M-" + e.GetMessage().GetID();
			}

			//本文
			String Text = e.GetMessage().GetText();
			if (e.GetSource() == SourceType.Discord) {
				//Discordのメンションを置き換える
				Text = Text.replaceAll("<@\\d{1,100}>", "@rumitest");
			}

			//ノート
			LinkedHashMap<String, Object> NoteBody = new LinkedHashMap<String, Object>();
			NoteBody.put("id", ID);
			NoteBody.put("createAt", "2025-02-26T08:00:10.046Z");
			String UID = "";
			switch (e.GetSource()) {
				case Discord: {
					UID = "D-" + e.GetUser().GetID();
					break;
				}

				case Misskey: {
					UID = "M-" + e.GetUser().GetID();
					break;
				}
			}
			NoteBody.put("userId", UID);
			NoteBody.put("text", Text);
			NoteBody.put("cw", null);
			NoteBody.put("visibility", "public");
			NoteBody.put("localOnly", false);
			NoteBody.put("reactionAcceptance", null);
			NoteBody.put("renoteCount", 0);
			NoteBody.put("repliesCount", 0);
			NoteBody.put("reactionCount", 0);
			NoteBody.put("reactions", new Object[] {});
			NoteBody.put("reactionEmojis", new Object[] {});
			NoteBody.put("reactionAndUserPairCache", new Object[] {});
			NoteBody.put("emojis", new Object[] {});
			NoteBody.put("fileIds", new Object[] {});
			NoteBody.put("files", new Object[] {});
			NoteBody.put("replyId", null);
			NoteBody.put("renoteId", null);
			if (e.GetMessage().isKaiMention()) {
				//自分自身がメンションされてる
				NoteBody.put("mentions", new String[] {"9pzfpcwe7o"});
			}
			NoteBody.put("clippedCount", 0);

			//ユーザー
			LinkedHashMap<String, Object> UserBody = new LinkedHashMap<String, Object>();
			UserBody.put("id", e.GetSource().name() + "_" + e.GetUser().GetID());
			UserBody.put("name", e.GetUser().GetName());
			UserBody.put("username", e.GetUser().GetName());
			if (e.GetSource() == SourceType.Discord) {
				UserBody.put("host", "discord.com");
			} else {
				UserBody.put("host", null);
			}
			UserBody.put("avatarUrl", e.GetUser().GetIconURL());
			//ノートにユーザーを
			NoteBody.put("user", UserBody);
			//ノートをぼでーに
			Body.put("body", NoteBody);
			//ぼでーをメッセージに
			WebSocketMessage.put("body", Body);

			String JSON = new ObjectMapper().writeValueAsString(WebSocketMessage);
			SendWebSocket(JSON);
		} catch (Exception EX) {
			EX.printStackTrace();
		}
	}
	@Override
	public boolean GetAllowCommand(String Name) {
		return false;
	}
	@Override
	public void RunCommand(CommandInteraction CI) throws Exception {
	}
}
