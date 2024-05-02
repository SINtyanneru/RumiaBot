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

			new Thread(new Runnable() {
				@Override
				public void run() {
					try{
						//繰り返す
						byte[] BUFFER = new byte[1024];
						int BYTES_READ;
						while (true) {
							BYTES_READ = INPUT_STREAM.read(BUFFER);

							//応答を受信したか
							if(BYTES_READ != -1){
								String MSG = new String(BUFFER, 0, BYTES_READ, StandardCharsets.UTF_8);
								String[] CMD = MSG.split(";");


								if(CMD[1].equals("DISCORD")){
									if(CMD[2].equals("VERIFY_PANEL_OK")){
										System.out.println("200返すわ");
										REPLY(CMD[0] + ";200");
									} else {
										REPLY(CMD[0] + ";SIGN_NOT_FOUND;404");
									}
								} else {
									//これしたら無限ループしたわｗｗｗ
									//理由は簡単、/JAVA/のPTを見てくれ、
									//REPLY_が先頭についてるやつは、全プロセスに送るようにしてるんだけど、
									//此れのせいで無限ループしたｗ
									//REPLY(CMD[0] + ";SIGN_NOT_FOUND;404");
								}
							}
						}
					}catch (Exception EX){
						EX.printStackTrace();
					}
				}
			}).start();
		} catch (Exception EX) {
			System.err.println("TELNETエラー");
			EX.printStackTrace();
		}
	}

	public static String REPLY_WAIT(String ID) throws IOException {
		byte[] BUFFER = new byte[1024];
		int BYTES_READ;

		//繰り返す
		while (true) {
			BYTES_READ = INPUT_STREAM.read(BUFFER);

			//応答を受信したか
			if(BYTES_READ != -1){
				String MSG = new String(BUFFER, 0, BYTES_READ, StandardCharsets.UTF_8);

				System.out.println("受信：" + MSG);

				//応答のIDが一致するまで待つ
				if(MSG.split(";")[0].equals(ID)){
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

	public static void REPLY(String TEXT){
		PW.print(TEXT);
		PW.flush();
	}
}
