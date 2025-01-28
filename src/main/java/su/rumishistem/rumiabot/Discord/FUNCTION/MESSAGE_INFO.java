package su.rumishistem.rumiabot.Discord.FUNCTION;

import net.dv8tion.jda.api.entities.Message;
import su.rumishistem.rumiabot.MODULE.DATE_FORMAT;

public class MESSAGE_INFO {
	public static void Main(Message REPLY_MSG, Message MSG) {
		String TEXT =REPLY_MSG.getContentRaw();
		TEXT = TEXT.replaceAll("`", "\\`");

		MSG.reply(	"# Messēżi talå̄g\n"
				+ "tvais:" + DATE_FORMAT.KOUKI(REPLY_MSG.getTimeCreated()) + "\n"
				+ "tvais(naṡonal):" + DATE_FORMAT.ZHUUNI_H(REPLY_MSG.getTimeCreated()) + "\n"
				+ "Sendeljå:" + REPLY_MSG.getAuthor().getName() + "(" + REPLY_MSG.getAuthor().getId() + ")" + "\n"
				+ "Messēżi ID:" + REPLY_MSG.getId() + "\n"
				+ "```\n" + TEXT + "\n```"
		).queue();
	}
}
