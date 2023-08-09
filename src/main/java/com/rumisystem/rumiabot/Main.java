package com.rumisystem.rumiabot;

import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.JDABuilder;
import net.dv8tion.jda.api.OnlineStatus;
import net.dv8tion.jda.api.entities.Activity;
import net.dv8tion.jda.api.interactions.DiscordLocale;
import net.dv8tion.jda.api.interactions.commands.Command;
import net.dv8tion.jda.api.interactions.commands.OptionType;
import net.dv8tion.jda.api.interactions.commands.build.CommandData;
import net.dv8tion.jda.api.interactions.commands.build.Commands;
import net.dv8tion.jda.api.interactions.commands.build.OptionData;
import net.dv8tion.jda.api.interactions.commands.build.SlashCommandData;
import net.dv8tion.jda.api.requests.GatewayIntent;

import javax.security.auth.login.LoginException;
import java.awt.*;
import java.io.*;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Base64;
import java.util.Objects;
import java.util.Scanner;

public class Main {
	public static String BOT_TOKEN = "";   //DiscordBOTのトークン
	public static String BOT_ID = "";
	public static String GOOGLE_API_KEY = "";
	public static String GOOGLE_API_ENGINE_ID = "";
	public static String SQL_HOST = "";
	public static String SQL_USER = "";
	public static String SQL_PASS = "";

	public static Path AppDir;
	public static JDA jda;

