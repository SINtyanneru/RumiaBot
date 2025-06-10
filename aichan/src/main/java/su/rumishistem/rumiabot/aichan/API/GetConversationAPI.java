package su.rumishistem.rumiabot.aichan.API;

import java.util.HashMap;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

import su.rumishistem.rumi_java_lib.FETCH;
import su.rumishistem.rumi_java_lib.FETCH_RESULT;
import su.rumishistem.rumi_java_lib.SmartHTTP.HTTP_REQUEST;
import su.rumishistem.rumi_java_lib.SmartHTTP.HTTP_RESULT;
import su.rumishistem.rumi_java_lib.SmartHTTP.Type.EndpointFunction;
import su.rumishistem.rumiabot.aichan.MisskeyAPIModoki;

public class GetConversationAPI implements EndpointFunction{
	@Override
	public HTTP_RESULT Run(HTTP_REQUEST r) throws Exception {
		try {
			JsonNode PostBody = new ObjectMapper().readTree(r.GetEVENT().getPOST_DATA());
			String NoteID = PostBody.get("noteId").asText();
			int Limit = 10;
			int Offset = 0;

			if (PostBody.get("limit") != null) {
				Limit = PostBody.get("limit").asInt();
			}

			if (PostBody.get("limit") != null) {
				Offset = PostBody.get("offset").asInt();
			}

			if (NoteID.startsWith("M-")) {
				//Misskey
				HashMap<String, Object> APIPostBody = new HashMap<String, Object>();
				APIPostBody.put("i", MisskeyAPIModoki.TOKEN);
				APIPostBody.put("noteId", NoteID.replace("M-", ""));
				APIPostBody.put("limit", Limit);
				APIPostBody.put("offset", Offset);

				FETCH Ajax = new FETCH("https://" + MisskeyAPIModoki.DOMAIN + "/api/notes/conversation");
				Ajax.SetHEADER("Content-Type", MisskeyAPIModoki.JSONMime);
				FETCH_RESULT Result = Ajax.POST(new ObjectMapper().writeValueAsBytes(APIPostBody));

				return new HTTP_RESULT(Result.GetSTATUS_CODE(), Result.GetString().getBytes(), MisskeyAPIModoki.JSONMime);
			} else if (NoteID.startsWith("D-")) {
				//Discord
				return new HTTP_RESULT(200, "[]".getBytes(), MisskeyAPIModoki.JSONMime);
			} else {
				return new HTTP_RESULT(404, "?".getBytes(), MisskeyAPIModoki.JSONMime);
			}
		} catch (Exception EX) {
			EX.printStackTrace();
			return new HTTP_RESULT(500, "{}".getBytes(), MisskeyAPIModoki.JSONMime);
		}
	}
}
