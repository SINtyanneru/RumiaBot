package com.rumisystem.rumiabot;

import java.sql.*;

import static com.rumisystem.rumiabot.Main.LOG_OUT;

public class SQL{
	public static Connection SQL_CON = null;
	public static void Main(){
		// MySQL接続情報
		String url = "jdbc:mysql://" + Main.SQL_HOST + ":3306/RumiaBOT"; // db_nameには接続したいデータベース名を指定
		String user = Main.SQL_USER; // ユーザ名
		String password = Main.SQL_PASS; // パスワード

		try {
			// JDBCドライバをロード
			Class.forName("com.mysql.cj.jdbc.Driver");

			// データベースに接続
			SQL_CON = DriverManager.getConnection(url, user, password);

			LOG_OUT("[ SQL ][ OK ]SQL Connected!");

		} catch (SQLException | ClassNotFoundException E) {
			LOG_OUT("[ SQL ][ ERR ]SQL ConnectionERR:" + E.getMessage());
		}
	}

	public static ResultSet SQL_RUN(String SQL_CODE) throws SQLException {
		PreparedStatement preparedStatement = SQL_CON.prepareStatement(SQL_CODE);
		preparedStatement.setString(1, "RumiaBOT"); // テーブルが所属するデータベース名を指定

		// クエリの実行

		// リソースの解放
		//preparedStatement.close();
		//resultSet.close();

		return preparedStatement.executeQuery();
	}
}
