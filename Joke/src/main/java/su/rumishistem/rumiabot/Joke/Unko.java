package su.rumishistem.rumiabot.Joke;

import su.rumishistem.rumiabot.System.TYPE.ReceiveMessageEvent;

public class Unko {
	public static void Main(ReceiveMessageEvent e) {
		String Content = e.GetMessage().GetText();

		if (Content.endsWith("う")) {
			e.GetMessage().GetDiscordChannel().sendMessage("んこ").queue();
		}

		if (Content.endsWith("うん")) {
			e.GetMessage().GetDiscordChannel().sendMessage("こ").queue();
		}
	}
}
