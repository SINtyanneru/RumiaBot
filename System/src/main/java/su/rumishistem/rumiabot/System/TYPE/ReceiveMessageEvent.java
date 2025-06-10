package su.rumishistem.rumiabot.System.TYPE;

import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.channel.concrete.TextChannel;
import su.rumishistem.rumi_java_lib.Misskey.TYPE.Note;

public class ReceiveMessageEvent {
	private SourceType Source;
	private MessageUser User;
	private MessageData MSG;

	private Member DiscordMember;
	private TextChannel DiscordChannel;
	private Message DiscordMessage;

	private Note MisskeyNote;

	public ReceiveMessageEvent(SourceType Source, MessageUser User, Member DiscordMember, TextChannel DiscordChannel, Message DiscordMessage, Note MisskeyNote, MessageData MSG) {
		this.Source = Source;
		this.User = User;
		this.MSG = MSG;
		this.DiscordMember = DiscordMember;
		this.DiscordChannel = DiscordChannel;
		this.DiscordMessage = DiscordMessage;
		this.MisskeyNote = MisskeyNote;
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

	public Message GetDiscordMessage() {
		return DiscordMessage;
	}

	public Note GetMisskeyNote() {
		return MisskeyNote;
	}

	public MessageData GetMessage() {
		return MSG;
	}
}
