package su.rumishistem.rumiabot.System.Type;

import net.dv8tion.jda.api.events.guild.member.GuildMemberJoinEvent;
import net.dv8tion.jda.api.events.guild.member.GuildMemberRemoveEvent;

public class EventReceiveEvent {
	private EventReceiveType type;
	private Object e;

	public EventReceiveEvent(GuildMemberJoinEvent e) {
		this.type = EventReceiveType.DiscordGuildMemberJoin;
		this.e = e;
	}

	public EventReceiveEvent(GuildMemberRemoveEvent e) {
		this.type = EventReceiveType.DiscordGuildMemberLeave;
		this.e = e;
	}

	public EventReceiveType get_type() {
		return type;
	}

	public GuildMemberJoinEvent get_as_discord_guild_member_join() {
		return (GuildMemberJoinEvent) e;
	}

	public GuildMemberRemoveEvent get_as_discord_guild_member_leave() {
		return (GuildMemberRemoveEvent) e;
	}
}
