package com.rumisystem.rumiabot;

import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.JDABuilder;
import net.dv8tion.jda.api.OnlineStatus;
import net.dv8tion.jda.api.entities.Activity;
import net.dv8tion.jda.api.interactions.commands.Command;
import net.dv8tion.jda.api.requests.GatewayIntent;

import javax.security.auth.login.LoginException;
import java.awt.*;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Random;
import java.util.Scanner;
import java.util.regex.Pattern;

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
            jda = JDABuilder.createDefault(BOT_TOKEN, GatewayIntent.GUILD_MESSAGES, GatewayIntent.DIRECT_MESSAGE_REACTIONS, GatewayIntent.DIRECT_MESSAGES, GatewayIntent.GUILD_MESSAGE_TYPING)
                    .setRawEventsEnabled(true)
                    .addEventListeners(new DiscordEvent()) //追加部分
                    .setActivity(Activity.playing("そーなのかー"))
                    .setStatus(OnlineStatus.valueOf("ONLINE"))
                    .build();

            jda.awaitReady();

            Console console = new Console();
            console.start();
        } catch (LoginException e) {
            e.printStackTrace();
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
    }

    public static Color RUND_COLOR(){
        Color color = null;

        Random rand = new Random();
        int num = (int)Math.ceil(Math.random() * 8);

        switch (num){
            case 1:
                color = new Color(0xFFFC00);
                break;
            case 2:
                color = new Color(0x00FFA6);
                break;
            case 3:
                color = new Color(0x00FFFF);
                break;
            case 4:
                color = new Color(0x008CFF);
                break;
            case 5:
                color = new Color(0xFF0000);
                break;
            case 6:
                color = new Color(0xFF8000);
                break;
            case 7:
                color = new Color(0x2FFF00);
                break;
            case 9:
                color = new Color(0xCE81FF);
                break;
        }

        return color;
    }

    public static void SHUTDOWN(){
        System.out.println("[ *** ]APP Shutdowned...");
        jda.shutdown();
        System.exit(0);
    }

    public static void LOG_OUT(String TEXT){
        System.out.println("\n" + TEXT);
    }
}

class Console extends Thread{
    public void run(){
        while (true){
            System.out.print("RUMIABOT>");

            Scanner sc = new Scanner(System.in);
            String cons_input = sc.nextLine();

            if(cons_input.split(" ")[0].equals("send")){
                Main.jda.getTextChannelById(cons_input.split(" ")[1]).sendMessage(cons_input.split(" ")[2]).queue();
                Main.LOG_OUT("[ CMD ]Send:" + cons_input.split(" ")[1] + "/" + cons_input.split(" ")[2]);

                Console console = new Console();
                console.start();
                break;
            }

            switch (cons_input){
                case "":
                    break;

                case "help":
                    Main.LOG_OUT("stop => BOT Shutdown");
                    break;
                case "stop":
                    Main.SHUTDOWN();
                    break;

                default:
                    Main.LOG_OUT("COMMAND GA NAI!!!!!!!!!!!!");
                    break;
            }
        }
    }
}