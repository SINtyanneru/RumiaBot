package com.rumisystem.rumiabot;

import com.rumisystem.rumiabot.Command.*;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.TextChannel;
import net.dv8tion.jda.api.events.channel.text.TextChannelDeleteEvent;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;

import java.awt.*;

import static com.rumisystem.rumiabot.Main.LOG_OUT;
import static com.rumisystem.rumiabot.Main.jda;

public class DiscordEvent extends ListenerAdapter {
    @Override
    public void onMessageReceived(MessageReceivedEvent e) {
        String msg = e.getMessage().getContentRaw(); //入力されたメッセージを取得
        if(!e.getAuthor().equals(jda.getSelfUser())) {  //送信されたメッセージがBOTによるものではないか
            Main.LOG_OUT(e.getGuild().getName() + "/" + e.getChannel().getName());
            System.out.println("[ INFO ]Sent msg:" + msg);
            if(msg.equals("r.test")){
                test.main(e);
            }
            if(msg.startsWith("r.help")){
                help.main(e);
            }
            if(msg.equals("r.tanzania")){
                tanzania.main(e);
            }
            if(msg.startsWith("r.setch")){
                setch.main(e);
            }

            //System.out.println(e.getChannel().getHistory().getMessageById(e.getMessageId()));
        }
    }

    @Override
    public void onTextChannelDelete(TextChannelDeleteEvent e) {
        String deletedChannelName = e.getChannel().getName();
        System.out.println("Channel [" + deletedChannelName + "] was deleted.");
        System.out.println("Server id:" + e.getChannel().getGuild().getId());
    }
}
