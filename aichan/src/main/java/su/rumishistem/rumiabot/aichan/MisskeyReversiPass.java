package su.rumishistem.rumiabot.aichan;

import static su.rumishistem.rumiabot.System.Main.CONFIG_DATA;
import java.util.LinkedHashMap;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import su.rumishistem.rumi_java_lib.WebSocket.Client.WebSocketClient;
import su.rumishistem.rumi_java_lib.WebSocket.Client.EVENT.CLOSE_EVENT;
import su.rumishistem.rumi_java_lib.WebSocket.Client.EVENT.CONNECT_EVENT;
import su.rumishistem.rumi_java_lib.WebSocket.Client.EVENT.MESSAGE_EVENT;
import su.rumishistem.rumi_java_lib.WebSocket.Client.EVENT.WS_EVENT_LISTENER;
import su.rumishistem.rumiabot.aichan.API.StreamingAPI;

public class MisskeyReversiPass {
	private static final String LOG_PREFIX = "[  藍  ][MisskeyReversiPass]";
	private static WebSocketClient WS;
	private static CONNECT_EVENT SESSION;

	public static void Main() {
		WS = new WebSocketClient();

		WS.SET_EVENT_LISTENER(new WS_EVENT_LISTENER() {
			@Override
			public void MESSAGE(MESSAGE_EVENT e) {
				try {
					JsonNode Body = new ObjectMapper().readTree(e.getMessage());
					System.out.print(LOG_PREFIX+"受信:");
					System.out.print(Body+"\n");

					MisskeyAPIModoki.WebSocketSend(e.getMessage());
				} catch (Exception EX) {
					EX.printStackTrace();
				}
			}

			@Override
			public void EXCEPTION(Exception EX) {
				EX.printStackTrace();
			}

			@Override
			public void CONNECT(CONNECT_EVENT e) {
				System.out.println(LOG_PREFIX+"オセロAPI接続");
				SESSION = e;
				e.SEND("{\"type\":\"connect\",\"body\":{\"channel\":\"reversi\",\"id\":\""+StreamingAPI.OseloChannnelID+"\"}}");
			}

			@Override
			public void CLOSE(CLOSE_EVENT e) {
				System.out.println(LOG_PREFIX+"オセロAPI切断");
			}
		});

		System.out.println(LOG_PREFIX+"オセロAPIのパススルー起動");
		WS.CONNECT("wss://" + CONFIG_DATA.get("MISSKEY").getData("DOMAIN").asString() + "/streaming?i=" + CONFIG_DATA.get("MISSKEY").getData("TOKEN").asString());
	}

	public static void WebSocketSend(String Body) {
		SESSION.SEND(Body);

		System.out.println(LOG_PREFIX+"Misskeyに送信:" + Body);
	}
}
