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
			ProcessBuilder PB = new ProcessBuilder(APP, ARG);
			Process PROCESS = PB.start();

			// 外部アプリケーションの出力を読み取るための BufferedReader を作成
			BufferedReader READER = new BufferedReader(new InputStreamReader(PROCESS.getInputStream()));

			// 外部アプリケーションの出力をコンソールに出力
			String LINE;
			while ((LINE = READER.readLine()) != null) {
				Main.LOG(TAG, LINE);
			}

			// プロセスが終了するまで待機
			int EXIT_CODE = PROCESS.waitFor();
			System.out.println(TAG + "a EXIT:" + EXIT_CODE);
		}catch (Exception EX){
			EX.printStackTrace();
		}
	}
}
