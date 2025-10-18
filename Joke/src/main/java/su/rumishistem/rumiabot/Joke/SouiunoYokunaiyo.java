package su.rumishistem.rumiabot.Joke;

import su.rumishistem.rumiabot.System.Type.ReceiveMessageEvent;

public class SouiunoYokunaiyo {
	public static void Main(ReceiveMessageEvent e) {
		String Content = e.get_discord().getMessage().getContentRaw();
		if (Content.equals("ち") || Content.equals("ちん") || Content.equals("ま") || Content.equals("まん")) {
			if (TeiKakuricu.get()) {
				if (Content.endsWith("ん")) {
					e.get_discord().getMessage().reply("こ").queue();
				} else {
					e.get_discord().getMessage().reply("んこ").queue();
				}
			} else {
				e.get_discord().getMessage().reply("そういうのよくないと思うよ。").queue();
			}
		}
	}
}
