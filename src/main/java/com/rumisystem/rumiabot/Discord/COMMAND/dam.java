package com.rumisystem.rumiabot.Discord.COMMAND;

import java.time.ZoneOffset;

import com.rumisystem.rumiabot.MODULE.DATE_FORMAT;
import com.rumisystem.rumiabot.MODULE.ISHITEGAWA.DAM_STATUS;
import com.rumisystem.rumiabot.MODULE.ISHITEGAWA.ISHITEGAWA_DAM;

import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;

public class dam {
	public static void Main(SlashCommandInteractionEvent IT) {
		try{
			DAM_STATUS STATUS = ISHITEGAWA_DAM.STATUS;

			IT.getHook().editOriginal(ISHITEGAWA_DAM.genTEXT()).queue();
		} catch (Exception EX) {
			EX.printStackTrace();
			IT.getHook().editOriginal("取得失敗").queue();
		}
	}
}
