package su.rumishistem.rumisanbot.base_system.Software.RumiaBot.Runer;

import su.rumishistem.rumisanbot.base_system.Software.RumiaBot.Type.CommandRuner;
import su.rumishistem.rumisanbot.base_system.Software.RumiaBot.Type.Event.CommandEvent;

public class TestCommand implements CommandRuner{
	@Override
	public void run(CommandEvent e) throws Exception{
		e.reply("こにゃーん");
	}
}
