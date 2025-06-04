package su.rumishistem.rumiabot.System.TYPE;

import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.channel.Channel;

public class DiscordEvent {
	private Object EventClass;
	private EventType Type;
	private Guild Server;
	private Channel Ch;

	public enum EventType {
		GuildMemberAdd,
		GuildMemberRemove,
		VCMemberUpdate
	}

	public DiscordEvent(Object EventClass, EventType Type, Guild Server, Channel Ch) {
		this.EventClass = EventClass;
		this.Type = Type;
		this.Server = Server;
		this.Ch = Ch;
	}

	public Object GetEventClass() {
		return EventClass;
	}

	public EventType GetType() {
		return Type;
	}

	public Guild GetGuild() {
		return Server;
	}

	public Channel GetChannel() {
		return Ch;
	}
}
