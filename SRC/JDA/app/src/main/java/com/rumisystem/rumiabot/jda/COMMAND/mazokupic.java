package com.rumisystem.rumiabot.jda.COMMAND;

import com.beust.ah.A;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.rumisystem.rumiabot.jda.MODULE.FILE_LOAD;
import com.rumisystem.rumiabot.jda.MODULE.FILE_WRITER;
import com.rumisystem.rumiabot.jda.MODULE.HTTP_REQUEST;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.utils.FileUpload;

import java.io.File;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Random;

public class mazokupic {
	private static String DOWNLOAD_DIR = "./DOWNLOAD/MAZOKUPIC/";

	public static void main(SlashCommandInteractionEvent INTERACTION) {
		JsonNode RESULT = GET_PICTURES_CATCHE();
		if(RESULT != null){
			//乱数を生成
			int RND = new Random().nextInt(RESULT.get("illust").get("data").size());

			JsonNode ILLUST_INFO = RESULT.get("illust").get("data").get(RND);

			//その乱数のイラストは有るか
			if(ILLUST_INFO != null){
				String ILLUST_ID =  ILLUST_INFO.get("id").asText();
				JsonNode USER_INFO = GET_USER(ILLUST_INFO.get("userId").asText());

				//Nullチェック
				if(USER_INFO != null){
					//イラストをダウンロード
					GET_ILLUST(ILLUST_ID);

					File ILLUST_FILE = new File(DOWNLOAD_DIR + "ILLUST/ILLUST_" + ILLUST_ID + ".jpg");
					File USER_FILE = new File(DOWNLOAD_DIR + "USER/USER_" + ILLUST_INFO.get("userId").asText() + ".png");

					if(ILLUST_FILE.exists()){
						EmbedBuilder EB = new EmbedBuilder();
						EB.setTitle(ILLUST_INFO.get("title").asText());
						EB.setUrl("https://pixiv.net/artworks/" + ILLUST_INFO.get("id").asText());

						//ILLUST
						EB.setImage("attachment://ILLUST_" + ILLUST_ID + ".jpg");

						//投稿者情報
						EB.setAuthor(USER_INFO.get("name").asText(), "https://pixiv.net/users/" + ILLUST_INFO.get("userId").asText(), "attachment://USER_" + ILLUST_INFO.get("userId").asText() + ".png");


						INTERACTION.getHook().editOriginalEmbeds(EB.build()).setAttachments(FileUpload.fromData(ILLUST_FILE), FileUpload.fromData(USER_FILE)).queue();
					} else {
						INTERACTION.getHook().editOriginal("ダウンロード失敗").queue();
					}
				} else {
					INTERACTION.getHook().editOriginal("ユーザー情報を取得できませんでした").queue();
				}
			} else {
				INTERACTION.getHook().editOriginal("乱数生成エラー").queue();
			}
		} else {
			INTERACTION.getHook().editOriginal("イラスト一覧を取得できませんでした").queue();
		}
	}

