package su.rumishistem.rumiabot.MisskeyReportFucker;

import static su.rumishistem.rumiabot.System.Main.CONFIG_DATA;

import java.net.MalformedURLException;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

import su.rumishistem.rumi_java_lib.FETCH;
import su.rumishistem.rumi_java_lib.FETCH_RESULT;

public class ReportGet {
	private static final String ReportURL = "https://" + CONFIG_DATA.get("MISSKEY").getData("DOMAIN").asString() + "/api/admin/abuse-user-reports";

	public static JsonNode Get(String TOKEN) throws MalformedURLException, JsonMappingException, JsonProcessingException {
		String State = "unresolved";
		//String State = "all";
		String PostBody = "{\"state\":\"" + State + "\",\"reporterOrigin\":\"combined\",\"targetUserOrigin\":\"combined\",\"limit\":10,\"allowPartial\":true,\"i\":\"" + TOKEN + "\"}";
		FETCH AJAX = new FETCH(ReportURL);
		AJAX.SetHEADER("Content-Type", "application/json; charset=UTF-8");
		FETCH_RESULT RESULT = AJAX.POST(PostBody.getBytes());
		JsonNode B = new ObjectMapper().readTree(RESULT.GetString());
		return B;
	}
}
