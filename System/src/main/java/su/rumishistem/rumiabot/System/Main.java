package su.rumishistem.rumiabot.System;

import static su.rumishistem.rumi_java_lib.LOG_PRINT.Main.LOG;

import java.io.File;
import java.util.Properties;

import su.rumishistem.rumi_java_lib.ArrayNode;
import su.rumishistem.rumi_java_lib.CONFIG;
import su.rumishistem.rumi_java_lib.SQL;
import su.rumishistem.rumi_java_lib.LOG_PRINT.LOG_TYPE;
import su.rumishistem.rumi_java_lib.Loger.LogerSystem;
import su.rumishistem.rumiabot.System.Discord.DiscordBot;
import su.rumishistem.rumiabot.System.HTTP.HTTPServer;
import su.rumishistem.rumiabot.System.Misskey.MisskeyBot;

public class Main {
	public static ArrayNode config = null;

	private static String build_date = "None";
	private static HTTPServer http_server;
	private static MisskeyBot misskey_bot;
	private static DiscordBot discord_bot;

	public static void main(String[] args) {
		try {
			/*LogerSystem LS = */new LogerSystem();

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

			SQL.CONNECT(
				config.get("SQL").getData("HOST").asString(),
				config.get("SQL").getData("PORT").asString(),
				config.get("SQL").getData("DB").asString(),
				config.get("SQL").getData("USER").asString(),
				config.get("SQL").getData("PASS").asString()
			);

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

			//HTTP起動
			http_server = new HTTPServer(config.get("HTTP").getData("PORT").asInt());

			//BOT
			LOG(LOG_TYPE.PROCESS, "MisskeyBotを起動しています...");
			misskey_bot = new MisskeyBot(config.get("MISSKEY").getData("DOMAIN").asString(), config.get("MISSKEY").getData("TOKEN").asString());
			LOG(LOG_TYPE.PROCESS_END_OK, "");

			LOG(LOG_TYPE.PROCESS, "DiscordBotを起動しています...");
			discord_bot = new DiscordBot(config.get("DISCORD").getData("TOKEN").asString());
			LOG(LOG_TYPE.PROCESS_END_OK, "");

			//機能ロード
			FunctionLoader.load();

			//色々起動
			http_server.start();
		} catch (Exception ex) {
			ex.printStackTrace();
			System.exit(1);
		}
	}

	public static String get_build_date() {
		return build_date;
	}

	public static HTTPServer get_http() {
		return http_server;
	}

	public static MisskeyBot get_misskey_bot() {
		return misskey_bot;
	}

	public static DiscordBot get_discord_bot() {
		return discord_bot;
	}
}
