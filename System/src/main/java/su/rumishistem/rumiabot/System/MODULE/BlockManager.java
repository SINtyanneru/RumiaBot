package su.rumishistem.rumiabot.System.MODULE;

import java.sql.SQLException;
import java.util.HashMap;

import su.rumishistem.rumi_java_lib.ArrayNode;
import su.rumishistem.rumi_java_lib.SQL;
import su.rumishistem.rumiabot.System.TYPE.SourceType;

public class BlockManager {
	private static HashMap<String, String> Misskey = new HashMap<String, String>();
	private static HashMap<String, String> Discord = new HashMap<String, String>();

	public static void Init() {
		ArrayNode RESULT = SQL.RUN("SELECT * FROM `BLOCK` ", new Object[] {});
		for (int I = 0; I < RESULT.asArrayList().size(); I++) {
			ArrayNode ROW = RESULT.get(I);

			switch (ROW.getData("TYPE").asString()) {
				case "Discord": {
					Discord.put(ROW.getData("UID").asString(), ROW.getData("PERMISSION").asString());
					break;
				}

				case "Misskey": {
					Misskey.put(ROW.getData("UID").asString(), ROW.getData("REASON").asString());
					break;
				}
			}
		}
	}

	public static boolean addBlock(SourceType Type, String UID) throws SQLException {
		SQL.UP_RUN("INSERT INTO `BLOCK` (`UID`, `TYPE`, `REASON`) VALUES(?, ?, '');", new Object[] {UID, Type.name()});

		switch (Type) {
			case Discord: {
				Discord.put(UID, "");
			}

			case Misskey: {
				Misskey.put(UID, "");
			}

			default: {
				return false;
			}
		}
	}

	public static boolean IsBlocked(SourceType Type, String UID) {
		switch (Type) {
			case Discord: {
				return Discord.get(UID) != null;
			}

			case Misskey: {
				return Misskey.get(UID) != null;
			}

			default: {
				return false;
			}
		}
	}
}
