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
		Server server = new Server(3001);

		WebSocketHandler wsHandler = new WebSocketHandler() {
			@Override
			public void configure(WebSocketServletFactory factory) {
				factory.register(WS_HANDLE.class);
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
}
