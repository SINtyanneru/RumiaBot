package com.rumisystem.rumiabot.jda;

import java.io.*;
import java.net.Socket;
import java.nio.charset.StandardCharsets;
import java.time.Instant;
import java.util.Arrays;
import java.util.UUID;

public class PT {
	private static PrintWriter PW;
	private static InputStream INPUT_STREAM;

	public static void main(int PORT) {
		try{
			System.out.println("Telnetに接続します...");

			String HOST = "localhost";

			Socket SOCKET = new Socket(HOST, PORT);

			System.out.println("Telnetに接続しました");

			INPUT_STREAM = SOCKET.getInputStream();
			PW = new PrintWriter(SOCKET.getOutputStream(), true);

			SEND("HELLO;JDA");
		} catch (Exception EX) {
			System.err.println("TELNETエラー");
			EX.printStackTrace();
		}
	}

	public static String REPLY_WAIT(String ID) throws IOException {
		byte[] BUFFER = new byte[1024];
		int BYTES_READ;
		while (true) {
			BYTES_READ = INPUT_STREAM.read(BUFFER);
			if(BYTES_READ != -1){
				String MSG = new String(BUFFER, 0, BYTES_READ, StandardCharsets.UTF_8);
				System.out.println("受信：" + MSG);
				//IDが一致するまで待つ
				if(MSG.split(";")[0].equals(ID)){
					System.out.println("来た");
					return MSG.replace(ID + ";", "");
				}
			}
		}
	}

	public static String SEND(String ARGS) throws IOException {
		Instant NOW_TIME = Instant.now();
		UUID GEN_UUID = UUID.nameUUIDFromBytes(NOW_TIME.toString().getBytes(StandardCharsets.UTF_8));

		PW.print(GEN_UUID + ";" + ARGS);
		PW.flush();

		return REPLY_WAIT(GEN_UUID.toString());
	}
}
