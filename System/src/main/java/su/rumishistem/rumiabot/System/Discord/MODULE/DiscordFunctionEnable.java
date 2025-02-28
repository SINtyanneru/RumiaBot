package su.rumishistem.rumiabot.System.Discord.MODULE;

import su.rumishistem.rumi_java_lib.SQL;
import su.rumishistem.rumiabot.System.TYPE.DiscordFunction;

import java.sql.SQLException;

import su.rumishistem.rumi_java_lib.ArrayNode;

public class DiscordFunctionEnable {
	public static void GuildSetting(boolean Enable, String GuildID, DiscordFunction Function) throws SQLException {
		boolean SettingExists = Exists(GuildID, "", Function);

		if (Enable) {
			if (!SettingExists) {
				//有効化する
				SQL.UP_RUN("INSERT INTO `CONFIG` (`GID`, `CID`, `FUNC_ID`) VALUES (?, '', ?);", new Object[] {
					GuildID, Function.name()
				});
			}
		} else {
			if (SettingExists) {
				//無効化する
				SQL.UP_RUN("DELETE FROM `CONFIG` WHERE `GID` = ? AND `CID` = '' AND `FUNC_ID` = ?;", new Object[] {
					GuildID, Function.name()
				});
			}
		}
	}
	
	private static boolean Exists(String GuildID, String ChannnelID, DiscordFunction Function) {
		ArrayNode RESULT = SQL.RUN("SELECT * FROM `CONFIG` WHERE `GID` = ? AND `CID` = ? AND `FUNC_ID` = ?;", new Object[] {
			GuildID, ChannnelID, Function.name()
		});

		if (RESULT.asArrayList().size() == 1) {
			return true;
		} else {
			return false;
		}
	}
}
