package com.rumisystem.rumiabot.Command;

import com.rumisystem.rumiabot.Main;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import net.dv8tion.jda.api.utils.FileUpload;

import java.io.File;
import java.nio.file.Path;
import java.nio.file.Paths;

public class tanzania {
    public static void Main(SlashCommandInteractionEvent e){
        Path p1 = Paths.get("");
        Path p2 = p1.toAbsolutePath();

        System.out.println(p2.toString());
        e.getInteraction().replyFiles(FileUpload.fromData(new File(p2.toString() + "/DATA/MUSIC/TANZANIA.mp3"))).addContent(":flag_tz:Tanzania!:flag_tz:").queue();
    }
}
