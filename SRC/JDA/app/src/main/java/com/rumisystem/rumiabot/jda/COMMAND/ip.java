package com.rumisystem.rumiabot.jda.COMMAND;

import com.rumisystem.rumiabot.jda.MODULE.HTTP_REQUEST;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;

import java.io.File;

public class ip {
	public static void main(SlashCommandInteractionEvent INTERACTION) {
		try{
			HTTP_REQUEST AJAX = new HTTP_REQUEST("https://ifconfig.me/ip");
			String IP_AD = AJAX.GET();

			INTERACTION.getHook().editOriginal("私のIPアドレスは" + IP_AD + "です").queue();
		} catch (Exception EX) {
			EX.printStackTrace();
			INTERACTION.getHook().editOriginal("取得失敗").queue();
		}
	}
}
