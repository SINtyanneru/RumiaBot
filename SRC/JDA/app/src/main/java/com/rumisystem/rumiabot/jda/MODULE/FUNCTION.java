package com.rumisystem.rumiabot.jda.MODULE;

import net.dv8tion.jda.api.interactions.commands.OptionType;
import net.dv8tion.jda.api.interactions.commands.build.Commands;
import net.dv8tion.jda.api.interactions.commands.build.OptionData;
import net.dv8tion.jda.api.interactions.commands.build.SlashCommandData;

import java.util.ArrayList;
import java.util.HashMap;

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

	//機能の有効チェック


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
	public static FUNCTION_CHECK_RESULT FUNCTION_CHECK(String GID, String CID){
		return FUNCTION_CHECK_RESULT.ANY;
	}
}
