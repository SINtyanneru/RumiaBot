package su.rumishistem.rumiabot.aichan.API;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import com.fasterxml.jackson.core.io.JsonEOFException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

import su.rumishistem.rumi_java_lib.WebSocket.Server.CONNECT_EVENT.CONNECT_EVENT;
import su.rumishistem.rumi_java_lib.WebSocket.Server.CONNECT_EVENT.CONNECT_EVENT_LISTENER;
import su.rumishistem.rumi_java_lib.WebSocket.Server.EVENT.CLOSE_EVENT;
import su.rumishistem.rumi_java_lib.WebSocket.Server.EVENT.MESSAGE_EVENT;
import su.rumishistem.rumi_java_lib.WebSocket.Server.EVENT.WS_EVENT_LISTENER;
import su.rumishistem.rumiabot.aichan.MisskeyReversiPass;

public class StreamingAPI implements CONNECT_EVENT_LISTENER{
	public static List<CONNECT_EVENT> WebSocketSession = new ArrayList<CONNECT_EVENT>();
	public static String MainChannnelID = "";
	public static String HomeTLChannnelID = "";
	public static String LocalTLChannnelID = "";
	public static String OseloChannnelID = "";
	public static String ServerStatusChannnelID = "";
	public static HashMap<String, String> MisskeyOseloGameTable = new HashMap<String, String>();
	public static StringBuilder WebSocketReceiveBuffer = new StringBuilder();

	@Override
	public void CONNECT_EVENT(CONNECT_EVENT SESSION) {
		System.out.println("[  藍  ][WebSocket]接続");

		SESSION.SET_EVENT_LISTENER(new WS_EVENT_LISTENER() {
			@Override
			public void MESSAGE(MESSAGE_EVENT e) {
				System.out.println("[  藍  ][WebSocket]受信:" + e.getMessage());

				try {
					WebSocketReceiveBuffer.append(e.getMessage());
					JsonNode Body = new ObjectMapper().readTree(WebSocketReceiveBuffer.toString());

					//此処まで来たらJSONが全部集合したということなので、バッファーを初期化する
					WebSocketReceiveBuffer = new StringBuilder();

					//チャンネルに繋ぐやつ
					if (Body.get("type").asText().equals("connect")) {
						String ID = Body.get("body").get("id").asText();
						String Channel = Body.get("body").get("channel").asText();
						switch (Channel) {
							case "main":
								MainChannnelID = ID;
								break;
							case "homeTimeline":
								HomeTLChannnelID = ID;
								break;
							case "reversi":
								OseloChannnelID = ID;
								//MisskeyオセロAPI
								new Thread(new Runnable() {
									@Override
									public void run() {
										try {
											MisskeyReversiPass.Main();
										} catch (Exception EX) {
											EX.printStackTrace();
										}
									}
								}).start();
								break;
							case "reversiGame":
								MisskeyOseloGameTable.put(Body.get("body").get("params").get("gameId").asText(), ID);
								MisskeyReversiPass.WebSocketSend(e.getMessage());
								break;
							case "localTimeline":
								LocalTLChannnelID = ID;
								break;
							case "serverStats":
								ServerStatusChannnelID = ID;
								break;
						}

						System.out.println("[  藍  ]チャンネル" + Channel + "に接続された！ ID:" + ID);
					} else if (Body.get("type").asText().equals("ch")) {
						for (String ID:MisskeyOseloGameTable.values()) {
							if (ID.equals(Body.get("body").get("id").asText())) {
								MisskeyReversiPass.WebSocketSend(e.getMessage());
								return;
							}
						}
					}
				} catch (JsonEOFException EX) {
					//JSONの構文エラーは虫
				} catch (Exception EX) {
					EX.printStackTrace();
				}
			}

			@Override
			public void EXCEPTION(Exception e) {}

			@Override
			public void CLOSE(CLOSE_EVENT e) {}
		});

		//セッションを配列に追加
		WebSocketSession.add(SESSION);
	}
}
