package com.rumisystem.rumiabot.jda;

import java.io.*;
import java.net.Socket;
import java.nio.charset.StandardCharsets;
import java.time.Instant;
import java.util.UUID;

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

			SEND("HELLO;JDA");

			byte[] BUFFER = new byte[1024];
			int BYTES_READ;
			while ((BYTES_READ = INPUT_STREAM.read(BUFFER)) != -1) {
				String MSG = new String(BUFFER, 0, BYTES_READ, StandardCharsets.UTF_8);
				String[] CMD = MSG.split(";");
			}
		} catch (Exception EX) {
			System.err.println("TELNETエラー");
			EX.printStackTrace();
		}
	}

	public static void SEND(String ARGS){
		Instant NOW_TIME = Instant.now();
		UUID GEN_UUID = UUID.nameUUIDFromBytes(NOW_TIME.toString().getBytes(StandardCharsets.UTF_8));

		PW.print(GEN_UUID + ";" + ARGS);
		PW.flush();
	}
}
