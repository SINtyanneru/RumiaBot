package com.rumisystem.rumiabot.jda.COMMAND;

import com.rumisystem.rumiabot.jda.CONFIG;
import com.rumisystem.rumiabot.jda.MODULE.FILE_WRITER;
import com.rumisystem.rumiabot.jda.MODULE.HTTP_REQUEST;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.utils.AttachedFile;

import java.io.File;
import java.net.URLEncoder;
import java.util.UUID;

public class VOICEVOX {
	private static String TEXT;
	private static String SEEKER;
	private static String ID;

	private static final String PATH = "./DOWNLOAD/VOICEVOX/";

	public static void main(SlashCommandInteractionEvent INTERACTION) {
		//処理を開始した時間を記録する
		long START_TIME = System.nanoTime();

		TEXT = INTERACTION.getOption("text").getAsString();
		SEEKER = INTERACTION.getOption("speeker").getAsString();
		ID = UUID.randomUUID().toString();

		String QUERY = GET_QUERY();
		if(QUERY != null){
			if(GENERATE(QUERY)){
				//処理が終了した時間を計算
				long END_MICRO_SEC = ((System.nanoTime() - START_TIME) / 1000000);
				double END_SEC = ((double) END_MICRO_SEC / 1000);

				INTERACTION.getHook().editOriginal("生成した\n(" + END_SEC + "秒で生成完了)").setAttachments(AttachedFile.fromData(new File(PATH + ID + ".wav"))).queue();
			} else {
				INTERACTION.getHook().editOriginal("生成できませんでした").queue();
			}
		} else {
			INTERACTION.getHook().editOriginal("キュー生成できませんでした").queue();
		}
	}

	//キューを生成する
	private static String GET_QUERY(){
		System.out.println("[ VOICEVOX ]VOICEVOXに問い合わせています。。。");

		String RESULT = new HTTP_REQUEST("http://" + CONFIG.CONFIG_DATA.get("VOICEVOX").get("HOST").asText() + ":" + CONFIG.CONFIG_DATA.get("VOICEVOX").get("PORT").asInt() + "/audio_query?text=" + URLEncoder.encode(TEXT) + "&speaker=" + SEEKER).POST("");

		if(RESULT != null){
			System.out.println("[ VOICEVOX ]返答がありました");

			return RESULT;
		} else {
			System.err.println("[ VOICEVOX ]キューを作成できませんでした");
			return null;
		}
	}

	//音声を生成する
	private static boolean GENERATE(String QUERY){
		System.out.println("[ VOICEVOX ]VOICEVOX音声を生成させています。。。");

		boolean RESULT = new HTTP_REQUEST("http://" + CONFIG.CONFIG_DATA.get("VOICEVOX").get("HOST").asText() + ":" + CONFIG.CONFIG_DATA.get("VOICEVOX").get("PORT").asInt() + "/synthesis?speaker=" + SEEKER).VOICEVOX_DOWNLOAD(PATH + ID + ".wav", QUERY);

		if(RESULT){
			System.out.println("[ VOICEVOX ]生成されました:" + ID);
			return true;
		} else {
			System.err.println("[ VOICEVOX ]生成に失敗しました");
			return false;
		}
	}
}
