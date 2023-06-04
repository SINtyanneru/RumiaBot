package com.rumisystem.rumiabot.Command;

import com.rumisystem.rumiabot.Main;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;

import java.util.Objects;

public class help {
    public static void main(MessageReceivedEvent e){
        try{
            if(e.getMessage().getContentRaw().split(" ").length == 1){
                //JAVAの仕様で無い配列を指定するとエラルので、ここでヘルプのホームを出す
                HELP_HOME(e);
            }
            String HELP_MODE = e.getMessage().getContentRaw().split(" ")[1];
            System.out.println(HELP_MODE);
            switch (HELP_MODE.toUpperCase()){
                case "SETCH":
                    //ヘルプのSETCHを出す
                    HELP_SETCH(e);
                    break;
                case "GAME":
                    //ヘルプのGAMEを出す
                    HELP_GAME(e);
                    break;

                default:
                    //ヘルプのホームを出す
                    HELP_HOME(e);
                    break;
            }
        }catch(Exception ex){

        }
    }

    public static void HELP_HOME(MessageReceivedEvent e){
        // Create the EmbedBuilder instance
        EmbedBuilder eb = new EmbedBuilder();   //埋め込みのやつを簡単に作れるツール(Discord.JSにはない！！神！！JAVA先生一生ついていきます！！)
        eb.setTitle("コマンドヘルプ", null);     //タイトル
        eb.setColor(Main.RUND_COLOR());   //色設定
        eb.setDescription("プレフィクスは「r.」です\n\n\n\n");
        eb.addField("test", "テストコマンドです。\n使い方：r.test", false);
        eb.addField("setch", "実行したチャンネルを、会話チャンネルやログチャンネル、挨拶チャンネルとして設定します。\n使い方：r.setch [チャンネルタイプ]\n詳細は「r.help setch」で御覧ください。", false);
        eb.addField("GAME", "「r.help GAME」をご覧ください", false);
        e.getMessage().replyEmbeds(eb.build()).queue();
    }

    public static void HELP_SETCH(MessageReceivedEvent e){
        // Create the EmbedBuilder instance
        EmbedBuilder eb = new EmbedBuilder();   //埋め込みのやつを簡単に作れるツール(Discord.JSにはない！！神！！JAVA先生一生ついていきます！！)
        eb.setTitle("コマンドヘルプ / setch", null);     //タイトル
        eb.setColor(Main.RUND_COLOR());   //色設定
        eb.setDescription("setchコマンドのヘルプです。\nこのコマンドは、実行したチャンネルを、会話チャンネルやログチャンネル、挨拶チャンネルとして設定します。\n使い方：r.setch [チャンネルタイプ]\n以下がチャンネルタイプです。\n\n\n\n");
        eb.addField("TALK", "会話チャンネルです、ルーミアちゃんが反応してくれます(いろいろ回数制限等掛けてます)", false);
        eb.addField("LOG", "ログ出力ちゃんねるです、チャンネルを消したりメッセージを消すとログを出してくれます。", false);
        e.getMessage().replyEmbeds(eb.build()).queue();
    }

    public static void HELP_GAME(MessageReceivedEvent e){
        // Create the EmbedBuilder instance
        EmbedBuilder eb = new EmbedBuilder();   //埋め込みのやつを簡単に作れるツール(Discord.JSにはない！！神！！JAVA先生一生ついていきます！！)
        eb.setTitle("ゲーム", null);     //タイトル
        eb.setColor(Main.RUND_COLOR());   //色設定
        eb.setDescription("ルーミアちゃんBOTのゲームです");
        eb.addField("国旗当てゲーム", "国旗を当てるゲームです、出された国旗を見て国名を当ててください。\nやり方「r.国旗当てゲーム START レベル」\nレベルは1〜5です。", false);
        e.getMessage().replyEmbeds(eb.build()).queue();
    }
}
