package com.rumisystem.rumiabot;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.IOException;
import java.nio.file.Paths;

import static com.rumisystem.rumiabot.Main.AppDir;

public class ConfigLoder {
    public static void main(){
        BotConfigLoad();
    }

    public static void BotConfigLoad(){
        try {
            ObjectMapper objectMapper = new ObjectMapper();
            JsonNode json = objectMapper.readTree(Paths.get(AppDir + "/Config.json").toFile());
            System.out.println("[ OK ]LoadConfig:" + json);
            System.out.println("[ *** ]Setting Config...");

            Main.BOT_TOKEN = json.get("TOKEN").textValue();//トークン
        } catch(IOException ex) {
            ex.printStackTrace();
        }
    }
}
