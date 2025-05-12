package su.rumishistem.rumiabot.System;

import static su.rumishistem.rumi_java_lib.LOG_PRINT.Main.LOG;
import static su.rumishistem.rumiabot.System.Main.CommandList;
import static su.rumishistem.rumiabot.System.Main.DISCORD_BOT;

import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.InputStream;
import java.io.PrintStream;
import java.io.StringReader;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

import su.rumishistem.rumi_java_lib.ArrayNode;
import su.rumishistem.rumi_java_lib.CONFIG;
import su.rumishistem.rumi_java_lib.EXCEPTION_READER;
import su.rumishistem.rumi_java_lib.SQL;
import su.rumishistem.rumi_java_lib.HTTP_SERVER.HTTP_SERVER;
import su.rumishistem.rumi_java_lib.LOG_PRINT.LOG_TYPE;
import su.rumishistem.rumi_java_lib.Loger.LogerSystem;
import su.rumishistem.rumi_java_lib.Misskey.MisskeyClient;
import su.rumishistem.rumi_java_lib.REON4213.REON4213Parser;
import su.rumishistem.rumi_java_lib.RESOURCE.RESOURCE_MANAGER;
import su.rumishistem.rumi_java_lib.SmartHTTP.SmartHTTP;
import su.rumishistem.rumiabot.System.Discord.DiscordBOT;
import su.rumishistem.rumiabot.System.HTTP.HTTP;
import su.rumishistem.rumiabot.System.MODULE.AdminManager;
import su.rumishistem.rumiabot.System.TYPE.CommandData;
import su.rumishistem.rumiabot.System.TYPE.CommandOption;
import su.rumishistem.rumiabot.System.TYPE.DiscordChannelFunction;
import su.rumishistem.rumiabot.System.TYPE.DiscordFunction;
import su.rumishistem.rumiabot.System.TYPE.FunctionClass;
import su.rumishistem.rumiabot.System.Telegram.TelegramBot;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.interactions.commands.OptionType;
import net.dv8tion.jda.api.interactions.commands.build.Commands;
import net.dv8tion.jda.api.interactions.commands.build.OptionData;
import net.dv8tion.jda.api.interactions.commands.build.SlashCommandData;

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
