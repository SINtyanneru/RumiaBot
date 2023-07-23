package com.rumisystem.rumiabot.Command;

import com.rumisystem.rumiabot.Main;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.MessageReaction;
import net.dv8tion.jda.api.entities.User;
import net.dv8tion.jda.api.entities.emoji.RichCustomEmoji;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;

import java.util.*;

import static com.rumisystem.rumiabot.Main.LOG_OUT;
import static com.rumisystem.rumiabot.Main.jda;
import java.text.SimpleDateFormat;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

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
                StringBuilder EMOJIS_STRING = new StringBuilder();
                for (RichCustomEmoji EMOJI : e.getGuild().getEmojis()){
                    if(EMOJI.isAvailable()){
                        if(!EMOJI.isAnimated()){
                            EMOJIS_STRING.append("<:" + EMOJI.getName() + ":" + EMOJI.getId() + ">");
                        }else{
                            EMOJIS_STRING.append("<a:" + EMOJI.getName() + ":" + EMOJI.getId() + ">");
                        }
                    }
                }

                Pattern pattern = Pattern.compile("<.*?>"); // 正規表現パターン
                Matcher matcher = pattern.matcher(EMOJIS_STRING);

                List<String> EMOJIS = new ArrayList<>();

                int EMOJI_LENGTH = 0;
                StringBuilder EMOJI_TEMP_STRING = new StringBuilder();
                while (matcher.find()) {
                    LOG_OUT(String.valueOf(EMOJI_LENGTH));
                    String tag = matcher.group();
                    if(EMOJI_LENGTH <= 1024){
                        EMOJI_TEMP_STRING.append(tag);
                        EMOJI_LENGTH = (EMOJI_LENGTH + tag.length());
                    }else{
                        EMOJIS.add(EMOJI_TEMP_STRING.toString());

                        LOG_OUT(EMOJIS.toString());

                        EMOJI_TEMP_STRING.delete(0, EMOJI_TEMP_STRING.length());

                        EMOJI_LENGTH = 0;
                    }
                }

                if(EMOJI_LENGTH >= 0){
                    EMOJIS.add(EMOJI_TEMP_STRING.toString());
                }

                LOG_OUT(EMOJIS.toString());


                for(String TEST:EMOJIS){
                    eb.addField("絵文字", TEST.toString().substring(0, Math.min(TEST.toString().length(), 1024)), false);//人数
                }

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
