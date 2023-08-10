package com.rumisystem.rumiabot;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.channel.concrete.TextChannel;

import java.awt.*;
import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.file.Paths;
import java.util.Timer;
import java.util.TimerTask;

import static com.rumisystem.rumiabot.Main.*;

public class MISSKEY {
	private static Timer TIMER;
	private static TimerTask TASK;
	private static String REQUEST_RESULT = null;
	private static String TEMP_NOTE_ID = "";

	public static void Main(){
		TIMER = new Timer();
		TASK = new TimerTask() {
			@Override
			public void run(){
				try {
					// URLを作成
					URL URL_OBJ = new URL("https://ussr.rumiserver.com/api/users/notes");

					// HttpURLConnectionを作成
					HttpURLConnection connection = (HttpURLConnection) URL_OBJ.openConnection();
					connection.setRequestMethod("POST");
					connection.setRequestProperty("Content-Type", "application/json");
					connection.setDoOutput(true); // POSTデータを送信するために必要

					String postData = "{\"userId\":\"9i642yz0h7\",\"i\":\"0wmcVp8aNuBRZD8lS9E7ArqHNXPZlVtu\"}"; // POSTデータ

					// POSTデータを送信
					try(DataOutputStream outputStream = new DataOutputStream(connection.getOutputStream())){
						outputStream.writeBytes(postData);
						outputStream.flush();
					}

					// レスポンスを取得
					int responseCode = connection.getResponseCode();

					if(responseCode == HttpURLConnection.HTTP_OK){
						// レスポンスの入力ストリームを取得
						BufferedReader reader = new BufferedReader(new InputStreamReader(connection.getInputStream()));
						String line;
						StringBuilder response = new StringBuilder();

						// HTMLコンテンツ全体を取得
						while ((line = reader.readLine()) != null) {
							response.append(line);
						}

						String RESPONSE_TEXT = response.toString();

						if(REQUEST_RESULT == null){//REQUEST_RESULTが空なら
							//セット
							REQUEST_RESULT = RESPONSE_TEXT;
							ObjectMapper objectMapper = new ObjectMapper();
							JsonNode json = objectMapper.readTree(RESPONSE_TEXT);
							SEND_MISSKEY_NOTE(json.get(0));
						}else{
							if(!REQUEST_RESULT.equals(RESPONSE_TEXT)){
								LOG_OUT("[ MISSKEY ]NOTES UPDATE");
								REQUEST_RESULT = RESPONSE_TEXT;

								ObjectMapper objectMapper = new ObjectMapper();
								JsonNode json = objectMapper.readTree(RESPONSE_TEXT);
								SEND_MISSKEY_NOTE(json.get(0));
							}
						}

						// ストリームと接続を閉じる
						reader.close();
					}else{
						LOG_OUT("[ ERR ]HTTP REQUEST ERR BY MI API:" + responseCode);
					}

					// 接続を閉じる
					connection.disconnect();
				}catch(IOException ex){
					LOG_OUT("[ ERR ]HTTP REQUEST ERR BY MI API");
				}
			}
		};

		// 1000ミリ秒後から5000ミリ秒間隔でタスクを実行します
		TIMER.schedule(TASK, 5000, 5000);
	}

	private static void SEND_MISSKEY_NOTE(JsonNode NOTE){
		if(!TEMP_NOTE_ID.equals(NOTE.get("id").textValue())){
			if(NOTE.get("replyId").textValue() == null){
				TEMP_NOTE_ID = NOTE.get("id").textValue();
				TextChannel TC = jda.getTextChannelById("1128742498194444298");
				if(TC != null && NOTE != null){
					EmbedBuilder EB = new EmbedBuilder();
					EB.setTitle(NOTE.get("user").get("name").textValue());
					EB.setColor(Color.GREEN);
					EB.setUrl("https://ussr.rumiserver.com/@" + NOTE.get("user").get("username").textValue());
					//EB.setTimestamp(NOTE.get("createdAt").deepCopy());
					if(NOTE.get("text").textValue() == null){
						EB.setDescription(NOTE.get("user").get("name").textValue() + "がRNしました\n" + NOTE.get("renote").get("text").textValue()
								+ "\n[見に行く](https://ussr.rumiserver.com/notes/" + NOTE.get("renote").get("id").textValue() +
								")");
					}else{
						EB.setDescription(NOTE.get("text").textValue() + "\n[見に行く](https://ussr.rumiserver.com/notes/" + NOTE.get("id").textValue() + ")");
					}

					TC.sendMessageEmbeds(EB.build()).queue();
				}
			}
		}
	}
}
