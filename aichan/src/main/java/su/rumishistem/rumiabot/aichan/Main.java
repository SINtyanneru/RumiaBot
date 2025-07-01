package su.rumishistem.rumiabot.aichan;

import static su.rumishistem.rumiabot.System.Main.MisskeyBOT;
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
import su.rumishistem.rumiabot.aichan.MODULE.ConvertType;

import static su.rumishistem.rumiabot.aichan.MisskeyAPIModoki.SendWebSocket;
import static su.rumishistem.rumiabot.aichan.API.StreamingAPI.MainChannnelID;
import static su.rumishistem.rumiabot.aichan.API.StreamingAPI.HomeTLChannnelID;

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
		try {
			if (!Enabled) {
				return;
			}

			//Discordなら機能が有効化されていることを確認
			if (e.GetSource() == SourceType.Discord) {
				if (!e.GetMessage().CheckDiscordGuildFunctionEnabled(DiscordFunction.aichan)) {
					return;
				}
			}
		} catch (Exception EX) {
			EX.printStackTrace();
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
			if (e.GetSource() == SourceType.Discord) {
				//Discord
				Body.put("body", ConvertType.DiscordMessageToNote(e.GetDiscordMessage()));
			} else if (e.GetSource() == SourceType.Misskey) {
				//Misskey
				Body.put("body", ConvertType.NoteToNote(e.GetMisskeyNote()));
			}

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
