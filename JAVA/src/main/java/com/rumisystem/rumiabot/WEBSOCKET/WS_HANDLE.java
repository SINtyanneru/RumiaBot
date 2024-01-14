package com.rumisystem.rumiabot.WEBSOCKET;


import org.eclipse.jetty.websocket.api.Session;
import org.eclipse.jetty.websocket.api.WebSocketAdapter;

public class WS_HANDLE extends WebSocketAdapter {
	@Override
	public void onWebSocketConnect(Session session) {
		super.onWebSocketConnect(session);
		System.out.println("WebSocket connected: " + session.getRemoteAddress().getAddress());
	}

	@Override
	public void onWebSocketText(String message) {
		super.onWebSocketText(message);
		System.out.println("Received message: " + message);

		// クライアントにメッセージを返信
		try {
			getSession().getRemote().sendString("Server received: " + message);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	@Override
	public void onWebSocketClose(int statusCode, String reason) {
		super.onWebSocketClose(statusCode, reason);
		System.out.println("WebSocket closed: " + statusCode + ", " + reason);
	}
}