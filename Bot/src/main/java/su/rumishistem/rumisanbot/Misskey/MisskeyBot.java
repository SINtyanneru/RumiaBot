package su.rumishistem.rumisanbot.Misskey;

import java.io.IOException;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import okhttp3.WebSocket;
import okhttp3.WebSocketListener;
import su.rumishistem.rumi_java_logger.SeverityLevel;
import su.rumishistem.rumisanbot.BaseSystem;
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

	public final String self_user_id;
	public final String self_uid;

	public MisskeyBot(String host, String token, String admin_token) {
		this.host = host;
		this.token = token;
		this.admin_token = admin_token;

		API.set_host(host);
		API.set_token(token);
		API.set_admin_token(admin_token);

		//ログイン
		try {
			JsonNode login = API.run("i", null);
			if (login.get("error") != null) {
				System.out.println(login);
				throw new RuntimeException("ログインできません");
			}

			self_user_id = login.get("id").asText();
			self_uid = login.get("username").asText();
		} catch (Exception e) {
			throw new RuntimeException("ログインできません");
		}

		//WebSocket
		ws_connect();
		admin_ws_connect();

		//ping
		Executors.newSingleThreadScheduledExecutor().scheduleAtFixedRate(new Runnable() {
			@Override
			public void run() {
				try {
					ws.send("h");
					admin_ws.send("h");
				} catch (Exception e) {
					//無視
				}
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

				s.send("{\"type\":\"connect\",\"body\":{\"channel\":\"homeTimeline\",\"id\":\""+STREAM_CHANNNEL_TL+"\",\"params\":{\"withRenotes\":true,\"withReplies\":true}}}");
				s.send("{\"type\":\"connect\",\"body\":{\"channel\":\"localTimeline\",\"id\":\""+STREAM_CHANNNEL_LOCAL_TL+"\",\"params\":{\"withRenotes\":true,\"withReplies\":true}}}");

				Main.logger.print(SeverityLevel.Notice, "MisskeyのWebSocketへ接続しました。");
			}

			//受信
			@Override
			public void onMessage(WebSocket s, String text) {
				try {
					JsonNode body = new ObjectMapper().readTree(text);

					if (body.get("type").asText().equals("channel")) {
						JsonNode data = body.get("body");
						switch (Integer.parseInt(data.get("id").asText())) {
							case STREAM_CHANNNEL_MAIN: {
								if (data.get("type").asText().equals("mention")) {
									JsonNode note = data.get("body");
									BaseSystem.send_event("MISSKEY", "MENTION", new HashMap<>(){{
										put("NOTE_ID", note.get("id").asText());
										put("NOTE_TEXT", note.get("text").asText());

										put("USER_ID", note.get("userId").asText());
										put("USER_UID", note.get("user").get("username").asText());
										if (note.get("user").get("host").isNull()) {
											put("USER_HOST", host);
										} else {
											put("USER_HOST", note.get("user").get("host").asText());
										}

										put("USER_NAME", note.get("user").get("name").asText());
										put("USER_ICON", note.get("user").get("avatarUrl").asText());
									}});
								} else if (data.get("type").asText().equals("followed")) {
									JsonNode user = data.get("body");
									BaseSystem.send_event("MISSKEY", "FOLLOW", new HashMap<>(){{
										put("USER_ID", user.get("id").asText());
										put("USER_UID", user.get("username").asText());
										if (user.get("host").isNull()) {
											put("USER_HOST", host);
										} else {
											put("USER_HOST", user.get("host").asText());
										}

										put("USER_NAME", user.get("name").asText());
										put("USER_ICON", user.get("avatarUrl").asText());
									}});
								} else if (data.get("type").asText().equals("notification") && data.get("body").get("type").asText().equals("unfollow")) {
									JsonNode user = data.get("body").get("user");
									BaseSystem.send_event("MISSKEY", "UNFOLLOW", new HashMap<>(){{
										put("USER_ID", user.get("id").asText());
										put("USER_UID", user.get("username").asText());
										if (user.get("host").isNull()) {
											put("USER_HOST", host);
										} else {
											put("USER_HOST", user.get("host").asText());
										}

										put("USER_NAME", user.get("name").asText());
										put("USER_ICON", user.get("avatarUrl").asText());
									}});
								}
								return;
							}

							case STREAM_CHANNNEL_TL: {
								if (data.get("type").asText().equals("note")) {
									JsonNode note = data.get("body");
									BaseSystem.send_event("MISSKEY", "NOTE", new HashMap<>(){{
										put("NOTE_ID", note.get("id").asText());
										put("NOTE_TEXT", note.get("text").asText());

										put("USER_ID", note.get("userId").asText());
										put("USER_UID", note.get("user").get("username").asText());
										if (note.get("user").get("host").isNull()) {
											put("USER_HOST", host);
											put("IS_LOCAL", true);
										} else {
											put("USER_HOST", note.get("user").get("host").asText());
											put("IS_LOCAL", false);
										}

										put("USER_NAME", note.get("user").get("name").asText());
										put("USER_ICON", note.get("user").get("avatarUrl").asText());
									}});
								}
								return;
							}

							case STREAM_CHANNNEL_LOCAL_TL: {
								if (data.get("type").asText().equals("note")) {
									JsonNode note = data.get("body");
									BaseSystem.send_event("MISSKEY", "LOCAL_NOTE", new HashMap<>(){{
										put("NOTE_ID", note.get("id").asText());
										put("NOTE_TEXT", note.get("text").asText());

										put("USER_ID", note.get("userId").asText());
										put("USER_UID", note.get("user").get("username").asText());
										put("USER_HOST", host);
										put("IS_LOCAL", true);

										put("USER_NAME", note.get("user").get("name").asText());
										put("USER_ICON", note.get("user").get("avatarUrl").asText());
									}});
								}
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
								int deliver_process = jq.get("deliver").get("activeSincePrevTick").asInt();
								int deliver_active = jq.get("deliver").get("active").asInt();
								int deliver_waiting = jq.get("deliver").get("waiting").asInt();
								int deliver_delayed = jq.get("deliver").get("delayed").asInt();

								int inbox_process = jq.get("inbox").get("activeSincePrevTick").asInt();
								int inbox_active = jq.get("inbox").get("active").asInt();
								int inbox_waiting = jq.get("inbox").get("waiting").asInt();
								int inbox_delayed = jq.get("inbox").get("delayed").asInt();

								BaseSystem.send_event("MISSKEY", "JOBQUEUE", new HashMap<String, Object>(){{
									put("DELIVER_PROCESS", deliver_process);
									put("DELIVER_ACTIVE", deliver_active);
									put("DELIVER_WAITING", deliver_waiting);
									put("DELIVER_DELAYED", deliver_delayed);
									put("INBOX_PROCESS", inbox_process);
									put("INBOX_ACTIVE", inbox_active);
									put("INBOX_WAITING", inbox_waiting);
									put("INBOX_DELAYED", inbox_delayed);
								}});
								return;
							}
							case STREAM_CHANNNEL_STATS: {
								JsonNode stats = body.get("body").get("body");
								int cpu_use = (int)Math.round(stats.get("cpu").asDouble() * 100);
								long memory_use = stats.get("mem").get("used").asLong();
								long memory_free = stats.get("mem").get("active").asLong();
								int net_rx = (int)Math.round(stats.get("net").get("rx").asDouble());
								int net_tx = (int)Math.round(stats.get("net").get("tx").asDouble());

								BaseSystem.send_event("MISSKEY", "STATS", new HashMap<String, Object>(){{
									put("CPU_USE", cpu_use);
									put("MEM_USE", memory_use);
									put("MEM_FREE", memory_free);
									put("NET_RX", net_rx);
									put("NET_TX", net_tx);
								}});
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
