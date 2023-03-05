package com.rumisystem.rumiabot.Command;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.rumisystem.rumiabot.Main;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.URL;
import java.net.URLConnection;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.nio.file.Paths;
import java.util.Base64;

import static com.rumisystem.rumiabot.Main.AppDir;
import static com.rumisystem.rumiabot.Main.LOG_OUT;

public class setch {
    public static void main(MessageReceivedEvent e){
        try {
            String[] COMMAND_MODE;
            try {
                COMMAND_MODE = e.getMessage().getContentRaw().split(" ");
                if (COMMAND_MODE[1] == null) {
                    e.getChannel().sendMessage("エラー 設定するチャンネルのタイプを指定してください").queue();
                    return;
                }
            } catch (Exception ex) {
                e.getChannel().sendMessage("エラー 設定するチャンネルのタイプを指定してください").queue();
                return;
            }

            URL url = new URL("http://192.168.0.3/sinch/RUMIA_BOT/SETCH.php?CID=" + e.getChannel().getId() + "&TYPE=" + COMMAND_MODE[1]);

            URLConnection conn = url.openConnection();
            conn.setDoOutput(true);
            conn.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");

            try (BufferedReader bf = new BufferedReader(new InputStreamReader(conn.getInputStream()))) {
                String line;
                while ((line = bf.readLine()) != null) {
                    LOG_OUT(line);
                    ObjectMapper objectMapper = new ObjectMapper();
                    JsonNode json = objectMapper.readTree(line);
                    if(json.get("STATUS").textValue().equals("OK")){
                        LOG_OUT("[ OK ]SET CH OK:" + e.getChannel().getId() + "/" + COMMAND_MODE[1]);
                        String CHTYPE_NAME = null;
                        switch (COMMAND_MODE[1]){
                            case "TALK":
                                CHTYPE_NAME = "会話";
                                break;
                        }
                        e.getChannel().sendMessage("チャンネルID「" + e.getChannel().getId() + "」を、" + CHTYPE_NAME + "チャンネルとして設定したのだ～").queue();
                    }else {
                        LOG_OUT("[ ERR ]SET CH ERR:" + json.get("ERR_CODE").textValue());
                        e.getChannel().sendMessage("[ エラー ]" + json.get("ERR_CODE").textValue()).queue();
                    }
                }
            }
        }catch (Exception ex){
            // Finally we have the response
            ex.printStackTrace();
        }
    }
}
