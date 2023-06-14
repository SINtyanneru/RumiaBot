/*
* r.test command
* */

package com.rumisystem.rumiabot.Command;

import com.rumisystem.rumiabot.Main;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;

public class test {
    public static void Main(SlashCommandInteractionEvent e){
        // Create the EmbedBuilder instance
        EmbedBuilder eb = new EmbedBuilder();   //埋め込みのやつを簡単に作れるツール(Discord.JSにはない！！神！！JAVA先生一生ついていきます！！)
        eb.setTitle("るーみあちゃん", null);     //タイトル
        eb.setColor(Main.RUND_COLOR());   //色設定
        eb.setDescription("正常なのだ！");

        //送信
        e.getInteraction().replyEmbeds(eb.build()).queue();
    }
}
