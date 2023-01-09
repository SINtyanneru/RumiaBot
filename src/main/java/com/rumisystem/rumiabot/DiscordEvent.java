package com.rumisystem.rumiabot;

import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;

import java.awt.*;

import static com.rumisystem.rumiabot.Main.jda;

public class DiscordEvent extends ListenerAdapter {
    @Override
    public void onMessageReceived(MessageReceivedEvent e) {
        String msg = e.getMessage().getContentRaw(); //入力されたメッセージを取得
        if(!e.getAuthor().equals(jda.getSelfUser())) {  //送信されたメッセージがBOTによるものではないか
            System.out.println("[ INFO ]Sent msg:" + msg);
            if(msg.equals("r.test")){
                // Create the EmbedBuilder instance
                EmbedBuilder eb = new EmbedBuilder();   //埋め込みのやつを簡単に作れるツール(Discord.JSにはない！！神！！JAVA先生一生ついていきます！！)
                eb.setTitle("サーバー", null);     //タイトル
                eb.setColor(Main.RUND_COLOR());   //色設定
                eb.setDescription("正常なのだ！");

                e.getTextChannel().sendMessage(eb.build()).queue();
            }
        }
    }
}
