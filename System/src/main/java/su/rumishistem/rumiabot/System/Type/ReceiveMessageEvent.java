package su.rumishistem.rumiabot.System.Type;

import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import su.rumishistem.rumi_java_lib.MisskeyBot.Event.NewNoteEvent;
import su.rumishistem.rumiabot.System.Main;

public class ReceiveMessageEvent {
	private SourceType type;
	private MessageReceivedEvent discord_event;
	private NewNoteEvent misskey_event;

	public ReceiveMessageEvent(MessageReceivedEvent e) {
		type = SourceType.Discord;
		discord_event = e;
	}

	public ReceiveMessageEvent(NewNoteEvent e) {
		type = SourceType.Misskey;
		misskey_event = e;
	}

	public SourceType get_source() {
		return type;
	}

	public NewNoteEvent get_misskey() {
		return misskey_event;
	}

	public MessageReceivedEvent get_discord() {
		return discord_event;
	}

	public boolean is_mention() {
		if (type == SourceType.Misskey) {
			return misskey_event.get_note().is_mention();
		} else if (type == SourceType.Discord) {
			return discord_event.getMessage().getMentions().getUsers().contains(Main.get_discord_bot().get_primary_bot().getSelfUser());
		} else {
			return false;
		}
	}
}
