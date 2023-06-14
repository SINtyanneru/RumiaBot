package com.rumisystem.rumiabot;

import com.rumisystem.rumiabot.Command.*;
import com.rumisystem.rumiabot.GAME.NATION_FLAG;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.channel.Channel;
import net.dv8tion.jda.api.events.guild.member.GuildMemberJoinEvent;
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

                Main.LOG_OUT(e.getGuild().getName() + "/" + e.getChannel().getName() + "\nSent msg:" + msg);

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
                if(msg.startsWith("r.info")){
                    info.Main(e);
                }
                if(msg.startsWith("r.ws")){
                    ws.Main(e);
                }
                if(msg.startsWith("r.msg")){
                    msgc.Main(e);
                }

                //ゲーム
                if(msg.startsWith("r.国旗当てゲーム")){
                    NATION_FLAG.Main(e);
                }

                //URL_GET.Main(e);
            }
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
