package su.rumishistem.rumiabot.aichan.API;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

import su.rumishistem.rumi_java_lib.SmartHTTP.HTTP_REQUEST;
import su.rumishistem.rumi_java_lib.SmartHTTP.HTTP_RESULT;
import su.rumishistem.rumi_java_lib.SmartHTTP.Type.EndpointFunction;
import su.rumishistem.rumiabot.aichan.MisskeyAPIModoki;
import su.rumishistem.rumiabot.aichan.SERVICE.CreateReaction;

public class CreateReactionAPI implements EndpointFunction {
	@Override
	public HTTP_RESULT Run(HTTP_REQUEST r) throws Exception {
		try {
			JsonNode POST_BODY = new ObjectMapper().readTree(r.GetEVENT().getPOST_DATA());
			String Reaction = POST_BODY.get("reaction").asText();
			String NoteID = POST_BODY.get("noteId").asText();

			CreateReaction.Create(Reaction, NoteID);

			return new HTTP_RESULT(200, "{}".getBytes(), MisskeyAPIModoki.JSONMime);
		} catch (Exception EX) {
			EX.printStackTrace();
			return new HTTP_RESULT(500, "{}".getBytes(), MisskeyAPIModoki.JSONMime);
		}
	}
}
