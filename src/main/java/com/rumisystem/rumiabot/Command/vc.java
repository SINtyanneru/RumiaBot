package com.rumisystem.rumiabot.Command;

import net.dv8tion.jda.api.entities.GuildVoiceState;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.VoiceChannel;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;

public class vc {
    public static void Main(MessageReceivedEvent e){
        String[] cmd = e.getMessage().getContentRaw().split(" ");
        if(cmd[1] == null){
            return;
        }
        if(cmd[1].equals("join")){
            //getMember().getVoiceState()できんのやけど？？ふざけんな
            //ChatGPTも真面目に答えろ
            //あとGitHubのIssues見てたら同じ問題があったから、解決できるかと思ったら
            //ここに書くべきでは無い的なことが書いててふざけんなと思ったわ。。。
            //JDAのバグだろふざけんな
        }
    }
}
