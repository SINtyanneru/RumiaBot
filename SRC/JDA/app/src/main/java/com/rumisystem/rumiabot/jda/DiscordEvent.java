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
			//TODO:ログを戻す
			//TODO:検索機能を復元する
			//TODO:日付機能を復元する
			//TODO:メンション時の返答を全て復元する
			//TODO:メッセージファイルロガーを復元する
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

			//TODO:ヘルプコマンドを復旧する
			//TODO:IP開示を復旧する
			//TODO:PINGを復旧する
			//TODO:mazokupicを復旧する
			//TODO:sns_set sns_removeを復旧する
			//TODO:SETTINGを復旧する
			//TODO:WH_CLEARを復旧する
			//TODO:VOICEVOXを復旧する

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
