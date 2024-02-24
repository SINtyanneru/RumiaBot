package com.rumisystem.rumiabot.jda;

import com.rumisystem.rumiabot.jda.COMMAND.info_server;
import com.rumisystem.rumiabot.jda.COMMAND.info_user;
import com.rumisystem.rumiabot.jda.COMMAND.test;
import com.rumisystem.rumiabot.jda.COMMAND.ws;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;

public class DiscordEvent extends ListenerAdapter {
	@Override
	public void onMessageReceived(MessageReceivedEvent E) {
		try {
			String MESSAGE_CONTENT = E.getMessage().getContentRaw();
			System.out.println(MESSAGE_CONTENT);
		} catch (Exception EX) {
			EX.printStackTrace();
		}
	}

	@Override
	public void onSlashCommandInteraction(SlashCommandInteractionEvent INTERACTION){
		try{
			System.out.println(
					"[ INFO ][ SL ]┌Interaction create:" +
							INTERACTION.getName() +
							"\n             ├in " +
							INTERACTION.getGuild().getName() +
							"\n             ├in " +
							INTERACTION.getChannel().getName() +
							INTERACTION.getCommandId() +
							"\n             └in " +
							INTERACTION.getMember().getUser().getName() +
							"(" +
							INTERACTION.getMember().getId() +
							")"
			);

			//ユーザーに待ってもらう
			INTERACTION.deferReply().queue();

			switch (INTERACTION.getName()){
				case "test":{
					test.main(INTERACTION);
					break;
				}

				case "info_server":{
					info_server.main(INTERACTION);
					break;
				}

				case "info_user":{
					info_user.main(INTERACTION);
					break;
				}

				case "ws":{
					ws.main(INTERACTION);
					break;
				}

				default:{
					INTERACTION.getHook().editOriginal("？").queue();
				}
			}
		}catch (Exception EX){
			EX.printStackTrace();
		}
	}
}
