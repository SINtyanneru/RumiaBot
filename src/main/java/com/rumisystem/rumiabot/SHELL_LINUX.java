package com.rumisystem.rumiabot;

import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;

import static com.rumisystem.rumiabot.Main.LOG_OUT;

public class SHELL_LINUX {
	private static MessageReceivedEvent MSG_EVENT;
	private static Message MSG;
	private static boolean TASK_COMP = false;
	private static Timer TIMER;
	private static TimerTask TASK;
	private static StringBuilder OUTPUT;
	public static void Main(MessageReceivedEvent e){
		if(e.getAuthor().getId().equals("564772363950882816")){
			MSG_EVENT = e;
			MSG = e.getChannel().sendMessage("実行中").complete();

			OUTPUT = new StringBuilder();//初期化
			TASK_COMP = false;

			CompletableFuture<Void> SHELL_RUN = CompletableFuture.runAsync(() -> {
				try {
					String[] command = new String[3];
					command[0] = "sh";
					command[1] = "-c";
					command[2] = e.getMessage().getContentRaw().replace("r.shell", "");

					// コマンドを実行しプロセスを取得します
					Process process = Runtime.getRuntime().exec(command);

					// コマンドの出力ストリームを取得します
					InputStream inputStream = process.getInputStream();
					BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream));

					// コマンドの出力を読み取って表示します
					String line;
					while ((line = reader.readLine()) != null) {
						System.out.println(line);
						OUTPUT.append(line + "\n");
					}

					// コマンドの終了結果を取得します
					int exitCode = process.waitFor();
					System.out.println("コマンドの終了コード: " + exitCode);
					OUTPUT.append("EXIT CODE:" + exitCode);

					TASK_COMP = true;
				} catch (IOException | InterruptedException EX) {
					MSG.editMessage("```" + EX.getMessage() + "```").queue();
				}
			});

			TIMER = new Timer();
			TASK = new TimerTask() {
				@Override
				public void run() {
					LOG_OUT("更新");
					//Discordに出す
					MSG.editMessage("```" + OUTPUT + "```").queue();

					if(TASK_COMP){
						TIMER.cancel();
						TASK.cancel();
					}
				}
			};

			// 1000ミリ秒後から5000ミリ秒間隔でタスクを実行します
			TIMER.schedule(TASK, 1000, 1000);
			SHELL_RUN.join();
		}else{
			e.getMessage().reply("WHO IS YOU").queue();
		}
	}
}
