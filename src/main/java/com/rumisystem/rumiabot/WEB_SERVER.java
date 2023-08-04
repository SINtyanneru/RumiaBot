/**
 * CUIが使いにくいので、HTTPからできるように
 */

package com.rumisystem.rumiabot;

import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import com.sun.net.httpserver.HttpServer;

import java.io.*;
import java.net.InetSocketAddress;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;

import static com.rumisystem.rumiabot.Main.LOG_OUT;

public class WEB_SERVER extends Thread{
	public static String FILE_DIR = "/HTML/";

	//サーバー実行
	public void run(){
		try {
			//HTTPサーバーの作成
			HttpServer SERVER = HttpServer.create(new InetSocketAddress(8080), 0);

			//ハンドラの登録
			SERVER.createContext("/", new Handler());

			//サーバーの開始
			SERVER.start();

			//開始したことを伝える
			LOG_OUT("[ HTTP ]====================");
			LOG_OUT("[ HTTP ]Start localhost:8080");
			LOG_OUT("[ HTTP ]====================");
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	//HTTPリクエストのイベント
	static class Handler implements HttpHandler {
		@Override
		public void handle(HttpExchange httpExchange) throws IOException {
			try{
				//ファイル名を取得する
				String FILE_NAME = httpExchange.getRequestURI().toString();
				if(FILE_NAME.equals("/")){//もし/なら
					//indexを読み込むようにする
					FILE_NAME = "index.html";
				}

				//MIMEタイプの取得
				String MIME = "text/html; charset=UTF-8";

				//読み込むファイル
				InputStream IS = Main.class.getResourceAsStream(FILE_DIR + FILE_NAME);

				//ファイルが有るか
				if(IS != null){
					//ファイルを読み込む
					BufferedReader BR = new BufferedReader(new InputStreamReader(IS));

					//ファイルを合体
					StringBuilder FILE_CONTENTS = new StringBuilder();
					String LINE;
					while ((LINE = BR.readLine()) != null) {
						FILE_CONTENTS.append(LINE);
					}

					//返答
					String BODY = FILE_CONTENTS.toString();
					HTTP_RES(httpExchange, BODY, MIME, 200);
				}else{
					//ファイルがないので死ぬ
					String BODY = FILE_ERR("404");
					HTTP_RES(httpExchange, BODY, MIME, 404);
					return;
				}
			}catch(IOException E){
				LOG_OUT("[ HTTP ]" + E.getMessage());
			}
		}
	}

	//HTTPリクエストを返す
	public static void HTTP_RES(HttpExchange httpExchange, String BODY, String MIME, int STATUS_CODE){
		try{
			//MIMEタイプを返す
			httpExchange.getResponseHeaders().set("Content-Type", MIME);

			//ステータスコード、文字コード、長さを返す
			httpExchange.sendResponseHeaders(STATUS_CODE, BODY.getBytes(StandardCharsets.UTF_8).length);

			//レスポンスの書き込み
			OutputStream outputStream = httpExchange.getResponseBody();
			outputStream.write(BODY.getBytes(StandardCharsets.UTF_8));
			outputStream.close();
		}catch(IOException E){
			LOG_OUT("[ HTTP ]" + E.getMessage());
		}
	}

	//エラー時に表示するファイルを返す
	public static String FILE_ERR(String STATUS){
		try{
			//読み込むファイル
			InputStream IS = Main.class.getResourceAsStream(FILE_DIR + STATUS + ".html");
			//ファイルが有るか
			if(IS != null){
				//ファイルを読み込む
				BufferedReader BR = new BufferedReader(new InputStreamReader(IS));
				//ファイルを合体
				StringBuilder FILE_CONTENTS = new StringBuilder();
				String LINE;
				while ((LINE = BR.readLine()) != null) {
					FILE_CONTENTS.append(LINE);
				}

				return FILE_CONTENTS.toString();
			}else{
				return "ERR：" + STATUS;
			}
		}catch(IOException E){
			return "ERR";
		}
	}
}