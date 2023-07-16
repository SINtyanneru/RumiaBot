package com.rumisystem.rumiabot.Command;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.file.Paths;
import java.util.Objects;
import java.util.concurrent.CompletableFuture;

import static com.rumisystem.rumiabot.Main.*;

public class RS_GET {
    public static void Main(SlashCommandInteractionEvent e){
        e.deferReply().queue();

        if(Objects.isNull(e.getInteraction().getOption("uid"))){
            e.getHook().editOriginal("ユーザーIDがありませんえらー！").queue();
            return;
        }

        CompletableFuture.runAsync(() -> {
            String URL = "http://192.168.0.3/API/SERVER/USER_GET.php?UID=" + e.getInteraction().getOption("uid").getAsString();
            LOG_OUT("[ HTTP ]" + URL);

            try{
                // URLを作成
                URL URL_OBJ = new URL(URL);

                // HttpURLConnectionを作成
                HttpURLConnection connection = (HttpURLConnection) URL_OBJ.openConnection();
                connection.setRequestMethod("GET");

                // レスポンスを取得
                int responseCode = connection.getResponseCode();

                if (responseCode == HttpURLConnection.HTTP_OK){
                    // レスポンスの入力ストリームを取得
                    BufferedReader reader = new BufferedReader(new InputStreamReader(connection.getInputStream()));
                    String line;
                    StringBuilder response = new StringBuilder();

                    // HTMLコンテンツ全体を取得
                    while((line = reader.readLine()) != null){
                        response.append(line);
                    }

                    String RESPONSE_TEXT = response.toString();

                    LOG_OUT(RESPONSE_TEXT);

                    ObjectMapper objectMapper = new ObjectMapper();
                    JsonNode json = objectMapper.readTree(RESPONSE_TEXT);
                    if(json.get("STATUS").booleanValue()){
                        String USER_ID = json.get("DATA").get("ID").textValue();
                        String USER_NAME = BASE64_DECODE(json.get("DATA").get("NAME").textValue());
                        String USER_DESC = BASE64_DECODE(json.get("DATA").get("DESCRIPTION").textValue());
                        String USER_SEX = "?";
                        String USER_REGIST_DATE = json.get("DATA").get("REGIST_DATE").textValue();
                        String USER_LOCATION = json.get("DATA").get("LOCATION").textValue();
                        String USER_STATUS = json.get("DATA").get("STATUS").textValue();

                        switch (json.get("DATA").get("SEX").textValue()){
                            case "MALE":
                                USER_SEX = "男";
                                break;
                            case "FMALE":
                                USER_SEX = "女";
                                break;
                            case "NEUT":
                                USER_SEX = "中";
                                break;
                            case "ASEXU":
                                USER_SEX = "無";
                                break;
                            case "ETC":
                                USER_SEX = "その他";
                                break;
                            case "NONE":
                                USER_SEX = "秘密";
                                break;
                        }

                        EmbedBuilder EB = new EmbedBuilder();
                        EB.setThumbnail("https://rumiserver.com/Data/API/user_icon.php?UID=" + e.getInteraction().getOption("uid").getAsString());
                        EB.setTitle(USER_NAME);
                        EB.setDescription(USER_DESC);
                        EB.addField("内部ユーザーID", USER_ID, false);
                        EB.addField("性別", USER_SEX, false);
                        EB.addField("登録日", USER_REGIST_DATE, false);
                        EB.addField("國", USER_LOCATION, false);
                        EB.addField("ステータス", USER_STATUS, false);

                        e.getHook().editOriginalEmbeds(EB.build()).queue();
                    }else {
                        e.getHook().editOriginal("エラー：").queue();
                    }

                    // ストリームと接続を閉じる
                    reader.close();
                }else{
                    e.getHook().editOriginal("エラー" + responseCode).queue();
                }

                // 接続を閉じる
                connection.disconnect();
            }catch(IOException ex){
                LOG_OUT("[ ERR ]HTTP REQUEST ERR" + URL);
                e.getHook().editOriginal("エラー").queue();
            }
        });
    }
}
