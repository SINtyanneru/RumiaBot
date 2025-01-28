package su.rumishistem.rumiabot.Discord.COMMAND;

import java.time.ZoneOffset;

import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import su.rumishistem.rumiabot.MODULE.DATE_FORMAT;
import su.rumishistem.rumiabot.MODULE.ISHITEGAWA.DAM_STATUS;
import su.rumishistem.rumiabot.MODULE.ISHITEGAWA.ISHITEGAWA_DAM;

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
