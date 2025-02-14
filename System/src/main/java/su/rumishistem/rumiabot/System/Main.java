package su.rumishistem.rumiabot.System;

import static su.rumishistem.rumi_java_lib.LOG_PRINT.Main.LOG;
import java.io.File;
import java.util.ArrayList;
import java.util.List;

import su.rumishistem.rumi_java_lib.ArrayNode;
import su.rumishistem.rumi_java_lib.CONFIG;
import su.rumishistem.rumi_java_lib.SQL;
import su.rumishistem.rumi_java_lib.HTTP_SERVER.HTTP_SERVER;
import su.rumishistem.rumi_java_lib.LOG_PRINT.LOG_TYPE;
import su.rumishistem.rumi_java_lib.Misskey.MisskeyClient;
import su.rumishistem.rumi_java_lib.SmartHTTP.SmartHTTP;
import su.rumishistem.rumiabot.System.Discord.DiscordBOT;
import su.rumishistem.rumiabot.System.HTTP.HTTP;
import su.rumishistem.rumiabot.System.TYPE.CommandData;
import su.rumishistem.rumiabot.System.TYPE.FunctionClass;
import net.dv8tion.jda.api.JDA;

public class Main {
	public static ArrayNode CONFIG_DATA = null;
	public static JDA DISCORD_BOT = null;
	public static MisskeyClient MisskeyBOT = null;
	public static FunctionModuleLoader FML = null;
	public static List<FunctionClass> FunctionModuleList = new ArrayList<FunctionClass>();
	public static List<CommandData> CommandList = new ArrayList<CommandData>();
	public static SmartHTTP SH = null;

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

			//HTTPサーバー起動
			new Thread(new Runnable() {
				@Override
				public void run() {
					try {
						HTTP.Init();
					} catch (Exception EX) {
						EX.printStackTrace();
						LOG(LOG_TYPE.FAILED, "HTTPサーバー起動失敗");
						System.exit(1);
					}
				}
			}).start();

			//モジュールをロード
			FML = new FunctionModuleLoader();
			FML.Load();

			//DiscordBOTを起動
			DiscordBOT.Init();

			//MisskeyBOTを起動
			su.rumishistem.rumiabot.System.Misskey.MisskeyBOTMain.Init();
		} catch (Exception EX) {
			EX.printStackTrace();
		}
	}
}
