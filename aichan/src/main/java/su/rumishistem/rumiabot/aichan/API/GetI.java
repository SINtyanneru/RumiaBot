package su.rumishistem.rumiabot.aichan.API;

import java.net.MalformedURLException;

import su.rumishistem.rumi_java_lib.FETCH;
import su.rumishistem.rumi_java_lib.FETCH_RESULT;
import su.rumishistem.rumi_java_lib.SmartHTTP.HTTP_REQUEST;
import su.rumishistem.rumi_java_lib.SmartHTTP.HTTP_RESULT;
import su.rumishistem.rumi_java_lib.SmartHTTP.Type.EndpointFunction;
import su.rumishistem.rumiabot.aichan.MisskeyAPIModoki;

public class GetI implements EndpointFunction {
	@Override
	public HTTP_RESULT Run(HTTP_REQUEST r) throws Exception {
		try {
			FETCH AJAX = new FETCH("https://" + MisskeyAPIModoki.DOMAIN + "/api/i");
			AJAX.SetHEADER("Content-Type", MisskeyAPIModoki.JSONMime);
			FETCH_RESULT RESULT = AJAX.POST(("{\"i\": \"" + MisskeyAPIModoki.TOKEN + "\"}").getBytes());

			return new HTTP_RESULT(200, RESULT.GetRAW(), MisskeyAPIModoki.JSONMime);
		} catch (MalformedURLException EX) {
			return new HTTP_RESULT(500, "{}".getBytes(), MisskeyAPIModoki.JSONMime);
		}
	}
}
