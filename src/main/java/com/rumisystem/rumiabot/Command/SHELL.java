package com.rumisystem.rumiabot.Command;

import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.utils.FileUpload;
import org.apache.commons.io.FileUtils;

import java.io.File;
import java.nio.file.*;
import java.util.Objects;

import static com.rumisystem.rumiabot.Main.AppDir;

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

            String PATH = AppDir + "/SHELL/" + e.getUser().getId();
            File folder = new File(PATH);
            if (folder.exists() && folder.isDirectory()) {
                //存在するからそのままGO
                switch (CMD[0]){
                    case "ls":
                        e.getHook().editOriginal("```ファイルシステム作成中```").queue();
                        break;
                    case "cd":
                        e.getHook().editOriginal("```ファイルシステム作成中```").queue();
                        break;
                    case "":
                        e.getHook().editOriginal("``` ```").queue();
                        break;
                    default:
                        e.getHook().editOriginal("```" + CMD[0] + "というコマンドはこの世に存在しません```").queue();
                        break;
                }
            } else {
                e.getHook().editOriginal("```[ *** ]あなたの仮想環境がありません、\n作成中...```").queue();
                boolean created = folder.mkdirs(); // フォルダを作成します

                if (created) {
                    File sourcePath = new File(AppDir + "/SHELL/SAMPLE_DATA");
                    File destinationPath = new File(PATH);

                    //サンプルファイルをコピー
                    FileUtils.copyDirectory(sourcePath, destinationPath);

                    e.getHook().editOriginal("```[ OK ]あなたの仮想環境が作成されました！```").queue();
                } else {
                    e.getHook().editOriginal("```[ ERR ]失敗```").queue();
                }
            }

        }catch (Exception ex){
            e.getHook().editOriginal("エラー" + ex.getMessage()).queue();
        }
    }
}
