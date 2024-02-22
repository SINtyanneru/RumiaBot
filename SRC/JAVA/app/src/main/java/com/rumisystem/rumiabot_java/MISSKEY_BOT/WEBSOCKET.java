package com.rumisystem.rumiabot_java.MISSKEY_BOT;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.rumisystem.rumiabot_java.HTTP_REQUEST;
import org.java_websocket.client.WebSocketClient;
import org.java_websocket.handshake.ServerHandshake;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.net.URI;
import java.util.HashMap;

public class WEBSOCKET extends WebSocketClient {
	public static WEBSOCKET WS = null;
	private URI SERVER_URL;
	private String TOKEN;

	public WEBSOCKET(URI SERVER_URL, String TOKEN){
		super(SERVER_URL);

		this.SERVER_URL = SERVER_URL;
		this.TOKEN = TOKEN;

		WS = this;
	}

	@Override//つながった
	public void onOpen(ServerHandshake HAND_SHAKE_DATA) {
		System.out.println("Misskeyサーバーに接続しました");
		SEND_MESSAGE("{\"type\":\"connect\",\"body\":{\"channel\":\"main\",\"id\":\"1\",\"params\":{\"withRenotes\":true,\"withReplies\":false}}}");
		SEND_MESSAGE("{\"type\":\"connect\",\"body\":{\"channel\":\"homeTimeline\",\"id\":\"2\",\"params\":{\"withRenotes\":true,\"withReplies\":false}}}");
	}

	@Override//受信
	public void onMessage(String MESSAGE) {
		try {
			System.out.println(MESSAGE);

			ObjectMapper OM = new ObjectMapper();

			JsonNode MESSAGE_JSON = OM.readTree(MESSAGE);
			switch (MESSAGE_JSON.get("body").get("type").asText()){
				case "followed":{
					String AJAX = new HTTP_REQUEST("https://" + SERVER_URL.getHost() + "/api/following/create").POST("{\"userId\":\"" + MESSAGE_JSON.get("body").get("body").get("id").asText() + "\",\"withReplies\":false,\"i\":\"" + TOKEN + "\"}");
					ObjectMapper OM_RESULT = new ObjectMapper();
					JsonNode FOLLOW_RESULT = OM_RESULT.readTree(AJAX);
					if(FOLLOW_RESULT.get("error") != null){
						System.out.println(FOLLOW_RESULT.get("name").asText() + "にフォローされたのでフォロバしました");
					} else {
						System.err.println(FOLLOW_RESULT.get("name").asText() + "にフォローされたのでフォロバしようとしたらできんかったわ");
					}
					break;
				}

				case "mention":{
					JsonNode NOTE_DATA = MESSAGE_JSON.get("body").get("body");

					if(NOTE_DATA.get("text").asText().contains("")){

					} else {
						String AJAX = new HTTP_REQUEST("https://" + SERVER_URL.getHost() + "/api/notes/reactions/create").POST("{\"noteId\":\"" + NOTE_DATA.get("id").asText() + "\",\"reaction\":\":1039992459209490513:\",\"i\":\"" + TOKEN + "\"}");
					}
					break;
				}
			}
		} catch (Exception EX) {
			EX.printStackTrace();
		}
	}

	@Override//切断
	public void onClose(int CODE, String REASON, boolean REMOTE) {
		System.out.println("[ MISSKEY_BOT ]Disconnected");
	}

	@Override//エラー
	public void onError(Exception EX) {
		EX.printStackTrace();
	}

	public static void SEND_MESSAGE(String TEXT){
		if(WS != null){
			if(WS.isOpen()){
				WS.send(TEXT);
			}
		}
	}
}
