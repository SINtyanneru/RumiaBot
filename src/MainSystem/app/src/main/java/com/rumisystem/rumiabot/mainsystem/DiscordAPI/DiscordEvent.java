package com.rumisystem.rumiabot.mainsystem.DiscordAPI;

import static com.rumisystem.rumiabot.mainsystem.Main.CONFIG_DATA;
import static com.rumisystem.rumi_java_lib.LOG_PRINT.Main.LOG;
import com.rumisystem.rumi_java_lib.LOG_PRINT.LOG_TYPE;

import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;


public class DiscordEvent extends ListenerAdapter {
	@Override
	public void onMessageReceived(MessageReceivedEvent E) {
		try {
			String MESSAGE_CONTENT = E.getMessage().getContentRaw();
			String GUILD_NAME = "localhost";

			//ログを出す部分
			if(CONFIG_DATA.get("DISCORD").asBool("MESSAGE_LOG_PRINT")){
				LOG(LOG_TYPE.INFO, "┌[" + E.getAuthor().getName() + "@" + GUILD_NAME + "/" + E.getChannel().getName() + "]");

				String[] TEXT_SPLIT = MESSAGE_CONTENT.split("\n");
				for(int I = 0; TEXT_SPLIT.length > I; I++){
					String TEXT = TEXT_SPLIT[I];
					if(TEXT_SPLIT.length > I + 1){
						LOG(LOG_TYPE.INFO, "├" + TEXT);
					} else {
						LOG(LOG_TYPE.INFO, "└" + TEXT);
					}
				}
			}
		}catch(Exception EX){
			EX.printStackTrace();
		}
	}
}
