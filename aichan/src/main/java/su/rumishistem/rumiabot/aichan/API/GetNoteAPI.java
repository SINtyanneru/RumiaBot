package su.rumishistem.rumiabot.aichan.API;

import java.util.HashMap;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

import net.dv8tion.jda.api.entities.Message;
import su.rumishistem.rumi_java_lib.FETCH;
import su.rumishistem.rumi_java_lib.FETCH_RESULT;
import su.rumishistem.rumi_java_lib.SmartHTTP.HTTP_REQUEST;
import su.rumishistem.rumi_java_lib.SmartHTTP.HTTP_RESULT;
import su.rumishistem.rumi_java_lib.SmartHTTP.Type.EndpointFunction;
import su.rumishistem.rumiabot.aichan.MisskeyAPIModoki;
import su.rumishistem.rumiabot.aichan.MODULE.ConvertType;
import su.rumishistem.rumiabot.aichan.MODULE.GetDiscordMessage;

public class GetNoteAPI implements EndpointFunction {
	@Override
	public HTTP_RESULT Run(HTTP_REQUEST r) throws Exception {
		try {
			JsonNode PostBody = new ObjectMapper().readTree(r.GetEVENT().getPOST_DATA());
			String NoteID = PostBody.get("noteId").asText();

			if (NoteID.startsWith("M-")) {
				//Misskey
				HashMap<String, Object> APIPostBody = new HashMap<String, Object>();
				APIPostBody.put("i", MisskeyAPIModoki.TOKEN);
				APIPostBody.put("noteId", NoteID.replace("M-", ""));

				FETCH Ajax = new FETCH("https://" + MisskeyAPIModoki.DOMAIN + "/api/notes/show");
				Ajax.SetHEADER("Content-Type", MisskeyAPIModoki.JSONMime);
				FETCH_RESULT Result = Ajax.POST(new ObjectMapper().writeValueAsBytes(APIPostBody));

				return new HTTP_RESULT(Result.GetSTATUS_CODE(), Result.GetString().getBytes(), MisskeyAPIModoki.JSONMime);
			} else if (NoteID.startsWith("D-")) {
				//Discord
				Message TargetMessage = GetDiscordMessage.Get(NoteID);
				if (TargetMessage == null) return new HTTP_RESULT(404, "{}".getBytes(), MisskeyAPIModoki.JSONMime);

				return new HTTP_RESULT(200, new ObjectMapper().writeValueAsString(ConvertType.DiscordMessageToNote(TargetMessage)).getBytes(), MisskeyAPIModoki.JSONMime);
			} else {
				return new HTTP_RESULT(404, "?".getBytes(), MisskeyAPIModoki.JSONMime);
			}
		} catch (Exception EX) {
			EX.printStackTrace();
			return new HTTP_RESULT(500, "{}".getBytes(), MisskeyAPIModoki.JSONMime);
		}
	}
}
