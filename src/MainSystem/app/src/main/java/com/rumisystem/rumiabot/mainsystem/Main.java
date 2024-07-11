package com.rumisystem.rumiabot.mainsystem;

import static com.rumisystem.rumi_java_lib.LOG_PRINT.Main.LOG;

import com.rumisystem.rumi_java_lib.ArrayNode;
import com.rumisystem.rumi_java_lib.CONFIG;
import com.rumisystem.rumi_java_lib.LOG_PRINT.LOG_TYPE;
import com.rumisystem.rumiabot.mainsystem.DiscordAPI.DiscordAPI;

import java.io.File;

public class Main {
	public static ArrayNode CONFIG_DATA = new ArrayNode();

	public static void main(String[] args) {
		try{
			System.out.println("    ____                  _       ____  ____  ______");
			System.out.println("   / __ \\__  ______ ___  (_)___ _/ __ )/ __ \\/_  __/");
			System.out.println("  / /_/ / / / / __ `__ \\/ / __ `/ __  / / / / / /   ");
			System.out.println(" / _, _/ /_/ / / / / / / / /_/ / /_/ / /_/ / / /    ");
			System.out.println("/_/ |_|\\__,_/_/ /_/ /_/_/\\__,_/_____/\\____/ /_/     ");
			System.out.println("V2.0");
	
			LOG(LOG_TYPE.PROCESS, "Config yomikomi nau");
			if(new File("./Config.ini").exists()){
				CONFIG_DATA = new CONFIG().DATA;

				LOG(LOG_TYPE.PROCESS_END_OK, "");
			} else {
				LOG(LOG_TYPE.PROCESS_END_FAILED, "Config file ga nai!");
			}

			DiscordAPI.Main();

			LOG(LOG_TYPE.OK, "DiscordBOT start!");
		}catch(Exception EX){
			EX.printStackTrace();
			System.exit(1);
		}
	}
}