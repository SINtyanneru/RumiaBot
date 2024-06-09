package com.rumisystem.rumiabot.jda.COMMAND;

import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.utils.FileUpload;

import java.io.File;

public class QR {
	public static void main(SlashCommandInteractionEvent INTERACTION) {
		try{
			String TYPE = INTERACTION.getInteraction().getOption("type").getAsString();
			String DATA = INTERACTION.getInteraction().getOption("data").getAsString();
			String PATH = "./DOWNLOAD/QR/" + INTERACTION.getId() + ".png";

			//安全対策
			TYPE = TYPE.replaceAll("[^QRMr]", "");
			DATA = DATA.replaceAll("\"", "'");//"は\"に置換する

			String[] CMD = {"/bin/sh", "-c", "qrean -t " + TYPE + " -l H -f PNG -o \"" + PATH + "\"" + " \"" + DATA + "\""};

			ProcessBuilder PB = new ProcessBuilder(CMD);

			Process PROCESS = PB.start();

			if(PROCESS.waitFor() == 0){
				INTERACTION.getHook().editOriginal("成功").setAttachments(FileUpload.fromData(new File(PATH))).queue();
			} else {
				INTERACTION.getHook().editOriginal("失敗").queue();
			}
		} catch (Exception EX) {
			EX.printStackTrace();
			INTERACTION.getHook().editOriginal("失敗").queue();
		}
	}
}
