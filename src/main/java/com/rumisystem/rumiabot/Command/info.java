package com.rumisystem.rumiabot.Command;

import com.rumisystem.rumiabot.Main;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.MessageReaction;
import net.dv8tion.jda.api.entities.User;
import net.dv8tion.jda.api.entities.emoji.RichCustomEmoji;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;

import java.util.Date;
import java.util.List;
import java.util.Objects;
import java.util.TimeZone;

import static com.rumisystem.rumiabot.Main.LOG_OUT;
import static com.rumisystem.rumiabot.Main.jda;
import java.text.SimpleDateFormat;

public class info {
    public static void Main(SlashCommandInteractionEvent e){
        e.deferReply().queue();

        if(Objects.isNull(e.getInteraction().getOption("select"))){
            e.getHook().editOriginal("エラー！").queue();
            return;
        }

        if(e.getInteraction().getOption("select").getAsString().equalsIgnoreCase("SERVER")){
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
                eb.addField("デフォルトチャンネル", "<#" + String.valueOf(e.getGuild().getDefaultChannel().getId()) + ">", false);//人数

                String CREATE_DATE = e.getGuild().getTimeCreated().toString();

                eb.addField("作成日時", String.valueOf(CREATE_DATE), false);//人数
                StringBuilder Emojis = new StringBuilder();
                for (RichCustomEmoji emoji : e.getGuild().getEmojis()){
                    Emojis.append("<:" + emoji.getName() + ":" + emoji.getId() + ">");
                }

                eb.addField("絵文字", Emojis.toString().substring(0, 1024), false);//人数

                e.getHook().editOriginalEmbeds(eb.build()).queue();
            }catch (Exception ex){
                e.getHook().editOriginal("サーバー情報取得時にエラーなのだ").queue();
                LOG_OUT(ex.getMessage());
            }
        }else if(e.getInteraction().getOption("select").getAsString().equalsIgnoreCase("USER")){
            try{
                if(Objects.isNull(e.getInteraction().getOption("users"))){
                    e.getHook().editOriginal("ユーザーを指定してね").queue();
                    return;
                }
                User user = e.getOption("users").getAsUser();

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

                e.getHook().editOriginalEmbeds(eb.build()).queue();
            }catch (Exception ex){
                e.getHook().editOriginal("ユーザー情報取得時にエラーなのだ").queue();
                LOG_OUT(ex.getMessage());
            }
        }else{
            e.getHook().editOriginal("使いかやがちがう！").queue();
        }
    }
}