	public static void main(String[] args) throws InterruptedException, LoginException {
		try {
			//カレントディレクトリ取得！
			Path p = Paths.get("");
			AppDir = p.toAbsolutePath();

			//設定ファイル読み込み関数実行
			ConfigLoder.main();

			LOG_OUT("[ SYSTEM ][ *** ]HTTP Server Stating...");
			WEB_SERVER WS = new WEB_SERVER();
			WS.start();

			LOG_OUT("[ SYSTEM ][ *** ]SQL Client Stating...");
			SQL.Main();

			LOG_OUT("[ SYSTEM ][ *** ]JDA Starting...");
			//JDAをこねくり回す
			jda = JDABuilder.createDefault(BOT_TOKEN, GatewayIntent.GUILD_MESSAGES, GatewayIntent.MESSAGE_CONTENT,
							GatewayIntent.GUILD_MESSAGE_TYPING, GatewayIntent.GUILD_MEMBERS,
							GatewayIntent.GUILD_EMOJIS_AND_STICKERS, GatewayIntent.GUILD_MODERATION)
					.setRawEventsEnabled(true)
					.addEventListeners(new DiscordEvent())
					.setActivity(Activity.playing("そーなのかー"))
					.setStatus(OnlineStatus.valueOf("ONLINE"))
					.build();

			jda.awaitReady();
			LOG_OUT("[ JDA ][ OK ]JDA Started!");

			//ウェブサイトスクショ
			SlashCommandData WS_CMD = Commands.slash("ws", "ウェブサイトのスクショ");
			WS_CMD.setNameLocalization(DiscordLocale.JAPANESE, "ウェブサイトのスクショ")
					.setDescriptionLocalization(DiscordLocale.JAPANESE, "ウェブサイトのスクショ");
			OptionData WS_URL_OP = new OptionData(OptionType.STRING, "url", "URL", true)
					.setNameLocalization(DiscordLocale.JAPANESE, "url")
					.setDescriptionLocalization(DiscordLocale.JAPANESE, "撮影先のURL");
			OptionData WS_BNAME_OP = new OptionData (OptionType.STRING, "browser_name", "ブラウザ名")
					.setNameLocalization(DiscordLocale.JAPANESE, "ブラウザ名")
					.setDescriptionLocalization(DiscordLocale.JAPANESE, "ブラウザ名を指定します");
			Command.Choice WS_BNAME_FIREFOX_CH = new Command.Choice("FireFox", "FireFox")
					.setNameLocalization(DiscordLocale.JAPANESE, "火狐");
			Command.Choice WS_BNAME_RUMI_CH = new Command.Choice("Rumisan", "Rumisan")
					.setNameLocalization(DiscordLocale.JAPANESE, "るみさん㌨Да！");
			Command.Choice WS_BNAME_FLOORP_CH = new Command.Choice("Floorp", "Floorp")
					.setNameLocalization(DiscordLocale.JAPANESE, "Floorp");
			WS_BNAME_OP.addChoices(WS_BNAME_FIREFOX_CH, WS_BNAME_RUMI_CH, WS_BNAME_FLOORP_CH);
			WS_CMD.addOptions(WS_URL_OP, WS_BNAME_OP);

			//ヘルプコマンド
			SlashCommandData HELP_CMD = Commands.slash("help", "ヘルプ");
			HELP_CMD.setNameLocalization(DiscordLocale.JAPANESE, "ヘルプ").setDescriptionLocalization(DiscordLocale.JAPANESE, "コマンドのヘルプです");
			OptionData HELP_PAGE = new OptionData(OptionType.STRING, "page", "ページ")
					.setNameLocalization(DiscordLocale.JAPANESE, "ページ")
					.setDescriptionLocalization(DiscordLocale.JAPANESE, "みたい㌻、特に無いなら指定しなくて良い");

			//スパムコマンド
			SlashCommandData SPAM_CMD = Commands.slash("spam", "ヘルプ");
			SPAM_CMD.setNameLocalization(DiscordLocale.JAPANESE, "スパム").setDescriptionLocalization(DiscordLocale.JAPANESE, "るみさんしか使えない");
			OptionData SPAM_LAT_OPTION = new OptionData(OptionType.STRING, "lat", "文字", true)
					.setNameLocalization(DiscordLocale.JAPANESE, "文字")
					.setDescriptionLocalization(DiscordLocale.JAPANESE, "スパムする文字");
			OptionData SPAM_COUNT_OPTION = new OptionData(OptionType.INTEGER, "count", "回数", true)
					.setNameLocalization(DiscordLocale.JAPANESE, "回数")
					.setDescriptionLocalization(DiscordLocale.JAPANESE, "スパムする回数");
			OptionData SPAM_TIME_OPTION = new OptionData(OptionType.INTEGER, "time", "感覚", true)
					.setNameLocalization(DiscordLocale.JAPANESE, "間隔")
					.setDescriptionLocalization(DiscordLocale.JAPANESE, "どれぐらいの間隔でスパムするか");
			SPAM_CMD.addOptions(SPAM_LAT_OPTION, SPAM_COUNT_OPTION, SPAM_TIME_OPTION);
			//スパムコマンド停止
			SlashCommandData SPAM_STOP_CMD = Commands.slash("spam_stop", "スパム停止");
			SPAM_STOP_CMD.setNameLocalization(DiscordLocale.JAPANESE, "スパム停止").setDescriptionLocalization(DiscordLocale.JAPANESE, "るみさんしか使えない");

			//情報取得
			SlashCommandData INFO_CMD = Commands.slash("info", "Infomation");
			INFO_CMD.setNameLocalization(DiscordLocale.JAPANESE, "情報取得").setDescriptionLocalization(DiscordLocale.JAPANESE, "色んな情報を取得");

			OptionData INFO_SELECT_OPTION = new OptionData(OptionType.STRING, "select", "You are nani wo get sulu!!", true)
					.setNameLocalization(DiscordLocale.JAPANESE, "種類")
					.setDescriptionLocalization(DiscordLocale.JAPANESE, "なにを取得するか");

			OptionData INFO_USER_OPTION = new OptionData(OptionType.USER, "users", "user dao")
					.setNameLocalization(DiscordLocale.JAPANESE, "ユーザー")
					.setDescriptionLocalization(DiscordLocale.JAPANESE, "取得するユーザー");

			Command.Choice INFO_SELECT_SERVER_CH = new Command.Choice("Server", "server")
					.setNameLocalization(DiscordLocale.JAPANESE, "サーバー");

			Command.Choice INFO_SELECT_USER_CH = new Command.Choice("User", "user")
					.setNameLocalization(DiscordLocale.JAPANESE, "ユーザー");

			INFO_SELECT_OPTION.addChoices(INFO_SELECT_SERVER_CH, INFO_SELECT_USER_CH);
			INFO_CMD.addOptions(INFO_SELECT_OPTION, INFO_USER_OPTION);

			//情報取得
			SlashCommandData RS_GET_CMD = Commands.slash("rsget", "RumiServer Get");
			INFO_CMD.setNameLocalization(DiscordLocale.JAPANESE, "るみ鯖のユーザー情報取得").setDescriptionLocalization(DiscordLocale.JAPANESE, "るみ鯖でのユーザーの、情報を取得します");
			OptionData RS_GET_CMD_UID_OPTION = new OptionData(OptionType.STRING, "uid", "user id", true)
					.setNameLocalization(DiscordLocale.JAPANESE, "ユーザーアイディー")
					.setDescriptionLocalization(DiscordLocale.JAPANESE, "誰を取得するか");
			RS_GET_CMD.addOptions(RS_GET_CMD_UID_OPTION);

			//鯖バックアップ
			SlashCommandData SERVER_BACKUP_CMD = Commands.slash("backup", "backUp");
			SERVER_BACKUP_CMD.setNameLocalization(DiscordLocale.JAPANESE, "サーバーバックアップ").setDescriptionLocalization(DiscordLocale.JAPANESE, "バックアップするのだ");

			//コマンドを追加 参考：https://jda.wiki/using-jda/interactions/#slash-commands
			jda.updateCommands().addCommands(
					Commands.slash("test", "テストコマンド"),
					Commands.slash("invite", "招待コード生成"),
					Commands.slash("update", "アプデ情報"),
					Commands.slash("tanzania", "タンザニア！"),
					Commands.slash("shell", "ルーミアシェル")
							.addOption(OptionType.STRING, "cmd", "コマンド", true),
					WS_CMD,
					HELP_CMD,
					SPAM_CMD,
					SPAM_STOP_CMD,
					INFO_CMD,
					RS_GET_CMD,
					SERVER_BACKUP_CMD
			).queue();
			LOG_OUT("[ JDA ][ OK ]JDAで、N以上のコマンドを登録しました！");

			LOG_OUT("[ SYSTEM ][ OK ]起動完了");
		} catch (InterruptedException e) {
			throw new RuntimeException(e);
		}
	}

