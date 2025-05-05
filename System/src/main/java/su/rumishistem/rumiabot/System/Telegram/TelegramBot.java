package su.rumishistem.rumiabot.System.Telegram;

import static su.rumishistem.rumiabot.System.Main.SH;
import su.rumishistem.rumi_java_lib.SmartHTTP.HTTP_REQUEST;
import su.rumishistem.rumi_java_lib.SmartHTTP.HTTP_RESULT;
import su.rumishistem.rumi_java_lib.SmartHTTP.Type.EndpointFunction;
import su.rumishistem.rumi_java_lib.SmartHTTP.Type.EndpointEntrie.Method;

public class TelegramBot {
	public static void Main() {
		SH.SetRoute("/telegramwh", Method.POST, new EndpointFunction() {
			@Override
			public HTTP_RESULT Run(HTTP_REQUEST r) throws Exception {
				System.out.println(r.GetEVENT().getPOST_DATA());
				return new HTTP_RESULT(200, new byte[] {}, "text/plain");
			}
		});
	}
}
