package com.rumisystem.rumiabot;

import net.dv8tion.jda.api.entities.channel.concrete.TextChannel;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;

import java.util.ArrayList;
import java.util.Timer;
import java.util.TimerTask;

import static com.rumisystem.rumiabot.Main.jda;

public class RUMISAN_SERVER {
	public static ArrayList<String> TEXT = new ArrayList<>(){{
		add("28箇所の刺し傷だぞ！");
		add("Θά 'ρθεις σαν αστραπή");
		add("Θούριος");
		add("Урааааааааааааааааа!!:Soviet:");
		add("Town-0 Phase-5");
		add("Μην παραχαράζετε");
		add("EXEC_CHRONICLE=KEY⧸.");
		add("Ще не вмерла України");
		add("教育教育教育教育死刑死刑死刑死刑");
		add("https://rumiserver.com");
		add("https://rumia.me");
		add("https://まひろ.net");
		add("https://kurisaba.xyz");
		add("これは、ぐへへです。: ");
		add("シコシコはしません、手を添えればピューっと");
		add("パンパンはしません、挿入すればドピュっと");
	}};
	private static int TIME = 10000;
	private static Timer TIMER;
	private static TimerTask TASK;
	private static TextChannel CH;

	public static void Main(){
		CH = jda.getTextChannelById("1141244219441303613");
		final String[] TEMP_TEXT = {""};

		if(CH != null){
			TIMER = new Timer();
			TASK = new TimerTask() {
				@Override
				public void run() {
					try{
						int RND = (int)Math.ceil(Math.random() * TEXT.size());
						if(TEXT.get(RND) != null){
							if(TEMP_TEXT[0] != TEXT.get(RND)){
								CH.sendMessage(TEXT.get(RND)).queue();
								TEMP_TEXT[0] = TEXT.get(RND);
							}

						}
					}catch(Exception EX){
						CH.sendMessage(EX.getMessage()).queue();
					}

				}
			};

			// 1000ミリ秒後から5000ミリ秒間隔でタスクを実行します
			TIMER.schedule(TASK, TIME, TIME);
		}
	}
}
