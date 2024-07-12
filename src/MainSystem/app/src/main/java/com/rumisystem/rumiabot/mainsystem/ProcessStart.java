package com.rumisystem.rumiabot.mainsystem;

import static com.rumisystem.rumi_java_lib.LOG_PRINT.Main.LOG;
import com.rumisystem.rumi_java_lib.LOG_PRINT.LOG_TYPE;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

public class ProcessStart implements Runnable {
	private String TAG;
	private String[] ARG;

	public ProcessStart(String TAG, String[] ARG){
		this.TAG = TAG;
		this.ARG = ARG;
	}

	public void run(){
		try{
			//外部アプリケーションのプロセスを起動
			ProcessBuilder PB = new ProcessBuilder(ARG);
			Process PROCESS = PB.start();

			LOG(LOG_TYPE.OK, TAG + "|Start " + ARG[0]);

			//標準出力の読み取る用のスレッド
			Thread OUT_TH = new Thread(() -> {
				try (BufferedReader READER = new BufferedReader(new InputStreamReader(PROCESS.getInputStream()))) {
					String LINE;
					while ((LINE = READER.readLine()) != null) {
						LOG(LOG_TYPE.INFO, TAG + "|" + LINE.replace("\u001B", "￼"));
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
						LOG(LOG_TYPE.FAILED, TAG + "|" + ERR_LINE.replace("\u001B", "￼"));
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
