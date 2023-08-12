package com.rumisystem.rumiabot.Command;

import com.fasterxml.jackson.core.JsonProcessingException;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.InetAddress;
import java.util.concurrent.CompletableFuture;

public class PING {
	public static void Main(SlashCommandInteractionEvent e){
		e.deferReply().queue();//ちょっとまってねをする

		String IP = e.getOption("ip").getAsString();//IPアドレス
		StringBuilder LOG = new StringBuilder();//ログ

		CompletableFuture<Void> pingFuture = CompletableFuture.runAsync(() -> {
			try {
				LOG.append("PINGを送信：").append(IP).append("\n");//開始を宣言

				Process process = Runtime.getRuntime().exec("ping " + IP + " -c5");//-c5で5回までにできるぞ、LINUXだと必須
				BufferedReader reader = new BufferedReader(new InputStreamReader(process.getInputStream()));//返答をよみとる

				String line;
				while ((line = reader.readLine()) != null) {
					LOG.append(line).append("\n");
					e.getHook().editOriginal(LOG.toString()).queue();
				}

				//完了
				int exitCode = process.waitFor();
				if(exitCode == 0){//返り値が0か
					LOG.append("\n返り値が0だから、成功したんじゃない？");
				}else{
					LOG.append("\n返り値が0じゃないから、失敗したかも");
				}

				//最終的なメッセージへの反映
				e.getHook().editOriginal(LOG.toString()).queue();

			} catch (IOException | InterruptedException EX) {
				EX.printStackTrace();
				LOG.append("\nエラーがでちゃった！！！:" + EX.getMessage());
				e.getHook().editOriginal(LOG.toString()).queue();
			}
		});

		pingFuture.join(); //非同期での処理の完了を待つ
		//非同期だから、同時に行うこともできるね！！
	}
}
