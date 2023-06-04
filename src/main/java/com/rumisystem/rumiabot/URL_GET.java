package com.rumisystem.rumiabot;

import net.dv8tion.jda.api.events.message.MessageReceivedEvent;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.concurrent.CompletableFuture;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static com.rumisystem.rumiabot.Main.LOG_OUT;

public class URL_GET {
    public static void Main(MessageReceivedEvent e){
        String TEXT = e.getMessage().getContentRaw();

        CompletableFuture.runAsync(() -> {
            // 正規表現パターンを定義
            String REGEX = "\\b(https?|ftp|file)://[-a-zA-Z0-9+&@#/%?=~_|!:,.;]*[-a-zA-Z0-9+&@#/%=~_|]";
            Pattern PATTERN = Pattern.compile(REGEX);

            // マッチャーを作成してテキストを検索
            Matcher MATCHER = PATTERN.matcher(TEXT);

            // URLを抽出して表示
            while (MATCHER.find()) {
                String URL = MATCHER.group();
                System.out.println("URL: " + URL);

                try {
                    // URLを作成
                    URL URL_OBJ = new URL(URL);

                    // HttpURLConnectionを作成
                    HttpURLConnection connection = (HttpURLConnection) URL_OBJ.openConnection();
                    connection.setRequestMethod("GET");

                    // レスポンスを取得
                    int responseCode = connection.getResponseCode();

                    if (responseCode == HttpURLConnection.HTTP_OK) {
                        // レスポンスの入力ストリームを取得
                        BufferedReader reader = new BufferedReader(new InputStreamReader(connection.getInputStream()));
                        String line;
                        StringBuilder response = new StringBuilder();

                        // HTMLコンテンツ全体を取得
                        while ((line = reader.readLine()) != null) {
                            response.append(line);
                        }

                        String RESPONSE_TEXT = DEL_HTML_TAGS(response.toString()).substring(0, Math.min(DEL_HTML_TAGS(response.toString()).length(), 500));
/*
                        // 最初の10文字を取得
                        while ((line = reader.readLine()) != null && response.length() < 1000) {
                            response.append(line);
                        }
*/
                        // 結果を表示
                        e.getMessage().reply("```" + RESPONSE_TEXT + "```").queue();
                        System.out.println(RESPONSE_TEXT);

                        // ストリームと接続を閉じる
                        reader.close();
                    } else {
                        System.out.println("HTTPリクエストが失敗しました。レスポンスコード: " + responseCode);
                    }

                    // 接続を閉じる
                    connection.disconnect();
                } catch (IOException ex) {
                    LOG_OUT("[ ERR ]HTTP REQUEST ERR" + URL);
                    e.getMessage().reply("```" +
                            "正常に接続できませんでした\n" +
                            "\n" +
                            URL + " への接続中にエラーが発生しました。\n" +
                            "\n" +
                            "    このサイトが一時的に利用できなくなっていたり、サーバーの負荷が高すぎて接続できなくなっている可能性があります。しばらくしてから再度試してください。\n" +
                            "    他のサイトも表示できない場合、コンピューターのネットワーク接続を確認してください。\n" +
                            "    ファイアウォールやプロキシーでネットワークが保護されている場合、Floorp によるウェブアクセスが許可されているか確認してください。\n" +
                            "\n" +
                            "```").queue();
                }
            }
        });
    }

    // HTMLタグを除去するメソッド
    public static String DEL_HTML_TAGS(String TEXT) {
        TEXT = TEXT.replaceAll("(?i)<br>", "\n");

        // 正規表現パターンを使用してHTMLタグを除去
        Pattern pattern = Pattern.compile("<[^>]*>|\\t|\\s");
        Matcher matcher = pattern.matcher(TEXT);
        return matcher.replaceAll("");
    }
}
