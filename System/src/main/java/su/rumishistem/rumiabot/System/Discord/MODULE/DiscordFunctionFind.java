package su.rumishistem.rumiabot.System.Discord.MODULE;

import su.rumishistem.rumiabot.System.TYPE.DiscordChannelFunction;
import su.rumishistem.rumiabot.System.TYPE.DiscordFunction;

public class DiscordFunctionFind {
	public static DiscordFunction Find(String Name) {
		for (DiscordFunction Function:DiscordFunction.values()) {
			if (Function.name().equals(Name)) {
				return Function;
			}
		}

		return null;
	}

	public static DiscordChannelFunction FindChannel(String Name) {
		for (DiscordChannelFunction Function:DiscordChannelFunction.values()) {
			if (Function.name().equals(Name)) {
				return Function;
			}
		}

		return null;
	}
}
