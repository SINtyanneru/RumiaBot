package com.rumisystem.rumiabot.jda;

import com.rumisystem.rumiabot.jda.COMMAND.VERIFY_PANEL;

import java.io.*;
import java.net.Socket;
import java.nio.charset.StandardCharsets;
import java.time.Instant;
import java.util.*;

public class PT {
	private static PrintWriter PW;
	private static InputStream INPUT_STREAM;
	private static List<String> MESSAGE_LIST = new ArrayList<>();

	public static void main(int PORT) {
		try{
			System.out.println("Telnetに接続します...");

			String HOST = "localhost";

			Socket SOCKET = new Socket(HOST, PORT);

			System.out.println("Telnetに接続しました");

			INPUT_STREAM = SOCKET.getInputStream();
			PW = new PrintWriter(SOCKET.getOutputStream(), true);

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

								//配列に入れる
								MESSAGE_LIST.add(MSG);
								System.out.println("[  PT  ]配列に入れた");

								//1000越えたら消す
								if(MESSAGE_LIST.size() > 1000){
									MESSAGE_LIST.removeFirst();
								}

								//受信した命令を処理する部分
								new Thread(new Runnable() {
									@Override
									public void run() {
										String[] CMD = MSG.split(";");

										if(CMD[1].equals("DISCORD")){
											if(CMD[2].equals("VERIFY_PANEL_OK")){
												if(VERIFY_PANEL.VERIFY(CMD[3])){
													REPLY(CMD[0] + ";200");
												} else {
													REPLY(CMD[0] + ";500");
												}
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
								}).start();
							}
						}
					}catch (Exception EX){
						EX.printStackTrace();
					}
				}
			}).start();

			//へろー
			SEND("HELLO;JDA");
		} catch (Exception EX) {
			System.err.println("TELNETエラー");
			EX.printStackTrace();
		}
	}

	public static String REPLY_WAIT(String ID) {
		//繰り返す
		while (true) {
			try{
				for(String MSG:MESSAGE_LIST){
					//応答のIDが一致するまで待つ
					if(MSG.split(";")[0].equals(ID)){
						System.out.println("受信：" + MSG);
						return MSG.replace(ID + ";", "");
					}
				}
			}catch (Exception EX){
				//エラーを握り潰す
				System.out.flush();
			}
		}
	}

	public static String SEND(String ARGS) {
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
