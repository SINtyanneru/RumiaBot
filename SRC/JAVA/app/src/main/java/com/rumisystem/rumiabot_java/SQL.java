package com.rumisystem.rumiabot_java;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import org.mariadb.jdbc.Connection;
import org.mariadb.jdbc.Statement;

import java.sql.*;
import java.util.ArrayList;
import java.util.concurrent.*;

import static com.rumisystem.rumiabot_java.Main.LOG;

public class SQL {
	public static Connection CONNECT = null;
	public static PreparedStatement STMT = null;

	SQL(){
		//接続文字列
		String URL = "jdbc:mariadb://" + CONFIG.CONFIG_DATA.get("SQL").get("SQL_HOST").asText() + ":3306/" + CONFIG.CONFIG_DATA.get("SQL").get("SQL_DB").asText();
		String USER = CONFIG.CONFIG_DATA.get("SQL").get("SQL_USER").asText();
		String PASS = CONFIG.CONFIG_DATA.get("SQL").get("SQL_PASS").asText();

		try {
			//MariaDBへ接続
			CONNECT = (Connection) DriverManager.getConnection(URL, USER, PASS);

			//自動コミットOFF
			CONNECT.setAutoCommit(false);

			LOG(0, "SQL", "Connected");

			ScheduledExecutorService SCHEDULER = Executors.newScheduledThreadPool(1);

			//1秒ごとに実行されるタスク
			Runnable TASK = () -> {
				LOG(0, "SQL", "PING");

				RUN("SHOW TABLES;", new Object[]{});
			};

			SCHEDULER.scheduleAtFixedRate(TASK, 0, 5, TimeUnit.HOURS);
		} catch (SQLException e) {
			//エラー
			LOG(2, "SQL", "Error");
			e.printStackTrace();
		}
	}

	public static ResultSet RUN(String SQL_SCRIPT, Object[] PARAMS) {
		//SQLの実行結果を入れる変数
		ResultSet SQL_RESULT = null;

		try{
			//SELECT文の実行
			STMT = CONNECT.prepareStatement(SQL_SCRIPT);

			for(int I = 0; I < PARAMS.length; I++){
				Object PARAM = PARAMS[I];
				//型に寄って動作をかえる
				if(PARAM instanceof String){//Stringなら
					STMT.setString(I + 1, PARAM.toString());
				}

				if(PARAM instanceof Integer){//Intなら
					STMT.setInt(I + 1, Integer.parseInt((String) PARAM.toString()));
				}
			}

			SQL_RESULT = STMT.executeQuery();

			return SQL_RESULT;
		} catch (SQLException e) {
			//エラー
			LOG(2, "SQL", "Error");
			e.printStackTrace();
			return null;
		}
	}

	public static void UP_RUN(String SQL_SCRIPT, Object[] PARAMS) throws SQLException {
		//SELECT文の実行
		STMT = CONNECT.prepareStatement(SQL_SCRIPT);

		for(int I = 0; I < PARAMS.length; I++){
			Object PARAM = PARAMS[I];
			//型に寄って動作をかえる
			if(PARAM instanceof String){//Stringなら
				STMT.setString(I + 1, PARAM.toString());
			}

			if(PARAM instanceof Integer){//Intなら
				STMT.setInt(I + 1, Integer.parseInt((String) PARAM.toString()));
			}
		}

		// 実行
		int rowsAffected = STMT.executeUpdate();

		CONNECT.commit();
	}

	public static JsonNode SQL_RESULT_TO_JSON(ResultSet RESULT_SET) throws SQLException{
		ObjectMapper OM = new ObjectMapper();
		ArrayNode ARRAY_NODE = OM.createArrayNode();

		ResultSetMetaData META_DATA = RESULT_SET.getMetaData();
		int COLUM_COUNT = META_DATA.getColumnCount();

		while (RESULT_SET.next()) {
			ObjectNode JSON_OBJ = OM.createObjectNode();

			for (int I = 1; I <= COLUM_COUNT; I++) {
				String COLUM_NAME = META_DATA.getColumnName(I);
				Object VAL = RESULT_SET.getObject(I);
				JSON_OBJ.put(COLUM_NAME, VAL.toString()); // Assuming everything is converted to String
			}

			ARRAY_NODE.add(JSON_OBJ);
		}

		return ARRAY_NODE;
	}
}
