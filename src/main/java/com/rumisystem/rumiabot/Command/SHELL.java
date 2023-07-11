package com.rumisystem.rumiabot.Command;

import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.utils.FileUpload;

import java.io.File;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Objects;

public class SHELL {
    public static void Main(SlashCommandInteractionEvent e){
        try{
            e.deferReply().queue();

            String[] CMD;
            if(Objects.isNull(e.getInteraction().getOption("cmd"))){//Nullチェック、未だにJAVAのNullチェックがわからん
                e.getHook().editOriginal("ERR").queue();
                return;
            }else {
                CMD = e.getInteraction().getOption("cmd").getAsString().split(" ");
            }

            e.getHook().editOriginal("コマンド：" + CMD[0]).queue();

        }catch (Exception ex){
            e.getHook().editOriginal("エラー" + ex.getMessage()).queue();
        }
    }
}
