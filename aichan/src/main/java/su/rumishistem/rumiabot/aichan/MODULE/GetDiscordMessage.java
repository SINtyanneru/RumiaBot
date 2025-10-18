package su.rumishistem.rumiabot.aichan.MODULE;

import static su.rumishistem.rumiabot.System.Main.get_discord_bot;

import java.util.concurrent.CountDownLatch;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.channel.concrete.TextChannel;

public class GetDiscordMessage {
	public static Message Get(String ID) {
		Matcher IDMatcher = Pattern.compile("D-(\\d{1,100})_(\\d{1,100})").matcher(ID);
		if (IDMatcher.find()) {
			String ChannelID = IDMatcher.group(1);
			String MessageID = IDMatcher.group(2);
			TextChannel Channel = get_discord_bot().get_primary_bot().getTextChannelById(ChannelID);
			if (Channel != null) {
				CountDownLatch CDL = new CountDownLatch(1);
				Message[] MSG = {null};
				Channel.retrieveMessageById(MessageID).queue(
					//↓ラムダ式しか使えないのクソ
					(Message) -> {
						MSG[0] = Message;
						CDL.countDown();
					}
				);

				try {
					CDL.await();
					return MSG[0];
				} catch (Exception EX) {
					EX.printStackTrace();
					throw new Error(EX.getMessage());
				}
			} else {
				throw new Error("チャンネルがこの世にありません");
			}
		} else {
			throw new Error("投稿がこの世にありません");
		}
	}
}
