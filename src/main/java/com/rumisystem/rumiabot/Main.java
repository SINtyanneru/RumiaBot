package com.rumisystem.rumiabot;

import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.JDABuilder;
import net.dv8tion.jda.api.OnlineStatus;
import net.dv8tion.jda.api.entities.Activity;
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
            jda = JDABuilder.createDefault(BOT_TOKEN, GatewayIntent.GUILD_MESSAGES, GatewayIntent.GUILD_MESSAGE_TYPING, GatewayIntent.GUILD_MEMBERS, GatewayIntent.GUILD_EMOJIS)
                    .setRawEventsEnabled(true)
                    .addEventListeners(new DiscordEvent())
                    .setActivity(Activity.playing("そーなのかー"))
                    .setStatus(OnlineStatus.valueOf("ONLINE"))
                    .build();

            jda.awaitReady();

            Console console = new Console();
            console.start();

            LOG_OUT("Hello");
        } catch (LoginException e) {
            e.printStackTrace();
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