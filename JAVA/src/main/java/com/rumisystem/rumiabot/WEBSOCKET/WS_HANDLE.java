package com.rumisystem.rumiabot.WEBSOCKET;

import com.rumisystem.rumiabot.URI_PARAM_PARSER;
import org.eclipse.jetty.websocket.api.Session;
import org.eclipse.jetty.websocket.api.WebSocketAdapter;

import java.util.HashMap;

import static com.rumisystem.rumiabot.Main.LOG;

public class WS_HANDLE extends WebSocketAdapter {
	private final String INFO_LOG_TAG = " INFO  | WS";
	private final String ERR_LOG_TAG = " ERR   | WS";

	private Session WS_SESSION = null;

	@Override//接続
	public void onWebSocketConnect(Session SESSION) {
		try{
			super.onWebSocketConnect(SESSION);
			String URI = getSession().getUpgradeRequest().getRequestURI().toString();
			HashMap<String, String> URI_PARAM = new URI_PARAM_PARSER(URI).PARSE();

			//セッションを変数に追加
			WS_SESSION = SESSION;

			LOG(INFO_LOG_TAG, "New Connected :" + SESSION.getRemoteAddress().getAddress(), 0);
			LOG(INFO_LOG_TAG, "ID            :" + URI_PARAM.get("ID"), 0);
		}catch (Exception EX){
			LOG(INFO_LOG_TAG, "ERROR!", 1);
			EX.printStackTrace();
		}
	}

	@Override//メッセージを受信
	public void onWebSocketText(String MESSAGE) {
		try {
			super.onWebSocketText(MESSAGE);
			LOG(INFO_LOG_TAG, "Message received:" + MESSAGE, 0);
			//メッセージを解析(;でsplit メッセージはるみ語に倣って;で区切る)
			String[] CMD = MESSAGE.split(";");

			//コマンドに寄って処理を変える
			switch (CMD[0]){
				case "HELLO":
					getSession().getRemote().sendString(CMD[0] + ";HELLO;200");
					break;

				default:
					getSession().getRemote().sendString(CMD[0] + ";ERR;構文エラー;400");
			}
		}catch (Exception EX){
			LOG(ERR_LOG_TAG, "onWebSocketText Error!:" + MESSAGE, 1);
		}
	}

	@Override
	public void onWebSocketClose(int STATUS_CODE, String REASON) {
		super.onWebSocketClose(STATUS_CODE, REASON);
		LOG(INFO_LOG_TAG, "Connection close, code:" + STATUS_CODE + ",reason:" + REASON , 0);
	}
}