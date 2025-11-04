package su.rumishistem.rumiabot.System.Type.DiscordFunction;

public enum DiscordChannelFunction {
	guten_morgen,
	welcomemessage,
	fuckyoumessage;

	public static DiscordChannelFunction get_from_name(String name) {
		for (DiscordChannelFunction f:DiscordChannelFunction.values()) {
			if (f.name().equalsIgnoreCase(name)) {
				return f;
			}
		}

		throw new RuntimeException("ない");
	}
}
