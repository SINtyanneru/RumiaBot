package com.rumisystem.rumiabot;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

public class ProcessStart implements Runnable {
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
			//外部アプリケーションのプロセスを起動
			ProcessBuilder PB = new ProcessBuilder(APP, ARG);
			Process PROCESS = PB.start();

			Main.LOG(TAG, "Start " + APP + " " + ARG, 0);

			//標準出力の読み取る用のスレッド
			Thread OUT_TH = new Thread(() -> {
				try (BufferedReader READER = new BufferedReader(new InputStreamReader(PROCESS.getInputStream()))) {
					String LINE;
					while ((LINE = READER.readLine()) != null) {
						Main.LOG(" INFO  | " + TAG, LINE.replace("\u001B", "￼"), 0);
					}
				} catch (IOException e) {
					e.printStackTrace();
				}
			});

			//エラー出力の読み取る用のスレッド
			Thread ERR_TH = new Thread(() -> {
				try (BufferedReader ERR_READER = new BufferedReader(new InputStreamReader(PROCESS.getErrorStream()))) {
					String ERR_LINE;
					while ((ERR_LINE = ERR_READER.readLine()) != null) {
						Main.LOG(" ERR   | " + TAG, ERR_LINE.replace("\u001B", "￼"), 1);
					}
				} catch (IOException e) {
					e.printStackTrace();
				}
			});

			//スレッドを開始
			OUT_TH.start();
			ERR_TH.start();

			//プロセスが終了するまで待機
			int EXIT_CODE = PROCESS.waitFor();
			PROCESS.destroy();
			System.out.println(TAG + " a EXIT:" + EXIT_CODE);
		}catch (Exception EX){
			EX.printStackTrace();
		}
	}
}
