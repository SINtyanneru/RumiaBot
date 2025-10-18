package su.rumishistem.rumiabot.Joke;

import su.rumishistem.rumiabot.System.Type.ReceiveMessageEvent;

public class Unko {
	public static void Main(ReceiveMessageEvent e) {
		String Content = e.get_discord().getMessage().getContentRaw();
		String ReturnContent = null;

		//う→んこ(ち)
		if (Content.endsWith("う")) {
			ReturnContent = "んこ";
			if (TeiKakuricu.get()) {
				ReturnContent = "んち𝅙";
			}
		}

		//うん/うーん→こ(ち)
		if (Content.matches("うー*ん$")) {
			ReturnContent = "こ";
			if (TeiKakuricu.get()) {
				ReturnContent = "ち𝅙";
			}
		}

		//おん/おーん→こ(ち)
		if (Content.matches("おー*ん$")) {
			ReturnContent = "こ";
			if (TeiKakuricu.get()) {
				ReturnContent = "ち𝅙";
			}
		}

		if (ReturnContent != null) {
			e.get_discord().getChannel().asTextChannel().sendMessage(ReturnContent).queue();
		}
	}
}
