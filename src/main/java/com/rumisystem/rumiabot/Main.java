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
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Scanner;

public class Main {
    public static String BOT_TOKEN = "";   //DiscordBOTのトークン
    public static Path AppDir;
    public static JDA jda;

    public static void main(String[] args) throws InterruptedException, LoginException {
        try {
            //カレントディレクトリ取得！
            Path p = Paths.get("");
            AppDir = p.toAbsolutePath();

            //設定ファイル読み込み関数実行
            ConfigLoder.main();

            //JDAをこねくり回す
            jda = JDABuilder.createDefault(BOT_TOKEN, GatewayIntent.GUILD_MESSAGES, GatewayIntent.MESSAGE_CONTENT, GatewayIntent.GUILD_MESSAGE_TYPING, GatewayIntent.GUILD_MEMBERS, GatewayIntent.GUILD_EMOJIS_AND_STICKERS)
                    .setRawEventsEnabled(true)
                    .addEventListeners(new DiscordEvent())
                    .setActivity(Activity.playing("そーなのかー"))
                    .setStatus(OnlineStatus.valueOf("ONLINE"))
                    .build();

            jda.awaitReady();

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


            //コマンドを追加 参考：https://jda.wiki/using-jda/interactions/#slash-commands
            jda.updateCommands().addCommands(
                    Commands.slash("test", "テストコマンド"),
                    Commands.slash("update", "アプデ情報"),
                    Commands.slash("tanzania", "タンザニア！"),
                    Commands.slash("shell", "ルーミアシェル")
                            .addOption(OptionType.STRING, "cmd", "コマンド", true),
                    WS_CMD,
                    HELP_CMD,
                    SPAM_CMD,
                    SPAM_STOP_CMD
            ).queue();



            Console console = new Console();
            console.start();

            LOG_OUT("Hello");
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

    public static void SHUTDOWN(){
        System.out.println("[ *** ]APP Shutdowned...");
        jda.shutdown();
        System.exit(0);
    }

    public static void LOG_OUT(String TEXT){
        Console.PRINT("[ INFO ]" + TEXT);
    }
}

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