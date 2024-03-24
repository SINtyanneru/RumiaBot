package com.rumisystem.rumiabot_java.MISSKEY_BOT;

import java.net.URI;

public class Main {
	public static void main(String DOMAIN, String TOKEN) {
		try{
			WEBSOCKET WS_CLIENT = new WEBSOCKET(new URI("wss://" + DOMAIN + "/streaming?i=" + TOKEN), TOKEN);
			WS_CLIENT.connect();
		} catch (Exception EX) {
			EX.printStackTrace();
		}
	}
}
