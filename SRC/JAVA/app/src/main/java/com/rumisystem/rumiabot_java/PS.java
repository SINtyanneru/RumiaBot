package com.rumisystem.rumiabot_java;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.net.Socket;
import java.time.Instant;
import java.util.UUID;

public class PS {
	private static PrintWriter PW;

	public static void main(int PORT) {
		try{
			System.out.println("Telnetに接続します...");

			String HOST = "localhost";

			Socket SOCKET = new Socket(HOST, PORT);

			System.out.println("Telnetに接続しました");

			BufferedReader BR = new BufferedReader(new InputStreamReader(SOCKET.getInputStream()));

			PW = new PrintWriter(SOCKET.getOutputStream(), true);

			SEND("HELLO;JAVA");

			String LINE;
			while((LINE = BR.readLine()) != null){
				System.out.println(LINE);
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
