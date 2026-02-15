package su.rumishistem.rumisanbot;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Properties;

import su.rumishistem.rumi_java_logger.RumiJavaLogger;
import su.rumishistem.rumi_java_logger.SeverityLevel;

public class Main {
	public static LocalDateTime BUILD_DATE = null;
	public static String JVM_PATH = null;

	public static RumiJavaLogger logger;

	public static void main(String[] args) throws IOException, InterruptedException {
		logger = new RumiJavaLogger();
		logger.hijack_std(SeverityLevel.Informational);

		try {
			Properties p = new Properties();
			p.load(Main.class.getResourceAsStream("/build.properties"));
			BUILD_DATE = LocalDateTime.parse(p.getProperty("build.timestamp"), DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));
		} catch (IOException ex) {
			ex.printStackTrace();
		}

		//すぷらーーーっしゅ
		logger.print(SeverityLevel.Ok, "    ____                  _                  ____        __ ");
		logger.print(SeverityLevel.Ok, "   / __ \\__  ______ ___  (_)________ _____  / __ )____  / /_");
		logger.print(SeverityLevel.Ok, "  / /_/ / / / / __ `__ \\/ / ___/ __ `/ __ \\/ __  / __ \\/ __/");
		logger.print(SeverityLevel.Ok, " / _, _/ /_/ / / / / / / (__  ) /_/ / / / / /_/ / /_/ / /_  ");
		logger.print(SeverityLevel.Ok, "/_/ |_|\\__,_/_/ /_/ /_/_/____/\\__,_/_/ /_/_____/\\____/\\__/  ");
		logger.print(SeverityLevel.Ok, "");
		logger.print(SeverityLevel.Informational, "ﾋﾞﾙﾄﾞ時刻：" + BUILD_DATE.toString());

		//JVMのパスを取得
		JVM_PATH = System.getProperty("java.home") + "/bin/java";
		logger.print(SeverityLevel.Informational, "JVMﾊﾟｽ: " + JVM_PATH);

		//設定をロード
		Config.load();

		//Botのシステムを起動
		Bot.start();

		//BaseSystemロード
		BaseSystem.boot();
	}
}