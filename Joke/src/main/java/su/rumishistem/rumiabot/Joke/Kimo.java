package su.rumishistem.rumiabot.Joke;

import java.util.Arrays;

import su.rumishistem.rumiabot.System.Type.ReceiveMessageEvent;

public class Kimo {
	public static void Main(ReceiveMessageEvent e) {
		String Content = e.get_discord().getMessage().getContentRaw();

		String[] NGWordList = new String[] {
			"まんこ"
		};

		if (Arrays.asList(NGWordList).contains(Content)) {
			e.get_discord().getMessage().reply("キモ...").queue();
		}
	}
}
