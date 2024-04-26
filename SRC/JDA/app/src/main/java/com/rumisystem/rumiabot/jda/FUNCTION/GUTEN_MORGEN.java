package com.rumisystem.rumiabot.jda.FUNCTION;

import net.dv8tion.jda.api.entities.channel.Channel;
import net.dv8tion.jda.api.entities.channel.concrete.TextChannel;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Locale;

import static com.rumisystem.rumiabot.jda.MODULE.FUNCTION.GET_FUNCTION_TRUE_CHANNEL;

public class GUTEN_MORGEN {
	public static void Main() {
		Thread TH = new Thread(new Runnable() {
			@Override
			public void run() {
				try {
					while (true){
						LocalDateTime DATETIME = LocalDateTime.now();
						String NOW_TIME = DATETIME.format(DateTimeFormatter.ofPattern("ah:m", Locale.GERMANY));
						if(NOW_TIME.equals("AM6:30")){
							//挨拶が有効化サれているチャンネルを取得
							for(Channel CHANNEL:GET_FUNCTION_TRUE_CHANNEL("guten_morgen")){
								TextChannel TCH = (TextChannel) CHANNEL;

								//送信するメッセージ
								StringBuilder TEXT = new StringBuilder();
								TEXT.append("みんなおはよう！！！\n");
								TEXT.append("今日は" + DATETIME.format(DateTimeFormatter.ofPattern("yyyy年MM月dd日E曜日")) + "だよ！\n");
								switch (DATETIME.format(DateTimeFormatter.ofPattern("E"))){
									case "日":{
										TEXT.append("今日も一日がんばろう！");
										break;
									}

									case "月":{
										TEXT.append("地獄の一日の始まりだ。。。。");
										break;
									}

									case "火":{
										TEXT.append("今日も一日がんばろう！");
										break;
									}

									case "水":{
										TEXT.append("今日も一日がんばろう！");
										break;
									}

									case "木":{
										TEXT.append("今日と明日頑張れば休み！頑張ろう！");
										break;
									}

									case "金":{
										TEXT.append("今日頑張ったら休み！ニートは労働しろ！");
										break;
									}

									case "土":{
										TEXT.append("今日から休日だああああああああ");
										break;
									}

									default:{
										TEXT.append("今日も一日がんばろう！");
									}
								}
								TEXT.append("\n");

								//送信
								TCH.sendMessage(TEXT.toString()).queue();
							}

							//スパムしてしまうので（） 2分待つ
							Thread.sleep(120000);
						}
					}
				} catch (Exception EX) {
					throw new RuntimeException(EX);
				}
			}
		});

		TH.start();
	}
}
