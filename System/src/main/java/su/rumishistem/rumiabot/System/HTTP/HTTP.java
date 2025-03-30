package su.rumishistem.rumiabot.System.HTTP;

import static su.rumishistem.rumiabot.System.Main.CONFIG_DATA;
import static su.rumishistem.rumiabot.System.Main.SH;
import java.io.IOException;
import su.rumishistem.rumi_java_lib.SmartHTTP.SmartHTTP;

public class HTTP {
	public static void Init() throws IOException, InterruptedException {
		SH = new SmartHTTP(CONFIG_DATA.get("HTTP").getData("PORT").asInt());
		SH.Start();
	}
}
