package com.rumisystem.rumiabot.Command;

import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.TextChannel;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;

import java.util.List;

public class msgc {
    public static void Main(MessageReceivedEvent e){
        String[] cmd = e.getMessage().getContentRaw().split(" ");
        if(cmd[1] == null){
            e.getMessage().reply("使い方が違う").queue();
            return;
        }

        if(cmd[1].equalsIgnoreCase("DEL")){//メッセージ削除
            Member member = e.getMember();
            if(member != null && member.hasPermission(Permission.ADMINISTRATOR)) {//ユーザーが管理者権限を持っているか
                int messageCount = Integer.parseInt(cmd[2]); //削除したいメッセージの数
                if(messageCount >= 2 && messageCount <= 100){//消去する範囲をチェック
                    TextChannel channel = e.getTextChannel(); //メッセージを削除するチャンネル

                    List<Message> messages = channel.getHistory().retrievePast(messageCount).complete();
                    channel.deleteMessages(messages).queue();
                }else {
                    //2〜100の範囲ではない
                    e.getMessage().reply("消去する範囲は2〜100です！！").queue();
                }
            } else {
                //管理者権限ねえよ！！
                e.getMessage().reply("あなたは所有していないを権限！").queue();
            }
        }
    }
}
