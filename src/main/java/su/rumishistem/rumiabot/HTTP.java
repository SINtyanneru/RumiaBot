package su.rumishistem.rumiabot;

import static su.rumishistem.rumiabot.Main.CONFIG_DATA;

import java.io.IOException;
import java.nio.charset.Charset;
import java.util.HashMap;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.common.base.Charsets;
import com.rumisystem.rumi_java_lib.FETCH;
import com.rumisystem.rumi_java_lib.FETCH_RESULT;
import com.rumisystem.rumi_java_lib.HTTP_SERVER.HTTP_EVENT;
import com.rumisystem.rumi_java_lib.HTTP_SERVER.HTTP_EVENT_LISTENER;
import com.rumisystem.rumi_java_lib.HTTP_SERVER.HTTP_SERVER;

import su.rumishistem.rumiabot.Discord.FUNCTION.VERIFY_PANEL;
import su.rumishistem.rumiabot.MODULE.DocumentResource;

public class HTTP {
	public static void Main() throws IOException {
		HTTP_SERVER SERVER = new HTTP_SERVER(3000);
		SERVER.SET_EVENT_VOID(new HTTP_EVENT_LISTENER() {
			@Override
			public void REQUEST_EVENT(HTTP_EVENT REQ) {
				try {
					String PATH = REQ.getEXCHANGE().getRequestURI().getPath();
					switch (PATH) {
						case "/user/discord/verify_panel": {
							String BODY = DocumentResource.GetDocument("/discord_verify_panel.html");
							BODY = BODY.replace("${SITE_KEY}", CONFIG_DATA.get("CAPTCHA").asString("SITE_KEY"));

							//応答
							REQ.setHEADER("Content-Type", "text/html;charset=UTF-8");
							REQ.REPLY_String(200, BODY);
							break;
						}

						case "/user/api/VERIFY_PANEL": {
							if (REQ.getEXCHANGE().getRequestMethod().equals("POST")) {
								JsonNode POST_DATA = new ObjectMapper().readTree(REQ.getPOST_DATA());
								if (POST_DATA.get("CFT_RESULT") == null || POST_DATA.get("VERIFY_ID") == null) {
									REQ.REPLY_String(400, "{\"STATUS\": false}");
								}

								//CFTを検証
								FETCH AJAX = new FETCH("https://challenges.cloudflare.com/turnstile/v0/siteverify");
								AJAX.SetHEADER("Content-Type", "application/json");
								//いざPOST
								HashMap<String, String> AJAX_BODY = new HashMap<String, String>();
								AJAX_BODY.put("response", POST_DATA.get("CFT_RESULT").asText());
								AJAX_BODY.put("secret", CONFIG_DATA.get("CAPTCHA").asString("SIKRET_KEY"));

								FETCH_RESULT CFT_RESULT = AJAX.POST(new ObjectMapper().writeValueAsString(AJAX_BODY).getBytes(Charsets.UTF_8));
								if (new ObjectMapper().readTree(CFT_RESULT.GetString()).get("success").asBoolean()) {
									//精巧
									VERIFY_PANEL.VERIFY(POST_DATA.get("VERIFY_ID").asText());
									REQ.REPLY_String(200, "{\"STATUS\": true}");
								} else {
									//失敗
									REQ.REPLY_String(400, "{\"STATUS\": false}");
								}
							}
							break;
						}

						default: {
							REQ.REPLY_String(404, "404");
						}
					}
				} catch (Exception EX) {
					EX.printStackTrace();
				}
			}
		});
		SERVER.START_HTTPSERVER();
	}
}
