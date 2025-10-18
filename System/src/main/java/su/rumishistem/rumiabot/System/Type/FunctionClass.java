package su.rumishistem.rumiabot.System.Type;

import java.util.HashMap;

import net.dv8tion.jda.api.events.interaction.component.ButtonInteractionEvent;

public interface FunctionClass {
	String function_name();
	String function_version();
	String function_author();

	void init();

	default void message_receive(ReceiveMessageEvent e) {}
	default void event_receive(EventReceiveEvent e) {}

	//Discord
	default void discord_button_event(String id, HashMap<String, String> param, ButtonInteractionEvent e) {}
}
