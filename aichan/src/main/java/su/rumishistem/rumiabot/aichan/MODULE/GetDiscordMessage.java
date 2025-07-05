package su.rumishistem.rumiabot.aichan.MODULE;

import static su.rumishistem.rumiabot.System.Main.DISCORD_BOT;

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
			TextChannel Channel = DISCORD_BOT.getTextChannelById(ChannelID);
			if (Channel != null) {
				System.out.println("CID:" + ChannelID);
				System.out.println("MID:" + MessageID);
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
