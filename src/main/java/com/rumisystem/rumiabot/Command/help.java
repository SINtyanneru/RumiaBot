package com.rumisystem.rumiabot.Command;

import com.rumisystem.rumiabot.Main;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;

public class help {
    public static void main(MessageReceivedEvent e){
        // Create the EmbedBuilder instance
        EmbedBuilder eb = new EmbedBuilder();   //埋め込みのやつを簡単に作れるツール(Discord.JSにはない！！神！！JAVA先生一生ついていきます！！)
        eb.setTitle("コマンド一覧", null);     //タイトル
        eb.setColor(Main.RUND_COLOR());   //色設定
        eb.setDescription("プレフィクス：r.");
        eb.addField("test", "テストコマンド", false);
        eb.addField("help", "コマンド一覧表示", false);

        e.getChannel().sendMessage(eb.build()).queue();
    }
}
