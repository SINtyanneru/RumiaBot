package com.rumisystem.rumiabot.jda;

import com.rumisystem.rumiabot.jda.COMMAND.info_server;
import com.rumisystem.rumiabot.jda.COMMAND.info_user;
import com.rumisystem.rumiabot.jda.COMMAND.test;
import com.rumisystem.rumiabot.jda.COMMAND.ws;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;

import java.util.Objects;

public class DiscordEvent extends ListenerAdapter {
	@Override
	public void onMessageReceived(MessageReceivedEvent E) {
		try {
			String MESSAGE_CONTENT = E.getMessage().getContentRaw();
			String GUILD_NAME = "localhost";

			//鯖でのメッセージなら、鯖名に鯖名を入れる
			if(E.isFromGuild()){
				GUILD_NAME = E.getGuild().getName();
			}

			//ログを出す部分
			StringBuilder LOG_TEXT = new StringBuilder("┌[" + E.getAuthor().getName() + "@" + GUILD_NAME + "/" + E.getChannel().getName() + "]\n");
			String[] TEXT_SPLIT = MESSAGE_CONTENT.split("\n");
			for(int I = 0; TEXT_SPLIT.length > I; I++){
				String TEXT = TEXT_SPLIT[I];
				if(TEXT_SPLIT.length > I + 1){
					LOG_TEXT.append("├" + TEXT + "\n");
				} else {
					LOG_TEXT.append("└" + TEXT + "\n");
				}
			}
			System.out.println(LOG_TEXT.toString());

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
