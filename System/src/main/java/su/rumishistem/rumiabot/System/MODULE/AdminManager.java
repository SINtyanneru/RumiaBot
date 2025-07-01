package su.rumishistem.rumiabot.System.MODULE;

import java.sql.SQLException;
import java.util.HashMap;

import su.rumishistem.rumi_java_lib.ArrayNode;
import su.rumishistem.rumi_java_lib.SQL;
import su.rumishistem.rumiabot.System.TYPE.SourceType;

public class AdminManager {
	private static HashMap<String, String> MisskeyAdmin = new HashMap<String, String>();
	private static HashMap<String, String> DiscordAdmin = new HashMap<String, String>();

	public static void Init() throws SQLException {
		ArrayNode RESULT = SQL.RUN("SELECT * FROM `ADMIN` ", new Object[] {});
		for (int I = 0; I < RESULT.asArrayList().size(); I++) {
			ArrayNode ROW = RESULT.get(I);

			switch (ROW.getData("TYPE").asString()) {
				case "Discord": {
					DiscordAdmin.put(ROW.getData("UID").asString(), ROW.getData("PERMISSION").asString());
					break;
				}

				case "Misskey": {
					MisskeyAdmin.put(ROW.getData("UID").asString(), ROW.getData("PERMISSION").asString());
					break;
				}
			}
		}
	}

	public static boolean IsAdmin(SourceType Type, String UID) {
		switch (Type) {
			case Discord: {
				return DiscordAdmin.get(UID) != null;
			}

			case Misskey: {
				return MisskeyAdmin.get(UID) != null;
			}

			default: {
				return false;
			}
		}
	}
}