	private static JsonNode GET_PICTURES_CATCHE(){
		try{
			String CATCHE_PATH = DOWNLOAD_DIR + "MACHIKADO_MAZOKU_PIXIV_CACHE.json";
			File CATCHE = new File(CATCHE_PATH);

			ObjectMapper OM = new ObjectMapper();
			LocalDateTime DATE = LocalDateTime.now();//今の日付


			//キャッシュがある
			if(CATCHE.exists()) {
				System.out.println("[ OK ][ MAZOKU ]キャッシュをロードしました");

				//キャッシュを読み込む
				JsonNode CATCHE_DATA = OM.readTree(new FILE_LOAD(CATCHE_PATH).LOAD());

				//キャッシュの日付は、今日か？
				if(CATCHE_DATA.get("DATE").asText().equals(DATE.format(DateTimeFormatter.ofPattern("yyyy-MM-dd")))){
					//今日なので、キャッシュのDATAだけ渡す
					return CATCHE_DATA.get("DATA");
				} else {
					System.out.println("[ OK ][ MAZOKU ]キャッシュを再取得しました");

					//今日ではないので再取得する
					JsonNode RESULT = GET_PICTURES().get("body");

					HashMap<String, Object> MAP = new HashMap<>();
					MAP.put("DATE", DATE.format(DateTimeFormatter.ofPattern("yyyy-MM-dd")));
					MAP.put("DATA", RESULT);

					String RES = OM.writeValueAsString(MAP);

					//キャッシュを書き込む
					new FILE_WRITER(CATCHE_PATH, RES);

					return RESULT.get("body");
				}
			} else {//無い
				System.out.println("[ OK ][ MAZOKU ]キャッシュを作成しました");

				JsonNode RESULT = GET_PICTURES().get("body");

				HashMap<String, Object> MAP = new HashMap<>();
				MAP.put("DATE", DATE.format(DateTimeFormatter.ofPattern("yyyy-MM-dd")));
				MAP.put("DATA", RESULT);

				String RES = OM.writeValueAsString(MAP);

				//キャッシュを書き込む
				new FILE_WRITER(CATCHE_PATH, RES);

				return RESULT;
			}
		}catch (Exception EX){
			System.err.println("Mazokupic ERR");
			EX.printStackTrace();
			return null;
		}
	}

	//イラスト一覧を取得する
	private static JsonNode GET_PICTURES(){
		try{
			//HTTPリクエスt
			String AJAX = new HTTP_REQUEST("https://www.pixiv.net/ajax/search/illustrations/まちカドまぞく")	.GET();

			//JSONをパース
			ObjectMapper OM = new ObjectMapper();
			JsonNode RESULT = OM.readTree(AJAX);

			if(!RESULT.get("error").asBoolean()){
				System.out.println("[ OK ][ MAZOKU ]PixivAPIが応答しました");

				return RESULT;
			} else {
				System.out.println("[ ERR ][ MAZOKU ]PixivAPIがエラーを吐きました；；");
				throw new RuntimeException("Pixiv API Err");
			}
		}catch (Exception EX){
			System.err.println("Mazokupic ERR");
			EX.printStackTrace();
			return null;
		}
	}

	//イラストを取得する
	public static void GET_ILLUST(String ID){
		try{
			String ILLUST_ALL_PAGE = new HTTP_REQUEST("https://www.pixiv.net/ajax/illust/" + ID + "/pages?lang=ja").GET();

			ObjectMapper OM = new ObjectMapper();
			JsonNode ILLUST_ALL_PAGE_JSON = OM.readTree(ILLUST_ALL_PAGE);

			if(!new File(DOWNLOAD_DIR + "ILLUST/ILLUST_" + ID + ".jpg").exists()){
				String FILE_URL = ILLUST_ALL_PAGE_JSON.get("body").get(0).get("urls").get("regular").asText();

				new HTTP_REQUEST(FILE_URL)
						.PIXIV_DOWNLOAD(DOWNLOAD_DIR + "ILLUST/ILLUST_" + ID + ".jpg");
			}
		}catch (Exception EX){
			System.err.println("Mazokupic ERR");
			EX.printStackTrace();
		}
	}

	//ユーザーのすべてを取得
	public static JsonNode GET_USER(String UID){
		try{
			String AJAX = new HTTP_REQUEST("https://www.pixiv.net/ajax/user/" + UID).GET();

			ObjectMapper OM = new ObjectMapper();
			JsonNode RESULT = OM.readTree(AJAX);

			if(!RESULT.get("error").asBoolean()){
				if(!new File(DOWNLOAD_DIR + "USER/USER_" + UID + ".png").exists()){
					String FILE_URL = RESULT.get("body").get("image").asText();

					new HTTP_REQUEST(FILE_URL)
							.PIXIV_DOWNLOAD(DOWNLOAD_DIR + "USER/USER_" + UID + ".png");
				}

				return RESULT.get("body");
			} else {
				return null;
			}
		}catch (Exception EX){
			System.err.println("Mazokupic ERR");
			EX.printStackTrace();
			return null;
		}
	}
}
