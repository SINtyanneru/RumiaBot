package com.rumisystem.rumiabot.Command;

import com.rumisystem.rumiabot.Main;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.User;
import net.dv8tion.jda.api.entities.emoji.RichCustomEmoji;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;

import java.util.List;

import static com.rumisystem.rumiabot.Main.jda;

public class info {
    public static void Main(MessageReceivedEvent e){
        String[] cmd = e.getMessage().getContentRaw().split(" ");
        if(cmd.length == 1){
            e.getMessage().reply("使い方が違う").queue();
            return;
        }

        if(cmd[1].equalsIgnoreCase("SERVER")){
            try{
                // Create the EmbedBuilder instance
                EmbedBuilder eb = new EmbedBuilder();   //埋め込みのやつを簡単に作れるツール(Discord.JSにはない！！神！！JAVA先生一生ついていきます！！)
                eb.setTitle(e.getGuild().getName(), null);     //タイトル
                eb.setColor(Main.RUND_COLOR());   //色設定
                eb.setDescription(e.getGuild().getDescription());
                eb.setThumbnail(e.getGuild().getIconUrl());
                if(e.getGuild().getOwner() != null){//所有者かを取得できるか
                    //できた
                    eb.addField("所有者", e.getGuild().getOwner().getUser().getName(), false);
                }else {
                    //出来なかった
                    eb.addField("所有者", "取得失敗", false);
                }
                eb.addField("人数", String.valueOf(e.getGuild().getMemberCount()), false);//人数
                eb.addField("チャンネル数", String.valueOf(e.getGuild().getChannels().size()), false);//人数
                eb.addField("カテゴリ数", String.valueOf(e.getGuild().getCategories().size()), false);//人数
                eb.addField("デフォルトチャンネル", String.valueOf(e.getGuild().getDefaultChannel()), false);//人数
                StringBuilder Emojis = new StringBuilder();
                for (RichCustomEmoji emoji : e.getGuild().getEmojis()){
                    Emojis.append("<:" + emoji.getName() + ":" + emoji.getId() + ">");
                }
                eb.addField("絵文字", Emojis.toString().substring(0, 1024), false);//人数

                e.getMessage().replyEmbeds(eb.build()).queue();
            }catch (Exception ex){
                e.getMessage().reply("エラー：" + ex).queue();
            }
        }else if(cmd[1].equalsIgnoreCase("USER")){
            try{
                // メッセージ内のメンションを取得する
                List<User> mentionedUsers = e.getMessage().getMentions().getUsers();

                // 各メンションのIDを取得する
                for (User user : mentionedUsers) {
                    String userId = user.getId();
                    User us = jda.getUserById(userId);
                    // Create the EmbedBuilder instance
                    EmbedBuilder eb = new EmbedBuilder();   //埋め込みのやつを簡単に作れるツール(Discord.JSにはない！！神！！JAVA先生一生ついていきます！！)
                    eb.setTitle(us.getName(), null);     //タイトル
                    eb.setColor(Main.RUND_COLOR());   //色設定
                    eb.setThumbnail(us.getAvatarUrl());

                    //eb.addField("この鯖でのニックネーム",e.getGuild().getMember(us).getNickname(), false);
                    eb.addField("ID", us.getId(), false);
                    eb.addField("アイコンのID", us.getAvatarId(), false);
                    eb.addField("この鯖の所有者か", String.valueOf(e.getGuild().getMember(us).isOwner()), false);
                    e.getMessage().replyEmbeds(eb.build()).queue();
                }
            }catch (Exception ex){
                e.getMessage().reply("エラー：" + ex.getMessage()).queue();
            }
        }else{
            e.getMessage().reply("つかいかたちがう！").queue();
        }
    }
}
