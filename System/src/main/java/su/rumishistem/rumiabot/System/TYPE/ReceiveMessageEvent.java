package su.rumishistem.rumiabot.System.TYPE;

public class ReceiveMessageEvent {
	private SourceType Source;
	private MessageUser User;
	private MessageData MSG;

	public ReceiveMessageEvent(SourceType Source, MessageUser User, MessageData MSG) {
		this.Source = Source;
		this.User = User;
		this.MSG = MSG;
	}

	public SourceType GetSource() {
		return Source;
	}

	public MessageUser GetUser() {
		return User;
	}

	public MessageData GetMessage() {
		return MSG;
	}
}
