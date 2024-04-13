package com.rumisystem.rumiabot.jda.MODULE;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import net.dv8tion.jda.api.interactions.commands.OptionType;
import net.dv8tion.jda.api.interactions.commands.build.Commands;
import net.dv8tion.jda.api.interactions.commands.build.OptionData;
import net.dv8tion.jda.api.interactions.commands.build.SlashCommandData;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;

import static com.rumisystem.rumiabot.jda.PT.SEND;

public class FUNCTION {
	//機能一覧
	public static ArrayList<HashMap<String, Object>> FUNCTION_LIST = new ArrayList<>(){
		{
			add(new HashMap<String, Object>(){
				{
					put("ID", "vxtwitter");
					put("NAME", "vxTwitter置き換え");
					put("CHANNEL_ONLY", false);
					put("GUILD_ONLY", false);
				}
			});

			add(new HashMap<String, Object>(){
				{
					put("ID", "guten_morgen");
					put("NAME", "朝のあいさつ");
					put("CHANNEL_ONLY", true);
					put("GUILD_ONLY", false);
				}
			});
		}
	};

	//機能の設定のキャッシュ
	private static ArrayList<JsonNode> FUNCTION_SETTING_CACHE = new ArrayList<>();

	//スラッシュコマンドを生成する
	public static SlashCommandData CREATE_SLASH_COMMAND(){
		//有効無効化のオプション
		OptionData SETTING_OPTION_ENABLE = new OptionData(OptionType.STRING, "enable", "有効化するか無効化するか", true);
		SETTING_OPTION_ENABLE.addChoice("true", "true");
		SETTING_OPTION_ENABLE.addChoice("false", "false");

		//機能一覧
		OptionData SETTING_OPTION_FUNCTION = new OptionData(OptionType.STRING, "function", "機能", true);
		//機能を全て追加する
		for(int I = 0; I < FUNCTION_LIST.size(); I++){
			String FUNCTION_ID = FUNCTION_LIST.get(I).get("ID").toString();
			String FUNCTION_NAME = FUNCTION_LIST.get(I).get("NAME").toString();

			//ついか
			SETTING_OPTION_FUNCTION.addChoice(FUNCTION_NAME, FUNCTION_ID);
		}

		//コマンド本体
		SlashCommandData SETTING_COMMAND = Commands.slash("setting", "機能の設定ができます")
				.addOptions(SETTING_OPTION_ENABLE, SETTING_OPTION_FUNCTION);

		return SETTING_COMMAND;
	}

	//その鯖/チャンネルで機能が有効かどうかをチェックする
	public static FUNCTION_CHECK_RESULT FUNCTION_CHECK(String GID, String CID, String FUNCTION_ID) throws IOException {
		ObjectMapper OM = new ObjectMapper();

		//キャッシュから取得
		for(JsonNode CACHE:FUNCTION_SETTING_CACHE){
			if(CACHE.get("FUNC_ID").asText().equals(FUNCTION_ID)){
				//キャッシュに設定があった

				switch (CACHE.get("MODE").asInt()){
					//鯖全体
					case 1:{
						return FUNCTION_CHECK_RESULT.ANY;
					}

					//チャンネルのみ
					case 2:{
						//チャンネルIDが一致するか？(しないならスキップ)
						if(CACHE.get("CID").asText().equals(CID)){
							return FUNCTION_CHECK_RESULT.CHANNEL_ONLY;
						}
					}
				}
			}
		}

		//キャッシュがないのでSQLから取得
		String SQL_RESULT = SEND("SQL;SELECT * FROM `CONFIG` WHERE `GID` = ?;[\"" + GID + "\"]");

		if(SQL_RESULT.split(";")[1].equals("200")){
			JsonNode RESULT = OM.readTree(SQL_RESULT.split(";")[0]);

			//リザルトから、指定された機能IDの設定を探す
			for(int I = 0; I < RESULT.size(); I++){
				JsonNode SETTING = RESULT.get(I);
				if(SETTING.get("FUNC_ID").asText().equals(FUNCTION_ID)){
					//設定があった

					//キャッシュに追加
					FUNCTION_SETTING_CACHE.add(SETTING);

					switch (SETTING.get("MODE").asInt()){
						//鯖全体
						case 1:{
							return FUNCTION_CHECK_RESULT.ANY;
						}

						//チャンネルのみ
						case 2:{
							//チャンネルIDが一致するか？(しないならスキップ)
							if(SETTING.get("CID").asText().equals(CID)){
								return FUNCTION_CHECK_RESULT.CHANNEL_ONLY;
							}
						}
					}
				}
			}

			//無かった場合
			return FUNCTION_CHECK_RESULT.NONE;
		}

		//無かった場合
		return FUNCTION_CHECK_RESULT.NONE;
	}
}
