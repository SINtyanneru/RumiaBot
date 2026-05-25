package su.rumishistem.rumisanbot.base_system.Tool;

public class DiscordMessageID {
	/**
	 * @return 0はギルドID 1はチャンネルID 2はメッセージID
	 */
	public static String[] parse(String id) {
		if (id.startsWith("!DS") == false) throw new RuntimeException("Discordの物ではないID");

		String[] result = new String[3];

		id = id.substring(3);
		String[] parts = id.split("::");

		result[0] = parts[1];
		result[1] = parts[2];
		result[2] = parts[0];

		return result;
	}
}
