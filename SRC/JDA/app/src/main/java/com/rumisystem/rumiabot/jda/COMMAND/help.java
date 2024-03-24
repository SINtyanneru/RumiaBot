package com.rumisystem.rumiabot.jda.COMMAND;

import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;

public class help {
	public static void main(SlashCommandInteractionEvent INTERACTION) {
		INTERACTION.getHook().editOriginal("[公式サイト](https://rumiserver.com/rumiabot/site/function)を見てくれ").queue();
	}
}
