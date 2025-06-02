package su.rumishistem.rumiabot.Joke;

import java.util.Random;

import su.rumishistem.rumiabot.System.TYPE.ReceiveMessageEvent;

public class SouiunoYokunaiyo {
	public static void Main(ReceiveMessageEvent e) {
		String Content = e.GetMessage().GetText();
		if (Content.equals("ち") || Content.equals("ちん") || Content.equals("ま") || Content.equals("まん")) {
			if (new Random().nextInt(1000) == 0) {
				if (Content.endsWith("ん")) {
					e.GetMessage().Reply("こ");
				} else {
					e.GetMessage().Reply("んこ");
				}
			} else {
				e.GetMessage().Reply("そういうのよくないと思うよ。");
			}
		}
	}
}
