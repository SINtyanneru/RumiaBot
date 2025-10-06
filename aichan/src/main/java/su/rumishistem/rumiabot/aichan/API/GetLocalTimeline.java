package su.rumishistem.rumiabot.aichan.API;

import java.util.HashMap;

import com.fasterxml.jackson.databind.ObjectMapper;

import su.rumishistem.rumi_java_lib.FETCH;
import su.rumishistem.rumi_java_lib.FETCH_RESULT;
import su.rumishistem.rumi_java_lib.SmartHTTP.HTTP_REQUEST;
import su.rumishistem.rumi_java_lib.SmartHTTP.HTTP_RESULT;
import su.rumishistem.rumi_java_lib.SmartHTTP.Type.EndpointFunction;
import su.rumishistem.rumiabot.aichan.MisskeyAPIModoki;

public class GetLocalTimeline implements EndpointFunction{
	@Override
	public HTTP_RESULT Run(HTTP_REQUEST r) throws Exception {
		HashMap<String, Object> APIPostBody = new HashMap<String, Object>();
		APIPostBody.put("i", MisskeyAPIModoki.TOKEN);

		FETCH Ajax = new FETCH("https://" + MisskeyAPIModoki.DOMAIN + "/api/notes/local-timeline");
		Ajax.SetHEADER("Content-Type", MisskeyAPIModoki.JSONMime);
		FETCH_RESULT Result = Ajax.POST(new ObjectMapper().writeValueAsBytes(APIPostBody));

		return new HTTP_RESULT(Result.getStatusCode(), Result.getString().getBytes(), MisskeyAPIModoki.JSONMime);
	}
}
