package su.rumishistem.rumiabot.System.Telegram;

import static su.rumishistem.rumiabot.System.Main.SH;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

import su.rumishistem.rumi_java_lib.SmartHTTP.HTTP_REQUEST;
import su.rumishistem.rumi_java_lib.SmartHTTP.HTTP_RESULT;
import su.rumishistem.rumi_java_lib.SmartHTTP.Type.EndpointFunction;
import su.rumishistem.rumi_java_lib.SmartHTTP.Type.EndpointEntrie.Method;

public class TelegramBot {
	public static void Main() {
		SH.SetRoute("/telegramwh", Method.POST, new EndpointFunction() {
			@Override
			public HTTP_RESULT Run(HTTP_REQUEST r) throws Exception {
				JsonNode Body = new ObjectMapper().readTree(r.GetEVENT().getPOST_DATA());

				System.out.println(Body);

				return new HTTP_RESULT(200, new byte[] {}, "text/plain");
			}
		});
	}
}
