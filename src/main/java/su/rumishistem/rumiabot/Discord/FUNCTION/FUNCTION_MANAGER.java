package su.rumishistem.rumiabot.Discord.FUNCTION;

import java.sql.SQLException;

import com.rumisystem.rumi_java_lib.ArrayNode;
import com.rumisystem.rumi_java_lib.SQL;

public class FUNCTION_MANAGER {
	/**
	 * 機能設定がすでにされているかチェック
	 * @param GID 鯖ID
	 * @param CID チャンネルID(ないならNull)
	 * @param FUNCTION_ID 機能ID
	 * @return 登録されているならtrue
	 */
	public static boolean FUNCTION_CHECK(String GID, String CID, String FUNCTION_ID) {
		ArrayNode SQL_RESULT = null;

		if (CID == null) {
			//鯖IDのみで検索
			SQL_RESULT = SQL.RUN("SELECT * FROM `CONFIG` WHERE GID = ? AND CID = '' AND FUNC_ID = ?", new String[] {GID, FUNCTION_ID});
		} else {
			//チャンネルIDも含めて検索
			SQL_RESULT =SQL.RUN("SELECT * FROM `CONFIG` WHERE GID = ? AND CID = ? AND FUNC_ID = ?", new String[] {GID, CID, FUNCTION_ID});
		}

		if (!SQL_RESULT.asArrayList().isEmpty()) {
			return true;
		} else {
			return false;
		}
	}

	public static String GET(String GID, String CID, String FUNCTION_ID) {
		ArrayNode SQL_RESULT = null;

		if (CID == null) {
			//鯖IDのみで検索
			SQL_RESULT = SQL.RUN("SELECT * FROM `CONFIG` WHERE GID = ? AND CID = '' AND FUNC_ID = ?", new String[] {GID, FUNCTION_ID});
		} else {
			//チャンネルIDも含めて検索
			SQL_RESULT =SQL.RUN("SELECT * FROM `CONFIG` WHERE GID = ? AND CID = ? AND FUNC_ID = ?", new String[] {GID, CID, FUNCTION_ID});
		}

		if (!SQL_RESULT.asArrayList().isEmpty()) {
			return SQL_RESULT.get(0).asString("ID");
		} else {
			return null;
		}
	}

	public static void REGIST(String GID, String CID, String FUNCTION_ID) throws SQLException {
		//チャンネルIDがないなら鯖IDのみで登録する
		if (CID == null) {
			if (!FUNCTION_MANAGER.FUNCTION_CHECK(GID, null, FUNCTION_ID)) {
				SQL.UP_RUN("INSERT INTO `CONFIG` (`ID`, `GID`, `CID`, `MODE`, `FUNC_ID`) VALUES (NULL, ?, '', 1, ?)", new String[] {GID, FUNCTION_ID});
			} else {
				throw new Error("既に登録済み");
			}
		} else {
			if (!FUNCTION_MANAGER.FUNCTION_CHECK(GID, CID, FUNCTION_ID)) {
				SQL.UP_RUN("INSERT INTO `CONFIG` (`ID`, `GID`, `CID`, `MODE`, `FUNC_ID`) VALUES (NULL, ?, ?, 1, ?)", new String[] {GID, CID, FUNCTION_ID});
			} else {
				throw new Error("既に登録済み");
			}
		}
	}

	public static void DELETE(String ID) throws SQLException {
		//SQL
		SQL.UP_RUN("DELETE FROM CONFIG WHERE `CONFIG`.`ID` = ?", new Object[] {Integer.parseInt(ID)});
	}
}
