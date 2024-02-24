package com.rumisystem.rumiabot.jda;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.checkerframework.checker.units.qual.C;

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

public class CONFIG {
	public static JsonNode CONFIG_DATA = null;

	public static void LOAD(){
		try{
			File CONFIG_FILE = new File("./Config.json");
			if(CONFIG_FILE.exists()) {
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
				CONFIG_DATA = OM.readTree(CONFIG_CONTENTS.toString());
			} else {
				System.err.println("エラー、設定ファイルがありません");
				System.exit(1);
			}
		}catch (Exception EX){
			EX.printStackTrace();
		}
	}
}
