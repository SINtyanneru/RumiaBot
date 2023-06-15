package com.rumisystem.rumiabot;

import com.rumisystem.rumiabot.Command.*;
import com.rumisystem.rumiabot.GAME.NATION_FLAG;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.channel.Channel;
import net.dv8tion.jda.api.events.guild.member.GuildMemberJoinEvent;
import net.dv8tion.jda.api.events.guild.member.GuildMemberRemoveEvent;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
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

                if(msg.startsWith("2時56分です。\n祈ってください") && e.getAuthor().getId().equals("891521181990129675")){
                    e.getMessage().reply("Namuhihutu(るみ語)").queue();
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
    public void onSlashCommandInteraction(SlashCommandInteractionEvent e) {
        switch (e.getName()){
            case "test":
                test.Main(e);
                break;
            case "help":
                help.Main(e);
                break;
            case "ws":
                ws.Main(e);
                break;
            case "update":
                update.Main(e);
                break;
            case "tanzania":
                tanzania.Main(e);
                break;
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