	public static Color RUND_COLOR(){
		Color color = null;

		int num = (int)Math.ceil(Math.random() * 8);

		switch (num) {
			case 1 -> color = new Color(0xFFFC00);
			case 2 -> color = new Color(0x00FFA6);
			case 3 -> color = new Color(0x00FFFF);
			case 4 -> color = new Color(0x008CFF);
			case 5 -> color = new Color(0xFF0000);
			case 6 -> color = new Color(0xFF8000);
			case 7 -> color = new Color(0x2FFF00);
			case 9 -> color = new Color(0xCE81FF);
		}

		return color;
	}

	public static String BASE64_DECODE(String TEXT){
		// Base64デコード
		byte[] decodedBytes = Base64.getDecoder().decode(TEXT);

		// デコードされたバイト列をUTF-8文字列に変換
		String decodedString = new String(decodedBytes, StandardCharsets.UTF_8);

		return decodedString;
	}


	public static void SHUTDOWN(){
		System.out.println("[ *** ]APP Shutdowned...");
		jda.shutdown();
		System.exit(0);
	}

	public static void LOG_OUT(String TEXT){
		System.out.print("[ INFO ]" + TEXT + "\n");
	}
}

/*
class Console extends Thread{
    public void run(){
        System.out.print("\033[H\033[2J");
        System.out.flush();

        Scanner scanner = new Scanner(System.in);
        while(true){

            String CMD = scanner.nextLine();

            PRINT(CMD);

            if(CMD.split(" ")[0].equals("send")){
                Main.jda.getTextChannelById(CMD.split(" ")[1]).sendMessage(CMD.split(" ")[2]).queue();
                Main.LOG_OUT("Send:" + CMD.split(" ")[1] + "/" + CMD.split(" ")[2]);
            }

            switch (CMD){
                case "exit":
                    Main.SHUTDOWN();
                    scanner.close();
                    break;
            }
        }
    }

    public static void PRINT(String TEXT) {
        System.out.print("\n"); // 改行
        System.out.print("\033[1A"); // 1行上に移動
        System.out.print("\033[2K"); // 現在の行をクリア
        System.out.flush();//ANSIエスケープシーケンスで必ず出てくる謎の関数
        System.out.print(TEXT + "\n");

        CONSOLE_TEXT();
    }

    public static void CONSOLE_TEXT(){
        System.out.print(">");
    }


}
 */