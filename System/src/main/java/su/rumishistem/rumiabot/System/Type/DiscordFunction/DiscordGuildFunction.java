package su.rumishistem.rumiabot.System.Type.DiscordFunction;

public enum DiscordGuildFunction {
	vxtwitter,
	fxtwitter,
	aichan,
	Joke;

	public static DiscordGuildFunction get_from_name(String name) {
		for (DiscordGuildFunction f:DiscordGuildFunction.values()) {
			if (f.name().equalsIgnoreCase(name)) {
				return f;
			}
		}

		throw new RuntimeException("ない");
	}
}
