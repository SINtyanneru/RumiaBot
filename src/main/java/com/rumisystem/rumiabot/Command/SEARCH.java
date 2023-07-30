package com.rumisystem.rumiabot.Command;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.rumisystem.rumiabot.Main;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.MessageEmbed;
import net.dv8tion.jda.api.entities.emoji.Emoji;
import net.dv8tion.jda.api.entities.emoji.EmojiUnion;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.lang.reflect.Array;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static com.rumisystem.rumiabot.Main.*;

public class SEARCH {
    public static void Main(MessageReceivedEvent e) throws JsonProcessingException {
        try{
            String Q = e.getMessage().getContentRaw().replace("検索 ", "");

            e.getMessage().addReaction(Emoji.fromUnicode("\uD83D\uDD04")).queue();

            String RESULT = HTTP_REQ("https://www.googleapis.com/customsearch/v1" +
                    "?key=" + Main.GOOGLE_API_KEY +
                    "&cx=" + Main.GOOGLE_API_ENGINE_ID +
                    "&q=" + Q);
            if(RESULT.startsWith("ERR")){//エラーチェック
                e.getMessage().reply("検索中にエラーが発生しました:" + RESULT.replace("ERR", "")).queue();
                return;
            }

            ObjectMapper objectMapper = new ObjectMapper();
            JsonNode RESULT_JSON = objectMapper.readTree(RESULT).get("items");

            ArrayList<String> RESULT_TITLE = new ArrayList<>();
            ArrayList<String> RESULT_LINK = new ArrayList<>();
            ArrayList<String> RESULT_SNIPPET = new ArrayList<>();

            for(JsonNode ITEM: RESULT_JSON){
                RESULT_TITLE.add(ITEM.get("title").textValue());
                RESULT_LINK.add(ITEM.get("link").textValue());
                RESULT_SNIPPET.add(ITEM.get("snippet").textValue());
            }

            EmbedBuilder EB = new EmbedBuilder();
            EB.setTitle("「" + Q + "」の検索結果");
            EB.setDescription("GoogleのAPIを使用");

            for(int I = 0; I < RESULT_TITLE.size(); I++){
                String TITLE = RESULT_TITLE.get(I);
                String LINK = RESULT_LINK.get(I);
                String SNIPPET = RESULT_SNIPPET.get(I);

                if(RESULT_TITLE.get(I).length() > 253){
                    TITLE = RESULT_TITLE.get(I).substring(0, 253) + "...";
                }

                EB.addField(TITLE, "[" + SNIPPET + "](" + LINK + ")", false);
            }

            e.getMessage().replyEmbeds(EB.build()).queue();
        }catch (Exception EX){
            e.getMessage().reply("検索中にエラーが発生しました:" + EX.getMessage()).queue();
        }
    }

    public static String HTTP_REQ(String URL_TEXT){
        try{
            // URLを作成
            URL URL_OBJ = new URL(URL_TEXT.replace("\n", "+"));

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

                String RESPONSE_TEXT = response.toString();

                // ストリームと接続を閉じる
                reader.close();

                // 結果を表示
                return RESPONSE_TEXT;
            } else {
                return "ERR" +  responseCode;
            }
        }catch (Exception EX){
            return "ERR" + EX.getMessage();
        }
    }
}
