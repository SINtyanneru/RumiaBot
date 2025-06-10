package su.rumishistem.rumiabot.System;

import static su.rumishistem.rumiabot.System.Main.CONFIG_DATA;

import java.util.LinkedHashMap;

import com.fasterxml.jackson.databind.ObjectMapper;

import su.rumishistem.rumi_java_lib.FETCH;
import su.rumishistem.rumi_java_lib.LOG_PRINT.LOG_TYPE;

import static su.rumishistem.rumi_java_lib.LOG_PRINT.Main.LOG;

public class LogSystem {
	public static void info(String Text) {
		LOG(LOG_TYPE.INFO, Text);
	}

	public static void error(String Text) {
		LOG(LOG_TYPE.FAILED, Text);

		try {
			FETCH Ajax = new FETCH(CONFIG_DATA.get("DISCORD").getData("LOG_WH").asString());

			LinkedHashMap<String, Object> Body = new LinkedHashMap<String, Object>();
			Body.put("content", "エラー```\n"+Text+"\n```");
			Ajax.POST(new ObjectMapper().writeValueAsString(Body).getBytes());
		} catch (Exception EX) {
			EX.printStackTrace();
		}
	}
}
