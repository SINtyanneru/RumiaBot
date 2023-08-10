package com.rumisystem.rumiabot;

import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;

public class SHELL_LINUX {
	static MessageReceivedEvent MSG_EVENT;
	static Message MSG;
	public static void Main(MessageReceivedEvent e){
		if(e.getAuthor().getId().equals("564772363950882816")){
			MSG_EVENT = e;
			MSG = e.getChannel().sendMessage("実行中").complete();

			try {
				// コマンドを実行しプロセスを取得します
				Process process = Runtime.getRuntime().exec(e.getMessage().getContentRaw().replace("r.shell", ""));

				// コマンドの出力ストリームを取得します
				InputStream inputStream = process.getInputStream();
				BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream));

				// コマンドの出力を読み取って表示します
				String line;
				StringBuilder OUTPUT = new StringBuilder();
				while ((line = reader.readLine()) != null) {
					System.out.println(line);
					OUTPUT.append(line + "\n");
				}

				// コマンドの終了結果を取得します
				int exitCode = process.waitFor();
				System.out.println("コマンドの終了コード: " + exitCode);
				OUTPUT.append("EXIT CODE:" + exitCode);

				//Discordに出す
				MSG.editMessage("```" + OUTPUT + "```").queue();
			} catch (IOException | InterruptedException EX) {
				MSG.editMessage("```" + EX.getMessage() + "```").queue();
			}
		}else{
			e.getMessage().reply("WHO IS YOU").queue();
		}
	}
}
