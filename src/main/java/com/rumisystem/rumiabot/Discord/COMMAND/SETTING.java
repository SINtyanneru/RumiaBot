package com.rumisystem.rumiabot.Discord.COMMAND;

import com.rumisystem.rumiabot.Discord.FUNCTION.FUNCTION_MANAGER;

import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;

public class SETTING {
	public static void Main(SlashCommandInteractionEvent IT) {
		try{
			String FUNCTION_NAME = IT.getOption("function").getAsString();
			String TF = IT.getOption("tf").getAsString();
			if (TF.equals("true")) {
				//登録処理
				//既に設定が登録されているなら処理しない
				if (!FUNCTION_MANAGER.FUNCTION_CHECK(IT.getGuild().getId(), null, FUNCTION_NAME)) {
					FUNCTION_MANAGER.REGIST(IT.getGuild().getId(), null, FUNCTION_NAME);
					IT.getHook().editOriginal("✅").queue();
				}
			} else {
				//登録削除
				String SQL_SETTING_ID = FUNCTION_MANAGER.GET(IT.getGuild().getId(), null, FUNCTION_NAME);
				if (SQL_SETTING_ID != null) {
					FUNCTION_MANAGER.DELETE(SQL_SETTING_ID);
					IT.getHook().editOriginal("✅").queue();
				} else {
					IT.getHook().editOriginal("抑登録されていない").queue();
				}
			}

		} catch (Exception EX) {
			EX.printStackTrace();
			IT.getHook().editOriginal("エラー：" + EX.getMessage()).queue();
		}
	}
}
