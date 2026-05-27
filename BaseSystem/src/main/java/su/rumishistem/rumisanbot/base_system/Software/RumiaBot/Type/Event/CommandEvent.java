package su.rumishistem.rumisanbot.base_system.Software.RumiaBot.Type.Event;

import java.util.Map;

import su.rumishistem.rumisanbot.base_system.Command;
import su.rumishistem.rumisanbot.base_system.Software.RumiaBot.Type.CommandOptionValue;
import su.rumishistem.rumisanbot.base_system.Tool.DiscordMessageID;
import su.rumishistem.rumisanbot.base_system.Type.ContentsSource;
import su.rumishistem.rumisanbot.base_system.Type.NotePublicSetting;
import su.rumishistem.rumisanbot.base_system.Type.Event.ReceiveMessageEvent;

public class CommandEvent {
	private final ReceiveMessageEvent e;
	private final Map<String, CommandOptionValue> option_list;

	public CommandEvent(ReceiveMessageEvent e, Map<String, CommandOptionValue> option_list) {
		this.e = e;
		this.option_list = option_list;
	}

	public boolean is_exists_option(String name) {
		return option_list.get(name) != null;
	}

	public CommandOptionValue get_option(String name) {
		return option_list.get(name);
	}

	public void reply(String text) {
		//Discordのインタラクションか？
		if (e.get_message().id.startsWith("!IT")) {
			Command.discord_interaction_defer(e.get_message().id, false);
			Command.discord_interaction_reply(e.get_message().id, true, text);
		} else if (e.get_message().source == ContentsSource.Misskey) {
			Command.misskey_create_note(text, e.get_message().id, null, NotePublicSetting.Home, false);
		} else if (e.get_message().source == ContentsSource.Discord) {
			String channel_id = DiscordMessageID.parse(e.get_message().id)[1];
			String message_id = DiscordMessageID.parse(e.get_message().id)[2];
			Command.discord_reply_message(channel_id, message_id, text);
		}
	}
}
