package com.rumisystem.rumiabot.Command;

import com.google.gson.Gson;
import net.dv8tion.jda.api.entities.*;
import net.dv8tion.jda.api.entities.channel.concrete.Category;
import net.dv8tion.jda.api.entities.channel.concrete.TextChannel;
import net.dv8tion.jda.api.entities.channel.concrete.VoiceChannel;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;

import java.util.ArrayList;
import java.util.List;

public class backup {
    public static void main(MessageReceivedEvent e){
        String TEXT = "";

        List<CATEGORY_BACKUP_OBJECT> category_backup_objects = new ArrayList<>();


        // 各カテゴリごとにチャンネルを取得
        for (Category category : e.getGuild().getCategories()) {
            List<TextChannel> textChannels = category.getTextChannels(); // テキストチャンネルのリストを取得
            List<VoiceChannel> voiceChannels = category.getVoiceChannels(); // ボイスチャンネルのリストを取得


            List<CHANNEL_BACKUP_OBJECT> channel_backup_objects = new ArrayList<>();

            //テキストチャンネルを取得
            for(TextChannel textChannel : textChannels){
                CHANNEL_BACKUP_OBJECT channel_backup_object = new CHANNEL_BACKUP_OBJECT();
                channel_backup_object.setNAME(textChannel.getName());

                channel_backup_objects.add(channel_backup_object);
            }

            //ボイスチャンネルを取得
            for(VoiceChannel voiceChannel : voiceChannels){
                CHANNEL_BACKUP_OBJECT channel_backup_object = new CHANNEL_BACKUP_OBJECT();
                channel_backup_object.setNAME(voiceChannel.getName());

                channel_backup_objects.add(channel_backup_object);
            }

            List<ROLE_BACKUP_OBJECT>role_backup_objects = new ArrayList<>();
            for(PermissionOverride permissionOverride : category.getPermissionOverrides()){
                ROLE_BACKUP_OBJECT role_backup_object = new ROLE_BACKUP_OBJECT();
                role_backup_object.setALLOWED(String.valueOf(permissionOverride.getAllowed()));
                role_backup_object.setDENIED(String.valueOf(permissionOverride.getDenied()));
                role_backup_object.setROLE(permissionOverride.getRole().getName());
                role_backup_object.setMEMBER(String.valueOf(permissionOverride.getManager()));

                role_backup_objects.add(role_backup_object);
            }

            CATEGORY_BACKUP_OBJECT category_backup_object = new CATEGORY_BACKUP_OBJECT();
            category_backup_object.setNAME(category.getName());
            category_backup_objects.add(category_backup_object);
            category_backup_object.setCHANNELS(channel_backup_objects);
            category_backup_object.setROLES(role_backup_objects);


            /*
            System.out.println("┌" + category.getName());
            TEXT =  TEXT + "┌" + category.getName()+ "\n";
            for(TextChannel textChannel : textChannels){
                System.out.println("├ #" + textChannel.getName());
                TEXT =  TEXT + "├ #" + textChannel.getName() + "\n";
                for(PermissionOverride permissionOverride : textChannel.getPermissionOverrides()){
                    System.out.println("Allowed: " + permissionOverride.getAllowed());
                    System.out.println("Denied: " + permissionOverride.getDenied());
                    System.out.println("Role/Member: " + permissionOverride.getRole() + "/" + permissionOverride.getMember());
                }
            }
            for(VoiceChannel voiceChannel : voiceChannels){
                System.out.println("├ 🔈" + voiceChannel.getName());
                TEXT =  TEXT + "├ 🔈" + voiceChannel.getName() + "\n";
                for(PermissionOverride permissionOverride : voiceChannel.getPermissionOverrides()){
                    System.out.println("Allowed: " + permissionOverride.getAllowed());
                    System.out.println("Denied: " + permissionOverride.getDenied());
                    System.out.println("Role/Member: " + permissionOverride.getRole() + "/" + permissionOverride.getMember());
                }
            }*/
        }

        Gson gson = new Gson();
        System.out.println(gson.toJson(category_backup_objects));

        //e.getChannel().sendMessage(TEXT).queue();
    }
}

class CATEGORY_BACKUP_OBJECT{
    private String NAME;
    //private CHANNEL_BACKUP_OBJECT CHANNEL;
    private List<CHANNEL_BACKUP_OBJECT> CHANNELS;
    private List<ROLE_BACKUP_OBJECT> ROLES;

    public void setNAME(String NAME){
        this.NAME = NAME;
    }
/*
    public void setCHANNEL(CHANNEL_BACKUP_OBJECT CHANNEL){
        this.CHANNEL = CHANNEL;
    }
 */

    public void setCHANNELS(List<CHANNEL_BACKUP_OBJECT> CHANNEL){
        this.CHANNELS = CHANNEL;
    }

    public void setROLES(List<ROLE_BACKUP_OBJECT> ROLE){
        this.ROLES = ROLE;
    }
}

class CHANNEL_BACKUP_OBJECT{
    private String NAME;
    private ROLE_BACKUP_OBJECT ROLE;

    public void setNAME(String NAME){
        this.NAME = NAME;
    }

    public void setROLE(ROLE_BACKUP_OBJECT ROLE){
        this.ROLE = ROLE;
    }
}

class ROLE_BACKUP_OBJECT{
    private String ALLOWED;
    private String DENIED;
    private String ROLE;
    private String MEMBER;

    public void setALLOWED(String ALLOW){
        this.ALLOWED = ALLOW;
    }

    public void setDENIED(String DENIED){
        this.DENIED = DENIED;
    }

    public void setROLE(String ROLE){
        this.ROLE = ROLE;
    }

    public void setMEMBER(String MEMBER){
        this.MEMBER = MEMBER;
    }
}