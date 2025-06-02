package su.rumishistem.rumiabot.Joke;

import su.rumishistem.rumiabot.System.TYPE.ReceiveMessageEvent;

public class Unko {
	private static final int BaiBain = 5;

	public static void Main(ReceiveMessageEvent e) {
		String Content = e.GetMessage().GetText();
		String ReturnContent = null;

		if (Content.endsWith("う")) {
			ReturnContent = "んこ";

			if (e.GetMessage().GetDiscordGuild().getId().equals("1377631640662315080")) {
				for (int I = 0; I < BaiBain; I++) {
					ReturnContent += "\nんこ";
				}
			}
		}

		if (Content.endsWith("うん")) {
			ReturnContent = "こ";

			if (e.GetMessage().GetDiscordGuild().getId().equals("1377631640662315080")) {
				for (int I = 0; I < BaiBain; I++) {
					ReturnContent += "\nこ";
				}
			}
		}

		if (ReturnContent != null) {
			e.GetMessage().GetDiscordChannel().sendMessage(ReturnContent).queue();
		}
	}
}
