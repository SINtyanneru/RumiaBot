package com.rumisystem.rumiabot.Command;

import com.rumisystem.rumiabot.Main;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;

public class update {
    public static void Main(MessageReceivedEvent e){
        EmbedBuilder eb = new EmbedBuilder();   //埋め込みのやつを簡単に作れるツール(Discord.JSにはない！！神！！JAVA先生一生ついていきます！！)
        eb.setTitle("るみあBOTアプデ情報", null);     //タイトル
        eb.setColor(Main.RUND_COLOR());   //色設定
        eb.addField("JDAが5.0に", "", false);

        e.getMessage().replyEmbeds(eb.build()).queue();
    }
}
