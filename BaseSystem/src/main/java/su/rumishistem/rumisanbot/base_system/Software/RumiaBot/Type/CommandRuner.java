package su.rumishistem.rumisanbot.base_system.Software.RumiaBot.Type;

import su.rumishistem.rumisanbot.base_system.Software.RumiaBot.Type.Event.CommandEvent;

public interface CommandRuner {
	void run(CommandEvent e) throws Exception;
}
