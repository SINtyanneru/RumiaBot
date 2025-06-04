package su.rumishistem.rumiabot.System;

import static su.rumishistem.rumi_java_lib.LOG_PRINT.Main.LOG;
import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;
import su.rumishistem.rumi_java_lib.ArrayNode;
import su.rumishistem.rumi_java_lib.CONFIG;
import su.rumishistem.rumi_java_lib.SQL;
import su.rumishistem.rumi_java_lib.LOG_PRINT.LOG_TYPE;
import su.rumishistem.rumi_java_lib.Loger.LogerSystem;
import su.rumishistem.rumi_java_lib.Misskey.MisskeyClient;
import su.rumishistem.rumi_java_lib.SmartHTTP.SmartHTTP;
import su.rumishistem.rumiabot.System.Discord.DiscordBOT;
import su.rumishistem.rumiabot.System.HTTP.HTTP;
import su.rumishistem.rumiabot.System.MODULE.AdminManager;
import su.rumishistem.rumiabot.System.MODULE.BlockManager;
import su.rumishistem.rumiabot.System.TYPE.CommandData;
import su.rumishistem.rumiabot.System.TYPE.FunctionClass;
import su.rumishistem.rumiabot.System.Telegram.TelegramBot;
import net.dv8tion.jda.api.JDA;

public class Main {
	public static ArrayNode CONFIG_DATA = null;
	public static JDA DISCORD_BOT = null;
	public static MisskeyClient MisskeyBOT = null;
	public static FunctionModuleLoader FML = null;
	public static List<FunctionClass> FunctionModuleList = new ArrayList<FunctionClass>();
	public static List<CommandData> CommandList = new ArrayList<CommandData>();
	public static SmartHTTP SH = null;
	public static final int MaxLineSize = 25;
	public static String BuildDate = "None";

	public static void main(String[] args) {
		try {
			LogerSystem LS = new LogerSystem();

			LOG(LOG_TYPE.PROCESS, "Config load...");
			if (new File("./Config.ini").exists()) {
				CONFIG_DATA = new CONFIG().DATA;
				LOG(LOG_TYPE.PROCESS_END_OK, "");
			} else {
				LOG(LOG_TYPE.PROCESS_END_FAILED, "Config.ini GA NAI!!!!!!!");
				System.exit(1);
			}

			//ビルド時刻
			try {
				Properties props = new Properties();
				props.load(Main.class.getResourceAsStream("/build.properties"));
				BuildDate = props.getProperty("build.timestamp");
				LOG(LOG_TYPE.INFO, "ビルド時刻:" + BuildDate);
			} catch (Exception EX) {
				EX.printStackTrace();
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

			//管理者マネージャーを初期化する
			AdminManager.Init();

			//ブロックマネージャーを初期化する
			BlockManager.Init();

			//DiscordBOTを起動
			new Thread(new Runnable() {
				@Override
				public void run() {
					try {
						DiscordBOT.Init();
					} catch (Exception EX) {
						EX.printStackTrace();
					}
				}
			}).start();

			//MisskeyBOTを起動
			new Thread(new Runnable() {
				@Override
				public void run() {
					try {
						su.rumishistem.rumiabot.System.Misskey.MisskeyBOTMain.Init();
					} catch (Exception EX) {
						EX.printStackTrace();
					}
				}
			}).start();

			//TelegramBOTを起動
			new Thread(new Runnable() {
				@Override
				public void run() {
					try {
						TelegramBot.Main();
					} catch (Exception EX) {
						EX.printStackTrace();
					}
				}
			}).start();

			//モジュールをロード
			FML = new FunctionModuleLoader();
			FML.Load();
		} catch (Exception EX) {
			EX.printStackTrace();
		}
	}
}
