package com.rumisystem.rumiabot.Discord.COMMAND;

import com.rumisystem.rumi_java_lib.HTTP_REQUEST;

import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;

public class ip {
	public static void Main(SlashCommandInteractionEvent IT) {
		try{
			HTTP_REQUEST AJAX = new HTTP_REQUEST("https://ifconfig.me/ip");
			String IP_AD = AJAX.GET();

			IT.getHook().editOriginal("私のIPアドレスは" + IP_AD + "です").queue();
		} catch (Exception EX) {
			EX.printStackTrace();
			IT.getHook().editOriginal("取得失敗").queue();
		}
	}
}
