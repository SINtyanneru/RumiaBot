package su.rumishistem.rumiabot.aichan.API;

import static su.rumishistem.rumiabot.System.Main.CONFIG_DATA;

import java.util.HashMap;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

import su.rumishistem.rumi_java_lib.FETCH;
import su.rumishistem.rumi_java_lib.SmartHTTP.HTTP_REQUEST;
import su.rumishistem.rumi_java_lib.SmartHTTP.HTTP_RESULT;
import su.rumishistem.rumi_java_lib.SmartHTTP.Type.EndpointFunction;
import su.rumishistem.rumiabot.aichan.MisskeyAPIModoki;

public class ReversiMatchAPI implements EndpointFunction {
	@Override
	public HTTP_RESULT Run(HTTP_REQUEST r) throws Exception {
		try {
			JsonNode POST_BODY = new ObjectMapper().readTree(r.GetEVENT().getPOST_DATA());
			String UID = POST_BODY.get("userId").asText();

			if (UID.startsWith("M-")) {
				//Misskey
				FETCH Ajax = new FETCH("https://" + CONFIG_DATA.get("MISSKEY").getData("DOMAIN").asString() + "/api/reversi/match");
				Ajax.SetHEADER("Content-Type", "application/json; charset=UTF-8");
				HashMap<String, Object> PostBody = new HashMap<String, Object>();
				PostBody.put("i", CONFIG_DATA.get("MISSKEY").getData("TOKEN").asString());
				PostBody.put("userId", UID.replace("M-", ""));
				JsonNode ReturnBody = new ObjectMapper().readTree(Ajax.POST(new ObjectMapper().writeValueAsBytes(PostBody)).getString());
				if (ReturnBody.get("error") != null) {
					throw new Error("Misskey APIでエラー:" + ReturnBody.get("error"));
				}
			} else {
				//Discord
				throw new Error("Discord未実装");
			}

			return new HTTP_RESULT(204, "".getBytes(), MisskeyAPIModoki.JSONMime);
		} catch (Exception EX) {
			EX.printStackTrace();
			return new HTTP_RESULT(500, "{}".getBytes(), MisskeyAPIModoki.JSONMime);
		}
	}
}
