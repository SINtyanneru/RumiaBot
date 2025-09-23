package su.rumishistem.rumiabot.Joke;

import su.rumishistem.rumiabot.System.TYPE.ReceiveMessageEvent;

public class SouiunoYokunaiyo {
	public static void Main(ReceiveMessageEvent e) {
		String Content = e.GetMessage().GetText();
		if (Content.equals("ち") || Content.equals("ちん") || Content.equals("ま") || Content.equals("まん")) {
			if (TeiKakuricu.get()) {
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
