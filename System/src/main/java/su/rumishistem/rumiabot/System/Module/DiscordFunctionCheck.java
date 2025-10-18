package su.rumishistem.rumiabot.System.Module;

import java.sql.SQLException;

import su.rumishistem.rumi_java_lib.ArrayNode;
import su.rumishistem.rumi_java_lib.SQL;
import su.rumishistem.rumiabot.System.Type.DiscordFunction.DiscordGuildFunction;

public class DiscordFunctionCheck {
	public static boolean guild(String guild_id, DiscordGuildFunction function) throws SQLException {
		ArrayNode SQL_RESULT = SQL.RUN("SELECT * FROM `CONFIG` WHERE `GID` = ? AND `FUNC_ID` = ? AND `CID` = '';", new Object[] {
			guild_id,
			function.name()
		});
		if (SQL_RESULT.asArrayList().size() == 1) {
			return true;
		} else {
			return false;
		}
	}
}
