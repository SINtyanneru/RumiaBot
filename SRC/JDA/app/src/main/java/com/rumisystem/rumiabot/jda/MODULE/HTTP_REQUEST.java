package com.rumisystem.rumiabot.jda.MODULE;

import java.io.InputStream;
import java.net.URL;
import java.io.*;
import java.net.CookieManager;
import java.net.HttpURLConnection;
import java.net.URI;
import java.net.URL;
import java.net.http.HttpRequest;
import java.nio.Buffer;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class HTTP_REQUEST {
	private URL REQIEST_URI = null;

	public HTTP_REQUEST(String INPUT_REQ_URL){
		try{
			REQIEST_URI = new URL(INPUT_REQ_URL);
		}catch (Exception EX) {
			System.err.println(EX);
			System.exit(1);
		}
	}

	//GET
	public String GET(){
		try{
			HttpURLConnection HUC = (HttpURLConnection) REQIEST_URI.openConnection();

			//GETリクエストだと主張する
			HUC.setRequestMethod("GET");

			CookieManager COOKIE_MANAGER = new CookieManager();

			File COOKIE_FILE = new File("./cookie.txt");
			if(COOKIE_FILE.exists()){
				StringBuilder COOKIE = new StringBuilder();

				FileReader FR = new FileReader(COOKIE_FILE);
				BufferedReader BR = new BufferedReader(FR);

				String TEMP;
				while ((TEMP = BR.readLine()) != null) {
					COOKIE.append(TEMP);
				}
				BR.close();

				HUC.setRequestProperty("Cookie", COOKIE.toString());
			}

			//レスポンスコード
			int RES_CODE = HUC.getResponseCode();

			if(RES_CODE == 200){
				BufferedReader BR = new BufferedReader(new InputStreamReader(HUC.getInputStream(), StandardCharsets.UTF_8));
				StringBuilder RES_STRING = new StringBuilder();

				String INPUT_LINE;
				while ((INPUT_LINE = BR.readLine()) != null){
					RES_STRING.append(INPUT_LINE);
				}

				BR.close();
				return RES_STRING.toString();
			}
			return null;
		}catch (Exception EX){
			EX.printStackTrace();
			return null;
		}
	}

	public String POST(String POST_BODY){
		try{
			System.out.println("[  ***  ]POST:" + REQIEST_URI.toString());
			HttpURLConnection HUC = (HttpURLConnection) REQIEST_URI.openConnection();

			//POSTだと主張する
			HUC.setRequestMethod("POST");

			//POST可能に
			HUC.setDoInput(true);
			HUC.setDoOutput(true);

			HUC.setRequestProperty("Content-Type", "application/json; charset=utf-8");

			HUC.connect();

			//リクエストボディに送信したいデータを書き込む
			PrintStream PS = new PrintStream(HUC.getOutputStream());
			PS.print(POST_BODY);
			PS.close();

			//レスポンスコード
			int RES_CODE = HUC.getResponseCode();
			BufferedReader BR = new BufferedReader(new InputStreamReader(HUC.getInputStream(), StandardCharsets.UTF_8));
			StringBuilder RES_STRING = new StringBuilder();

			String INPUT_LINE;
			while ((INPUT_LINE = BR.readLine()) != null){
				RES_STRING.append(INPUT_LINE);
			}

			BR.close();
			System.out.println("[  OK   ]POST");
			return RES_STRING.toString();
		}catch (Exception EX){
			EX.printStackTrace();
			return null;
		}
	}

	//Pixivの画像を落とすための関数
	public void PIXIV_DOWNLOAD(String PATH){
		try{
			//名前が長すぎるので切り落としたよ
			HttpURLConnection HUC = (HttpURLConnection) REQIEST_URI.openConnection();

			//GETリクエストだと主張する
			HUC.setRequestMethod("GET");

			//ヘッダーを入れる
			HUC.setRequestProperty("Host", "i.pximg.net");
			HUC.setRequestProperty("User-Agent", "Mozilla/5.0 (X11; Linux x86_64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/120.0.0.0 Safari/537.36");
			HUC.setRequestProperty("Referer", "https://www.pixiv.net/");

			//レスポンスコード
			int RES_CODE = HUC.getResponseCode();
			if(RES_CODE == HttpURLConnection.HTTP_OK){
				//ファイルを保存する機構
				InputStream IS = HUC.getInputStream();
				FileOutputStream OS = new FileOutputStream(PATH);
				byte[] BUFFER = new byte[4096];
				int BYTES_READ;
				while((BYTES_READ = IS.read(BUFFER)) != -1){
					OS.write(BUFFER, 0, BYTES_READ);
				}
			}
		}catch (Exception EX){
			EX.printStackTrace();
		}
	}


	//ダウンロード
	public void DOWNLOAD(String PATH){
		try{
			//名前が長すぎるので切り落としたよ
			HttpURLConnection HUC = (HttpURLConnection) REQIEST_URI.openConnection();

			//GETリクエストだと主張する
			HUC.setRequestMethod("GET");

			//ヘッダーを入れる
			HUC.setRequestProperty("User-Agent", "Mozilla/5.0 (X11; Linux x86_64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/120.0.0.0 Safari/537.36");

			//レスポンスコード
			int RES_CODE = HUC.getResponseCode();
			if(RES_CODE == HttpURLConnection.HTTP_OK){
				//ファイルを保存する機構
				InputStream IS = HUC.getInputStream();
				FileOutputStream OS = new FileOutputStream(PATH);
				byte[] BUFFER = new byte[4096];
				int BYTES_READ;
				while((BYTES_READ = IS.read(BUFFER)) != -1){
					OS.write(BUFFER, 0, BYTES_READ);
				}

			}
		}catch (Exception EX){
			EX.printStackTrace();
		}
	}
}
