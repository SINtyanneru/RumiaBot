package su.rumishistem.rumiabot.System.HTTP;

import static su.rumishistem.rumiabot.System.Main.CONFIG_DATA;
import static su.rumishistem.rumiabot.System.Main.SH;
import java.io.IOException;
import java.util.HashMap;

import com.fasterxml.jackson.databind.ObjectMapper;

import su.rumishistem.rumi_java_lib.SmartHTTP.HTTP_REQUEST;
import su.rumishistem.rumi_java_lib.SmartHTTP.HTTP_RESULT;
import su.rumishistem.rumi_java_lib.SmartHTTP.SmartHTTP;
import su.rumishistem.rumi_java_lib.SmartHTTP.Type.EndpointFunction;
import su.rumishistem.rumi_java_lib.SmartHTTP.Type.EndpointEntrie.Method;
import su.rumishistem.rumiabot.System.Discord.DiscordBOT;

public class HTTP {
	public static void Init() throws IOException, InterruptedException {
		SH = new SmartHTTP(CONFIG_DATA.get("HTTP").getData("PORT").asInt());

		SH.SetRoute("/user/api/Count", Method.GET, new EndpointFunction() {
			@Override
			public HTTP_RESULT Run(HTTP_REQUEST r) throws Exception {
				return new HTTP_RESULT(200, new ObjectMapper().writeValueAsString(new HashMap<String, Object>() {{
					put("DISCORD", DiscordBOT.join_guild_count);
				}}).getBytes(), "application/json; charset=UTF-8");
			}
		});

		SH.Start();
	}
}
