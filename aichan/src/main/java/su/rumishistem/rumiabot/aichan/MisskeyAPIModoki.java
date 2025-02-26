package su.rumishistem.rumiabot.aichan;

import static su.rumishistem.rumiabot.System.Main.CONFIG_DATA;

import java.io.IOException;
import java.net.MalformedURLException;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Function;

import com.fasterxml.jackson.core.io.JsonEOFException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

import su.rumishistem.rumi_java_lib.FETCH;
import su.rumishistem.rumi_java_lib.FETCH_RESULT;
import su.rumishistem.rumi_java_lib.SmartHTTP.HTTP_REQUEST;
import su.rumishistem.rumi_java_lib.SmartHTTP.HTTP_RESULT;
import su.rumishistem.rumi_java_lib.SmartHTTP.SmartHTTP;
import su.rumishistem.rumi_java_lib.WebSocket.Server.WebSocketSERVER;
import su.rumishistem.rumi_java_lib.WebSocket.Server.CONNECT_EVENT.CONNECT_EVENT;
import su.rumishistem.rumi_java_lib.WebSocket.Server.CONNECT_EVENT.CONNECT_EVENT_LISTENER;
import su.rumishistem.rumi_java_lib.WebSocket.Server.EVENT.CLOSE_EVENT;
import su.rumishistem.rumi_java_lib.WebSocket.Server.EVENT.MESSAGE_EVENT;
import su.rumishistem.rumi_java_lib.WebSocket.Server.EVENT.WS_EVENT_LISTENER;
import su.rumishistem.rumiabot.aichan.SERVICE.CreateNote;
import su.rumishistem.rumiabot.aichan.SERVICE.CreateReaction;

public class MisskeyAPIModoki {
	private static final String JSONMime = "application/json; charset=UTF-8";
	private static List<CONNECT_EVENT> WebSocketSession = new ArrayList<CONNECT_EVENT>();
	public static String MainChannnelID = "";
	public static String HomeTLChannnelID = "";
	public static String LocalTLChannnelID = "";
	public static String OseloChannnelID = "";
	public static String ServerStatusChannnelID = "";
	public static StringBuilder WebSocketReceiveBuffer = new StringBuilder();

	public static void WebSocketStart() {
		//WebSocketサーバー
		WebSocketSERVER WSS = new WebSocketSERVER();
		WSS.SET_EVENT_VOID(new CONNECT_EVENT_LISTENER() {
			@Override
			public void CONNECT_EVENT(CONNECT_EVENT SESSION) {
				SESSION.SET_EVENT_LISTENER(new WS_EVENT_LISTENER() {
					@Override
					public void MESSAGE(MESSAGE_EVENT e) {
						try {
							System.out.println("[  藍  ][WebSocket]受信:" + e.getMessage());
							WebSocketReceiveBuffer.append(e.getMessage());
							JsonNode Body = new ObjectMapper().readTree(WebSocketReceiveBuffer.toString());

							//此処まで来たらJSONが全部集合したということなので、バッファーを初期化する
							WebSocketReceiveBuffer = new StringBuilder();

							//チャンネルに繋ぐやつ
							if (Body.get("type").asText().equals("connect")) {
								String ID = Body.get("body").get("id").asText();
								String Channel = Body.get("body").get("channel").asText();
								switch (Body.get("body").get("channel").asText()) {
									case "main":
										MainChannnelID = ID;
										break;
									case "homeTimeline":
										HomeTLChannnelID = ID;
										break;
									case "reversi":
										OseloChannnelID = ID;
										break;
									case "localTimeline":
										LocalTLChannnelID = ID;
										break;
									case "serverStats":
										ServerStatusChannnelID = ID;
										break;
								}

								System.out.println("[  藍  ]チャンネル" + Channel + "に接続された！ ID:" + ID);
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
		});
		WSS.START(CONFIG_DATA.get("AI").getData("WS").asInt());
	}

	public static void HTTPStart() throws IOException {
		String DOMAIN = CONFIG_DATA.get("MISSKEY").getData("DOMAIN").asString();
		String TOKEN = CONFIG_DATA.get("MISSKEY").getData("TOKEN").asString();

		//HTTPサーバー
		SmartHTTP SH = new SmartHTTP(CONFIG_DATA.get("AI").getData("HTTP").asInt());

		//自分自身を習得
		SH.SetRoute("/api/i", new Function<HTTP_REQUEST, HTTP_RESULT>() {
			@Override
			public HTTP_RESULT apply(HTTP_REQUEST r) {
				try {
					FETCH AJAX = new FETCH("https://" + DOMAIN + "/api/i");
					AJAX.SetHEADER("Content-Type", JSONMime);
					FETCH_RESULT RESULT = AJAX.POST(("{\"i\": \"" + TOKEN + "\"}").getBytes());

					return new HTTP_RESULT(200, RESULT.GetRAW(), JSONMime);
				} catch (MalformedURLException EX) {
					return new HTTP_RESULT(500, "{}".getBytes(), JSONMime);
				}
			}
		});

		//ノート作成
		SH.SetRoute("/api/notes/create", new Function<HTTP_REQUEST, HTTP_RESULT>() {
			@Override
			public HTTP_RESULT apply(HTTP_REQUEST r) {
				try {
					JsonNode POST_BODY = new ObjectMapper().readTree(r.GetEVENT().getPOST_DATA());
					String ReplyID = null;

					if (POST_BODY.get("replyId") != null) {
						ReplyID = POST_BODY.get("replyId").asText();
					}

					return new HTTP_RESULT(200, CreateNote.Create(POST_BODY.get("text").asText(), ReplyID).getBytes(), JSONMime);
				} catch (Exception EX) {
					EX.printStackTrace();
					return new HTTP_RESULT(500, "{}".getBytes(), JSONMime);
				}
			}
		});

		//ノートにリアクション
		SH.SetRoute("/api/notes/reactions/create", new Function<HTTP_REQUEST, HTTP_RESULT>() {
			@Override
			public HTTP_RESULT apply(HTTP_REQUEST r) {
				try {
					JsonNode POST_BODY = new ObjectMapper().readTree(r.GetEVENT().getPOST_DATA());
					String Reaction = POST_BODY.get("reaction").asText();
					String NoteID = POST_BODY.get("noteId").asText();

					CreateReaction.Create(Reaction, NoteID);

					return new HTTP_RESULT(200, "{}".getBytes(), JSONMime);
				} catch (Exception EX) {
					EX.printStackTrace();
					return new HTTP_RESULT(500, "{}".getBytes(), JSONMime);
				}
			}
		});

		SH.Start();
	}

	public static void SendWebSocket(String Text) {
		//配列にしないと送信できませんわ！
		for (CONNECT_EVENT SESSION:WebSocketSession) {
			SESSION.SendMessage(Text);
		}
	}
}
