package com.rumisystem.rumiabot.jda.COMMAND;

import net.dv8tion.jda.api.entities.Webhook;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;

import java.util.HashMap;
import java.util.List;

public class wh_clear {
	public static void main(SlashCommandInteractionEvent INTERACTION) {
		List<Webhook> WHU = INTERACTION.getChannel().asTextChannel().retrieveWebhooks().complete();

		String CONTENTS = "";

		for(Webhook WH:WHU){
			try{
				WH.delete().queue();

				CONTENTS += "[  OK  ]削除：" + WH.getName() + "\n";
			}catch (Exception EX){
				CONTENTS += "[FAILED]失敗：" + WH.getName() + "\n";
			}

			INTERACTION.getHook().editOriginal("```\n" + CONTENTS + "\n```").queue();
		}

		INTERACTION.getHook().editOriginal("```\n" + CONTENTS + "\n```\n完了").queue();
	}
}
