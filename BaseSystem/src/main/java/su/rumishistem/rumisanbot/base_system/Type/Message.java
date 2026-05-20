package su.rumishistem.rumisanbot.base_system.Type;

public class Message {
	public final ContentsSource source;
	public final String id;
	public final String text;
	public final User user;

	public Message(ContentsSource source, String id, String text, User user) {
		this.source = source;
		this.id = id;
		this.text = text;
		this.user = user;
	}
}
