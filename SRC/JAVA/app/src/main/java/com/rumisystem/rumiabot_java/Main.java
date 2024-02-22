package com.rumisystem.rumiabot_java;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.File;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;

import com.rumisystem.rumiabot_java.PS;

public class Main {
	public static void main(String[] args) {
		try{
			System.out.println("RumiBot JAVA System V1.0");

			File CONFIG_FILE = new File("./Config.json");
			if(CONFIG_FILE.exists()){
				System.out.println("設定ファイルを読み込みます...");

				FileReader FR = new FileReader(CONFIG_FILE);
				BufferedReader BR = new BufferedReader(FR);

				StringBuilder CONFIG_CONTENTS = new StringBuilder();

				String CONFIG_CONTENTS_TEMP;
				while ((CONFIG_CONTENTS_TEMP = BR.readLine()) != null) {
					CONFIG_CONTENTS.append(CONFIG_CONTENTS_TEMP);
				}
				BR.close();

				ObjectMapper OM = new ObjectMapper();
				JsonNode CONFIG = OM.readTree(CONFIG_CONTENTS.toString());

				//PS
				new Thread(new Runnable() {
					@Override
					public void run() {
						PS.main(3001);
					}
				}).start();

				for (int I = 0; CONFIG.get("SNS").size() > I; I++) {
					JsonNode ROW = CONFIG.get("SNS").get(I);
					if(ROW.get("MAIN").asBoolean()){
						System.out.println(ROW.get("ID").asText() + "に接続します");
						new Thread(new Runnable() {
							@Override
							public void run() {
								//com.rumisystem.rumiabot_java.MISSKEY_BOT.Main.main(ROW.get("DOMAIN").asText(), ROW.get("API").asText());
							}
						}).start();
						break;
					}
				}
			} else {
				System.err.println("設定ファイルがありません");
				System.exit(1);
			}
		} catch(Exception EX) {
			EX.printStackTrace();
		}
	}
}
