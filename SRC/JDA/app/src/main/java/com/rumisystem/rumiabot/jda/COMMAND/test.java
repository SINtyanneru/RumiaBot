package com.rumisystem.rumiabot.jda.COMMAND;

import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;

public class test {
	public static void main(SlashCommandInteractionEvent INTERACTION) {
		//TODO:JSの方から色々持ってくる
		INTERACTION.getHook().editOriginal("あいうえお").queue();
	}
}
