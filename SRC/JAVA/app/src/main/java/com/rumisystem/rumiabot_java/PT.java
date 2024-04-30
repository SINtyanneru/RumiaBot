package com.rumisystem.rumiabot_java;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.InputStream;
import java.io.PrintWriter;
import java.net.Socket;
import java.nio.charset.StandardCharsets;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.Instant;
import java.util.UUID;

import static com.rumisystem.rumiabot_java.Main.ARRAY_JSON_TO_ARRAYLIST;

public class PT {
	private static PrintWriter PW;

	public static void main(int PORT) {
		try{
			System.out.println("Telnetに接続します...");

			String HOST = "localhost";

			Socket SOCKET = new Socket(HOST, PORT);

			System.out.println("Telnetに接続しました");

			InputStream INPUT_STREAM = SOCKET.getInputStream();

			PW = new PrintWriter(SOCKET.getOutputStream(), true);

			SEND("HELLO;JAVA");

			byte[] BUFFER = new byte[1024];
			int BYTES_READ;
			while ((BYTES_READ = INPUT_STREAM.read(BUFFER)) != -1) {
				String MSG = new String(BUFFER, 0, BYTES_READ, StandardCharsets.UTF_8);
				String[] CMD = MSG.split(";");
				switch (CMD[1]){
					case "SQL":{
						ResultSet RESULT = SQL.RUN(CMD[2], ARRAY_JSON_TO_ARRAYLIST(CMD[3]).toArray());

						for(Object ROW:ARRAY_JSON_TO_ARRAYLIST(CMD[3]).toArray()){
							System.out.println(ROW.toString());
						}

						if(RESULT != null){
							//実行して取得成功
							JsonNode JSON_RESULT = SQL.SQL_RESULT_TO_JSON(RESULT);
							PW.print(CMD[0] + ";" + new ObjectMapper().writeValueAsString(JSON_RESULT) + ";200");
							PW.flush();
						} else {
							//Nullの場合の処理
							PW.print(CMD[0] + ";404");
							PW.flush();
						}
						break;
					}

					case "SQL_UP":{
						try{
							SQL.UP_RUN(CMD[2], ARRAY_JSON_TO_ARRAYLIST(CMD[3]).toArray());

							//実行して取得成功
							PW.print(CMD[0] + ";200");
							PW.flush();
						}catch (SQLException EX){
							EX.printStackTrace();

							PW.print(CMD[0] + ";" + EX.getSQLState() + ";500");
							PW.flush();
						}
						break;
					}
				}
			}
		} catch (Exception EX) {
			System.err.println("TELNETエラー");
			EX.printStackTrace();
		}
	}

	public static void SEND(String ARGS){
		Instant NOW_TIME = Instant.now();
		UUID GEN_UUID = UUID.nameUUIDFromBytes(NOW_TIME.toString().getBytes());

		PW.print(GEN_UUID + ";" + ARGS);
		PW.flush();
	}
}
