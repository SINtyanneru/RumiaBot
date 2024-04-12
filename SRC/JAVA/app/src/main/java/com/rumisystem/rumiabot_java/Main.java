package com.rumisystem.rumiabot_java;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.checkerframework.checker.units.qual.N;

import java.util.ArrayList;
import java.util.List;

public class Main {
	public static void main(String[] args) {
		try{
			System.out.println("RumiBot JAVA System V1.0");

			//設定ファイル
			System.out.println("設定ファイルを読み込みます...");
			CONFIG.LOAD();

			//SQL起動
			new SQL();

			//PS
			new Thread(new Runnable() {
				@Override
				public void run() {
					PT.main(3001);
				}
			}).start();

			for (int I = 0; CONFIG.CONFIG_DATA.get("SNS").size() > I; I++) {
				JsonNode ROW = CONFIG.CONFIG_DATA.get("SNS").get(I);
				if(ROW.get("MAIN").asBoolean()){
					System.out.println(ROW.get("ID").asText() + "に接続します");
					new Thread(new Runnable() {
						@Override
						public void run() {
							com.rumisystem.rumiabot_java.MISSKEY_BOT.Main.main(ROW.get("DOMAIN").asText(), ROW.get("API").asText());
						}
					}).start();
					break;
				}
			}
		} catch(Exception EX) {
			EX.printStackTrace();
		}
	}

	public static void LOG(int LEVEL, String CLASS, String TEXT){
		switch (LEVEL){
			case 0:{
				System.out.println("[  \u001B[32mOK\u001B[0m  ][" + CLASS + "] " + TEXT);
				break;
			}

			case 1:{
				System.out.println("[\u001B[31mFAILED\u001B[0m][" + CLASS + "] " + TEXT);
				break;
			}

			case 2:{
				System.out.println("[ INFO ][" + CLASS + "] " + TEXT);
				break;
			}

			case 3:{
				System.out.println("[ **** ][" + CLASS + "] " + TEXT);
				break;
			}

			case 4:{
				System.out.println("\u001B[1F[  \u001B[32mOK\u001B[0m  ]");
				break;
			}

			case 5:{
				System.out.println("\u001B[1F[\u001B[31mFAILED\u001B[0m]");
				break;
			}
		}
	}

	//配列のJSONをArrayListに変換するやつ
	public static List<Object> ARRAY_JSON_TO_ARRAYLIST(String ARRAY_JSON) throws JsonProcessingException {
		ObjectMapper OM = new ObjectMapper();
		JsonNode JN = OM.readTree(ARRAY_JSON);

		List<Object> ARRAY = new ArrayList<>();

		for(JsonNode NODE:JN){
			//こうやって、型ごとに変換をしないと、
			//「com.fasterxml.jackson.databind.node.TextNode」みたいになって面倒くさいので変換する
			switch (NODE.getNodeType()){
				case STRING:{
					ARRAY.add(NODE.asText());
				}

				case NUMBER:{
					ARRAY.add(NODE.asInt());
				}

				case BOOLEAN:{
					ARRAY.add(NODE.asBoolean());
				}

				default:{
					ARRAY.add(NODE);
				}
			}
		}

		return ARRAY;
	}
}
