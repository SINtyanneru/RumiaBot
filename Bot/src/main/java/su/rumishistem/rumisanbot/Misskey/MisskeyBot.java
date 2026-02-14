package su.rumishistem.rumisanbot.Misskey;

import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import okhttp3.WebSocket;
import okhttp3.WebSocketListener;
import okio.ByteString;
import su.rumishistem.rumi_java_logger.SeverityLevel;
import su.rumishistem.rumisanbot.Bot;
import su.rumishistem.rumisanbot.Main;

public class MisskeyBot {
	private static final int STREAM_CHANNNEL_MAIN = 100;
	private static final int STREAM_CHANNNEL_JOBQUEUE = 200;
	private static final int STREAM_CHANNNEL_STATS = 250;
	private static final int STREAM_CHANNNEL_TL = 300;
	private static final int STREAM_CHANNNEL_LOCAL_TL = 400;

	private final OkHttpClient http_client = new OkHttpClient.Builder().readTimeout(0, TimeUnit.MILLISECONDS).build();
	private WebSocket ws;
	private WebSocket admin_ws;

	private final String host;
	private final String token;
	private final String admin_token;

	public MisskeyBot(String host, String token, String admin_token) {
		this.host = host;
		this.token = token;
		this.admin_token = admin_token;

		//WebSocket
		ws_connect();
		admin_ws_connect();

		//ping
		Executors.newSingleThreadScheduledExecutor().scheduleAtFixedRate(new Runnable() {
			@Override
			public void run() {
				ws.send("h");
				admin_ws.send("h");
			}
		}, 30, 30, TimeUnit.SECONDS);

		Bot.misskey_ready = true;
	}

	private void ws_connect() {
		Request request = new Request.Builder().url("wss://"+host+"/streaming?i=" + URLEncoder.encode(token, StandardCharsets.UTF_8)).build();
		ws = http_client.newWebSocket(request, new WebSocketListener() {
			//切断
			@Override
			public void onOpen(WebSocket s, Response response) {
				s.send("{\"type\":\"connect\",\"body\":{\"channel\":\"main\",\"id\":\""+STREAM_CHANNNEL_MAIN+"\"}}");

				s.send("{\"type\":\"connect\",\"body\":{\"channel\":\"homeTimeline\",\"id\":\""+STREAM_CHANNNEL_TL+"\",\"params\":{\"withRenotes\":true,\"withReplies\":false}}}");
				s.send("{\"type\":\"connect\",\"body\":{\"channel\":\"localTimeline\",\"id\":\""+STREAM_CHANNNEL_LOCAL_TL+"\",\"params\":{\"withRenotes\":true,\"withReplies\":false}}}");

				Main.logger.print(SeverityLevel.Notice, "MisskeyのWebSocketへ接続しました。");
			}

			//受信
			@Override
			public void onMessage(WebSocket s, String text) {
				try {
					JsonNode body = new ObjectMapper().readTree(text);

					if (body.get("type").asText().equals("channel")) {
						switch (Integer.parseInt(body.get("body").get("id").asText())) {
							case STREAM_CHANNNEL_MAIN: {
								return;
							}

							case STREAM_CHANNNEL_TL: {
								return;
							}

							case STREAM_CHANNNEL_LOCAL_TL: {
								return;
							}
						}
					}
				} catch (Exception ex) {
					ex.printStackTrace();
				}
			}

			//切断
			@Override
			public void onClosed(WebSocket s, int code, String reason) { admin_ws_connect(); }
			@Override
			public void onFailure(WebSocket s, Throwable t, Response response) { admin_ws_connect(); }
		});
	}

	private void admin_ws_connect() {
		Request request = new Request.Builder().url("wss://"+host+"/streaming?i=" + URLEncoder.encode(admin_token, StandardCharsets.UTF_8)).build();
		admin_ws = http_client.newWebSocket(request, new WebSocketListener() {
			//切断
			@Override
			public void onOpen(WebSocket s, Response response) {
				//↓なぜadminじゃないと流れてこない？？(Firefox/Chromeで見ると一般ユーザーでも流れる)
				s.send("{\"type\":\"connect\",\"body\":{\"channel\":\"queueStats\",\"id\":\""+STREAM_CHANNNEL_JOBQUEUE+"\"}}");
				s.send("{\"type\":\"connect\",\"body\":{\"channel\":\"serverStats\",\"id\":\""+STREAM_CHANNNEL_STATS+"\"}}");
				Main.logger.print(SeverityLevel.Notice, "管理者としてMisskeyのWebSocketへ接続しました。");
			}

			//受信
			@Override
			public void onMessage(WebSocket s, String text) {
				try {
					JsonNode body = new ObjectMapper().readTree(text);

					if (body.get("type").asText().equals("channel")) {
						switch (Integer.parseInt(body.get("body").get("id").asText())) {
							case STREAM_CHANNNEL_JOBQUEUE: {
								JsonNode jq = body.get("body").get("body");
								System.out.println("Deliver");
								System.out.println("  Process : " + jq.get("deliver").get("activeSincePrevTick").asInt());
								System.out.println("  Active  : " + jq.get("deliver").get("active").asInt());
								System.out.println("  Waiting : " + jq.get("deliver").get("waiting").asInt());
								System.out.println("  Delayed : " + jq.get("deliver").get("delayed").asInt());

								System.out.println("Inbox");
								System.out.println("  Process : " + jq.get("inbox").get("activeSincePrevTick").asInt());
								System.out.println("  Active  : " + jq.get("inbox").get("active").asInt());
								System.out.println("  Waiting : " + jq.get("inbox").get("waiting").asInt());
								System.out.println("  Delayed : " + jq.get("inbox").get("delayed").asInt());
								return;
							}
							case STREAM_CHANNNEL_STATS: {
								JsonNode stats = body.get("body").get("body");
								System.out.println("CPU: " + Math.round(stats.get("cpu").asDouble() * 100));
								System.out.println("MEM: " + (stats.get("mem").get("used").asInt() + stats.get("mem").get("active").asInt()) + "\\" + stats.get("mem").get("used").asInt());
								System.out.println("NET: RX[" + Math.round(stats.get("net").get("rx").asDouble()) + "]");
								System.out.println("NET: TX[" + Math.round(stats.get("net").get("tx").asDouble()) + "]");
								return;
							}
						}
					}
				} catch (Exception ex) {
					ex.printStackTrace();
				}
			}

			//切断
			@Override
			public void onClosed(WebSocket s, int code, String reason) { admin_ws_connect(); }
			@Override
			public void onFailure(WebSocket s, Throwable t, Response response) { admin_ws_connect(); }
		});
	}
}
