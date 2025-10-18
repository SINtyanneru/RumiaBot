package su.rumishistem.rumiabot.System.Type;

import net.dv8tion.jda.api.events.interaction.command.MessageContextInteractionEvent;

public interface RunDiscordMessageContext {
	void run(MessageContextInteractionEvent e) throws Exception;
}
