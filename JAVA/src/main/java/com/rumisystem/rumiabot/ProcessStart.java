package com.rumisystem.rumiabot;

import java.io.BufferedReader;
import java.io.InputStreamReader;

public class ProcessStart extends Thread {
	private String TAG;
	private String APP;
	private String ARG;

	public ProcessStart(String TAG, String APP, String ARG){
		this.TAG = TAG;
		this.APP = APP;
		this.ARG = ARG;
	}

	public void run(){
		try{
			// 外部アプリケーションのプロセスを起動
			ProcessBuilder processBuilder = new ProcessBuilder(APP, ARG);
			Process process = processBuilder.start();

			// 外部アプリケーションの出力を読み取るための BufferedReader を作成
			BufferedReader reader = new BufferedReader(new InputStreamReader(process.getInputStream()));

			// 外部アプリケーションの出力をコンソールに出力
			String line;
			while ((line = reader.readLine()) != null) {
				System.out.println(line);
			}

			// プロセスが終了するまで待機
			int exitCode = process.waitFor();
			System.out.println("外部アプリケーションの終了コード: " + exitCode);
		}catch (Exception EX){
			EX.printStackTrace();
		}
	}
}
