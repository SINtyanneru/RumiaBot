package su.rumishistem.rumiabot.System;

import static su.rumishistem.rumi_java_lib.LOG_PRINT.Main.LOG;

import java.io.File;
import java.util.Properties;

import su.rumishistem.rumi_java_lib.ArrayNode;
import su.rumishistem.rumi_java_lib.CONFIG;
import su.rumishistem.rumi_java_lib.LOG_PRINT.LOG_TYPE;
import su.rumishistem.rumi_java_lib.Loger.LogerSystem;
import su.rumishistem.rumiabot.System.Misskey.MisskeyBot;

public class Main {
	public static ArrayNode config = null;

	private static String build_date = "None";
	private static MisskeyBot misskey_bot;

	public static void main(String[] args) {
		try {
			LogerSystem LS = new LogerSystem();

			LOG(LOG_TYPE.INFO, "    ____                  _       ____  ____  ______");
			LOG(LOG_TYPE.INFO, "   / __ \\__  ______ ___  (_)___ _/ __ )/ __ \\/_  __/");
			LOG(LOG_TYPE.INFO, "  / /_/ / / / / __ `__ \\/ / __ `/ __  / / / / / /   ");
			LOG(LOG_TYPE.INFO, " / _, _/ /_/ / / / / / / / /_/ / /_/ / /_/ / / /    ");
			LOG(LOG_TYPE.INFO, "/_/ |_|\\__,_/_/ /_/ /_/_/\\__,_/_____/\\____/ /_/     ");

			LOG(LOG_TYPE.PROCESS, "Config load...");
			if (new File("./Config.ini").exists()) {
				config = new CONFIG().DATA;
				LOG(LOG_TYPE.PROCESS_END_OK, "");
			} else {
				LOG(LOG_TYPE.PROCESS_END_FAILED, "Config.ini GA NAI!!!!!!!");
				System.exit(1);
			}

			//ビルド時刻
			try {
				Properties props = new Properties();
				props.load(Main.class.getResourceAsStream("/build.properties"));
				build_date = props.getProperty("build.timestamp");
				LOG(LOG_TYPE.INFO, "ビルド時刻:" + build_date);
			} catch (Exception EX) {
				EX.printStackTrace();
			}

			//スレッドプール
			ThreadPool.init();

			//BOT
			LOG(LOG_TYPE.PROCESS, "MisskeyBotを起動しています...");
			misskey_bot = new MisskeyBot(config.get("MISSKEY").getData("DOMAIN").asString(), config.get("MISSKEY").getData("TOKEN").asString());
			LOG(LOG_TYPE.PROCESS_END_OK, "");

			FunctionLoader.load();
		} catch (Exception ex) {
			ex.printStackTrace();
			System.exit(1);
		}
	}

	public static String get_build_date() {
		return build_date;
	}

	public static MisskeyBot get_misskey_bot() {
		return misskey_bot;
	}
}
