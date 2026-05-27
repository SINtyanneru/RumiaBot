package su.rumishistem.rumisanbot.base_system.Software.RumiaBot.Runer;

import su.rumishistem.rumisanbot.base_system.Main;
import su.rumishistem.rumisanbot.base_system.Software.RumiaBot.Type.*;
import su.rumishistem.rumisanbot.base_system.Software.RumiaBot.Type.Event.CommandEvent;

public class HelpCommand implements CommandRuner {
	@Override
	public void run(CommandEvent e) throws Exception {
		StringBuilder sb = new StringBuilder();

		for (CommandData cmd:Main.rumiabot.command_list) {
			sb.append("┌["+cmd.name+"]\n");
			sb.append("├"+cmd.description+"\n");
			sb.append("└使用方法→\n");
		}

		e.reply(sb.toString());
	}
}
