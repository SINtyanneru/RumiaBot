package su.rumishistem.rumiabot.Joke;

import static su.rumishistem.rumiabot.System.Main.CONFIG_DATA;

import java.io.IOException;
import java.net.URLEncoder;
import java.util.UUID;

import net.dv8tion.jda.api.events.interaction.command.MessageContextInteractionEvent;
import su.rumishistem.rumi_java_lib.*;
import su.rumishistem.rumiabot.System.MODULE.ErrorPrinter;

public class GjakuHonjaku {
	public static void RunContextmenu(MessageContextInteractionEvent e) {
		if (e.getName().equals("rvtr")) {
			String text = e.getTarget().getContentRaw();
			e.deferReply().queue();

			try {
				FETCH ajax = new FETCH("https://script.google.com/macros/s/"+CONFIG_DATA.get("GOOGLE").getData("RVTR_MACRO_ID").asString()+"/exec?text=" + URLEncoder.encode(text));
				ajax.setFollowRedirect(true);
				FETCH_RESULT result = ajax.GET();

				if (result.getStatusCode() == 200) {
					String rv_text = result.getString();

					e.getHook().editOriginal("逆翻訳：" + rv_text.replace("@", "[AD]")).queue();
				} else {
					throw new RuntimeException("GoogleのAPIがエラーを吐いたぜ");
				}
			} catch (IOException ex) {
				String id = UUID.randomUUID().toString();
				e.getHook().editOriginal("エラー:" + id).queue();
				ErrorPrinter.print(id, ex);
				return;
			}
		}
	}
}
