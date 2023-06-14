package com.rumisystem.rumiabot.Command;

import com.rumisystem.rumiabot.Main;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;

import java.util.Objects;

public class help {
    public static void Main(SlashCommandInteractionEvent e){
        try{
            if(Objects.isNull(e.getInteraction().getOption("page"))){
                //JAVAの仕様で無い配列を指定するとエラルので、ここでヘルプのホームを出す
                HELP_HOME(e);
            }else {
                String HELP_MODE = e.getInteraction().getOption("page").getAsString().toUpperCase();
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
            }
        }catch(Exception ex){
            Main.LOG_OUT(ex.getMessage());
            e.getInteraction().reply("エラー：" + ex.getMessage()).queue();
        }
    }

    public static void HELP_HOME(SlashCommandInteractionEvent e){
        // Create the EmbedBuilder instance
        EmbedBuilder eb = new EmbedBuilder();   //埋め込みのやつを簡単に作れるツール(Discord.JSにはない！！神！！JAVA先生一生ついていきます！！)
        eb.setTitle("コマンドヘルプ", null);     //タイトル
        eb.setColor(Main.RUND_COLOR());   //色設定
        eb.setDescription("プレフィクスは「r.」です\n\n\n\n");
        eb.addField("test", "テストコマンドです。\n使い方：r.test", false);
        eb.addField("setch", "実行したチャンネルを、会話チャンネルやログチャンネル、挨拶チャンネルとして設定します。\n使い方：/setch [チャンネルタイプ]\n詳細は「setch」で御覧ください。", false);
        eb.addField("GAME", "「GAME」をご覧ください", false);

        e.getInteraction().replyEmbeds(eb.build()).queue();
    }

    public static void HELP_SETCH(SlashCommandInteractionEvent e){
        // Create the EmbedBuilder instance
        EmbedBuilder eb = new EmbedBuilder();   //埋め込みのやつを簡単に作れるツール(Discord.JSにはない！！神！！JAVA先生一生ついていきます！！)
        eb.setTitle("コマンドヘルプ / setch", null);     //タイトル
        eb.setColor(Main.RUND_COLOR());   //色設定
        eb.setDescription("setchコマンドのヘルプです。\nこのコマンドは、実行したチャンネルを、会話チャンネルやログチャンネル、挨拶チャンネルとして設定します。\n使い方：r.setch [チャンネルタイプ]\n以下がチャンネルタイプです。\n\n\n\n");
        eb.addField("TALK", "会話チャンネルです、ルーミアちゃんが反応してくれます(いろいろ回数制限等掛けてます)", false);
        eb.addField("LOG", "ログ出力ちゃんねるです、チャンネルを消したりメッセージを消すとログを出してくれます。", false);
        e.getInteraction().replyEmbeds(eb.build()).queue();
    }

    public static void HELP_GAME(SlashCommandInteractionEvent e){
        // Create the EmbedBuilder instance
        EmbedBuilder eb = new EmbedBuilder();   //埋め込みのやつを簡単に作れるツール(Discord.JSにはない！！神！！JAVA先生一生ついていきます！！)
        eb.setTitle("ゲーム", null);     //タイトル
        eb.setColor(Main.RUND_COLOR());   //色設定
        eb.setDescription("ルーミアちゃんBOTのゲームです");
        eb.addField("国旗当てゲーム", "国旗を当てるゲームです、出された国旗を見て国名を当ててください。\nやり方「r.国旗当てゲーム START レベル」\nレベルは1〜5です。", false);
        e.getInteraction().replyEmbeds(eb.build()).queue();
    }
}
