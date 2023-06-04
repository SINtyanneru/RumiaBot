package com.rumisystem.rumiabot;

import com.rumisystem.rumiabot.Command.*;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.TextChannel;
import net.dv8tion.jda.api.events.channel.text.TextChannelDeleteEvent;
import net.dv8tion.jda.api.events.guild.member.GuildMemberJoinEvent;
import net.dv8tion.jda.api.events.guild.member.GuildMemberLeaveEvent;
import net.dv8tion.jda.api.events.guild.member.GuildMemberRemoveEvent;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;

import java.awt.*;
import java.io.IOException;

import static com.rumisystem.rumiabot.Main.LOG_OUT;
import static com.rumisystem.rumiabot.Main.jda;

public class DiscordEvent extends ListenerAdapter {
    @Override
    public void onMessageReceived(MessageReceivedEvent e) {
        try {
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
                if(msg.startsWith("r.backup")){
                    backup.main(e);
                }
                if(msg.startsWith("r.vc")){
                    vc.Main(e);
                }
                if(msg.startsWith("r.info")){
                    info.Main(e);
                }
                if(msg.startsWith("r.ws")){
                    ws.Main(e);
                }
                if(msg.startsWith("r.msg")){
                    msgc.Main(e);
                }

                URL_GET.Main(e);

                //System.out.println(e.getChannel().getHistory().getMessageById(e.getMessageId()));
            }
        }catch (Exception ex){
            LOG_OUT("[ ERR ]" + ex.getMessage());
        }
    }

    @Override
    public void onTextChannelDelete(TextChannelDeleteEvent e) {
        try{
            String deletedChannelName = e.getChannel().getName();
            System.out.println("Channel [" + deletedChannelName + "] was deleted.");
            System.out.println("Server id:" + e.getChannel().getGuild().getId());
        }catch (Exception ex){
            LOG_OUT("[ ERR ]" + ex.getMessage());
        }
    }

    @Override
    public void onGuildMemberJoin(GuildMemberJoinEvent e) {
        try{
            // ユーザーが参加したときに実行されるコード
            System.out.println(e.getMember().getUser().getName() + " joined the server.");
        }catch (Exception ex){
            LOG_OUT("[ ERR ]" + ex.getMessage());
        }

    }

    //※GuildMemberLeaveEventは古いので使えない
    @Override
    public void onGuildMemberRemove(GuildMemberRemoveEvent e) {
        try{
            // ユーザーが離脱したときに実行されるコード
            System.out.println(e.getMember().getUser().getName() + " left the server.");
        }catch (Exception ex){
            LOG_OUT("[ ERR ]" + ex.getMessage());
        }
    }
}