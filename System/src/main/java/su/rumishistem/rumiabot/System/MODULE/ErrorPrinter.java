package su.rumishistem.rumiabot.System.MODULE;

import static su.rumishistem.rumiabot.System.Main.DISCORD_BOT;

import net.dv8tion.jda.api.entities.channel.concrete.TextChannel;
import su.rumishistem.rumi_java_lib.EXCEPTION_READER;

public class ErrorPrinter {
	public static void print(String id, Exception ex) {
		String ex_text = EXCEPTION_READER.READ(ex);
		ex.printStackTrace();

		//エラーを吐き出すチャンネル
		TextChannel ch = DISCORD_BOT.getTextChannelById("1382127695273529455");
		if (ch != null) {
			ch.sendMessage("["+id+"]\n```" + ex_text + "\n```").queue();
		}
	}
}
