package su.rumishistem.rumiabot.Joke;

import java.util.Arrays;

import su.rumishistem.rumiabot.System.TYPE.ReceiveMessageEvent;

public class Kimo {
	public static void Main(ReceiveMessageEvent e) {
		String Content = e.GetMessage().GetText();

		String[] NGWordList = new String[] {
			"まんこ"
		};

		if (Arrays.asList(NGWordList).contains(Content)) {
			e.GetMessage().Reply("キモ...");
		}
	}
}
