package su.rumishistem.rumiabot.System.TYPE;

import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.channel.concrete.TextChannel;

public class ReceiveMessageEvent {
	private SourceType Source;
	private MessageUser User;
	private MessageData MSG;

	private Member DiscordMember;
	private TextChannel DiscordChannel;

	public ReceiveMessageEvent(SourceType Source, MessageUser User, Member DiscordMember, TextChannel DiscordChannel, MessageData MSG) {
		this.Source = Source;
		this.User = User;
		this.MSG = MSG;
		this.DiscordMember = DiscordMember;
		this.DiscordChannel = DiscordChannel;
	}

	public SourceType GetSource() {
		return Source;
	}

	public MessageUser GetUser() {
		return User;
	}

	public Member GetDiscordMember() {
		return DiscordMember;
	}
	
	public TextChannel GetDiscordChannel() {
		return DiscordChannel;
	}

	public MessageData GetMessage() {
		return MSG;
	}
}
