package su.rumishistem.rumiabot.System;

import static su.rumishistem.rumi_java_lib.LOG_PRINT.Main.LOG;

import java.io.File;
import java.io.PrintStream;

import su.rumishistem.rumi_java_lib.ArrayNode;
import su.rumishistem.rumi_java_lib.CONFIG;
import su.rumishistem.rumi_java_lib.SQL;
import su.rumishistem.rumi_java_lib.LOG_PRINT.LOG_TYPE;
import su.rumishistem.rumi_java_lib.Misskey.MisskeyClient;
import net.dv8tion.jda.api.JDA;

public class Main {
	public static ArrayNode CONFIG_DATA = null;
	public static JDA DISCORD_BOT = null;
	public static MisskeyClient MisskeyBOT = null;

	public static void main(String[] args) {
		try {
			LOG(LOG_TYPE.INFO, "    ____                  _       ____  ____  ______    ");
			LOG(LOG_TYPE.INFO, "   / __ \\__  ______ ___  (_)___ _/ __ )/ __ \\/_  __/  ");
			LOG(LOG_TYPE.INFO, "  / /_/ / / / / __ `__ \\/ / __ `/ __  / / / / / /      ");
			LOG(LOG_TYPE.INFO, " / _, _/ /_/ / / / / / / / /_/ / /_/ / /_/ / / /        ");
			LOG(LOG_TYPE.INFO, "/_/ |_|\\__,_/_/ /_/ /_/_/\\__,_/_____/\\____/ /_/      ");
			LOG(LOG_TYPE.INFO, "V1.1");

			LOG(LOG_TYPE.PROCESS, "Config load...");
			if (new File("./Config.ini").exists()) {
				CONFIG_DATA = new CONFIG().DATA;
				LOG(LOG_TYPE.PROCESS_END_OK, "");
			} else {
				LOG(LOG_TYPE.PROCESS_END_FAILED, "Config.ini GA NAI!!!!!!!");
				System.exit(1);
			}

			//SQL用意
			SQL.CONNECT(
				CONFIG_DATA.get("SQL").getData("HOST").asString(),
				CONFIG_DATA.get("SQL").getData("PORT").asString(),
				CONFIG_DATA.get("SQL").getData("DB").asString(),
				CONFIG_DATA.get("SQL").getData("USER").asString(),
				CONFIG_DATA.get("SQL").getData("PASS").asString()
			);
		} catch (Exception EX) {
			EX.printStackTrace();
		}
	}
}
