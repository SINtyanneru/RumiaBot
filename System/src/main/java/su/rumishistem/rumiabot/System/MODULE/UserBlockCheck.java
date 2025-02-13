package su.rumishistem.rumiabot.System.MODULE;

import static su.rumishistem.rumiabot.System.Main.CONFIG_DATA;

public class UserBlockCheck {
	public static boolean isBlock(String UID) {
		if (CONFIG_DATA.get("BLOCK").getData("DISCORD").asString().contains(UID)) {
			return true;
		} else {
			return false;
		}
	}
}
