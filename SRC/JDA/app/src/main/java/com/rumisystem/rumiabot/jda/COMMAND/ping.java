package com.rumisystem.rumiabot.jda.COMMAND;

import com.rumisystem.rumiabot.jda.MODULE.HTTP_REQUEST;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.InetAddress;

public class ping {
	public static void main(SlashCommandInteractionEvent INTERACTION) {
		try{
			String IP = INTERACTION.getOption("ip").getAsString().replaceAll("[^0-9.-A-Za-z]", "");

			ProcessBuilder PB = new ProcessBuilder(new String[]{"ping", "-c4", IP});
			Process PROCESS = PB.start();

			String CONTENTS = "";
			String RESULT = "";

			BufferedReader READER = new BufferedReader(new InputStreamReader(PROCESS.getInputStream()));
			String LINE;
			int I = 0;
			while ((LINE = READER.readLine()) != null) {
				if(I == 0){
					CONTENTS += LINE;
					INTERACTION.getHook().editOriginal(CONTENTS).queue();
				}

				//PINGの送信データ
				if(I >= 1 && I <= 4){
					String[] LINE_SPLIT = LINE.split(" ");

					CONTENTS += "\n" + LINE_SPLIT[0] + "バイト / 時間:" + LINE_SPLIT[6].split("=")[1] + "ミリ秒";

					INTERACTION.getHook().editOriginal(CONTENTS).queue();
				}

				//PINGの結果
				if(I >= 7){
					RESULT = LINE;
					break;
				}

				I++;
			}

			int EXIT_CODE = PROCESS.waitFor();
			PROCESS.destroy();

			if(EXIT_CODE == 0){
				CONTENTS += "\n" + RESULT;
				INTERACTION.getHook().editOriginal(CONTENTS).queue();
			} else {
				CONTENTS += "\n失敗";
				INTERACTION.getHook().editOriginal(CONTENTS).queue();
			}
		} catch (Exception EX) {
			EX.printStackTrace();
			INTERACTION.getHook().editOriginal("失敗").queue();
		}
	}
}