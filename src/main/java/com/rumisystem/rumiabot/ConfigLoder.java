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
            System.out.println("[ OK ]LoadConfig!");
            System.out.println("[ *** ]Setting Config...");

            Main.BOT_TOKEN = json.get("TOKEN").textValue();//トークン
            Main.BOT_ID = json.get("ID").textValue();
            Main.GOOGLE_API_KEY = json.get("GOOGLE_API_KEY").textValue();
            Main.GOOGLE_API_ENGINE_ID = json.get("GOOGLE_API_ENGINE_ID").textValue();

            Main.SQL_HOST = json.get("SQL_HOST").textValue();
            Main.SQL_USER = json.get("SQL_USER").textValue();
            Main.SQL_PASS = json.get("SQL_PASS").textValue();
            System.out.println("[ OK ]Setting Config!");
        } catch(IOException ex) {
            ex.printStackTrace();
        }
    }
}
