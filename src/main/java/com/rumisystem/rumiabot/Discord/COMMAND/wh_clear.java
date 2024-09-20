package com.rumisystem.rumiabot.Discord.COMMAND;

import java.util.List;

import net.dv8tion.jda.api.entities.Webhook;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;

public class wh_clear {
	public static void Main(SlashCommandInteractionEvent IT) {
		try{
			List<Webhook> WHU = IT.getChannel().asTextChannel().retrieveWebhooks().complete();

			String CONTENTS = "";

			for(Webhook WH:WHU){
				try{
					WH.delete().queue();

					CONTENTS += "[  OK  ]削除：" + WH.getName() + "\n";
				}catch (Exception EX){
					CONTENTS += "[FAILED]失敗：" + WH.getName() + "\n";
				}

				IT.getHook().editOriginal("```\n" + CONTENTS + "\n```").queue();
			}

			IT.getHook().editOriginal("```\n" + CONTENTS + "\n```\n完了").queue();
		} catch (Exception EX) {
			EX.printStackTrace();
			IT.getHook().editOriginal("失敗").queue();
		}
	}
}
