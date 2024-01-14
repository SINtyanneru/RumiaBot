package com.rumisystem.rumiabot.WEBSOCKET;

import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.server.handler.HandlerList;
import org.eclipse.jetty.server.handler.ResourceHandler;
import org.eclipse.jetty.websocket.api.Session;
import org.eclipse.jetty.websocket.api.WebSocketAdapter;
import org.eclipse.jetty.websocket.server.WebSocketHandler;
import org.eclipse.jetty.websocket.server.WebSocketServerFactory;
import org.eclipse.jetty.websocket.servlet.WebSocketServletFactory;

public class WS_SERVER {

	public static void main() throws Exception {
		Server server = new Server(3001); // ポートは適切に変更してください

		WebSocketHandler wsHandler = new WebSocketHandler() {
			@Override
			public void configure(WebSocketServletFactory factory) {
				factory.register(MyWebSocketHandler.class);
			}
		};

		ResourceHandler resourceHandler = new ResourceHandler();
		resourceHandler.setResourceBase("./src/main/webapp");

		HandlerList handlers = new HandlerList();
		handlers.addHandler(resourceHandler);
		handlers.addHandler(wsHandler);

		server.setHandler(handlers);

		server.start();
		server.join();
	}

	public static class MyWebSocketHandler extends WebSocketAdapter {
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
}
