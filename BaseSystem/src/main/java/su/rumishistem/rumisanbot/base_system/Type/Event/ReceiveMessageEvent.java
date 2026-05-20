package su.rumishistem.rumisanbot.base_system.Type.Event;

import su.rumishistem.rumisanbot.base_system.Type.Message;
import su.rumishistem.rumisanbot.base_system.Type.User;

public class ReceiveMessageEvent {
	private final Message message;

	public ReceiveMessageEvent(Message message) {
		this.message = message;
	}

	public Message get_message() {
		return message;
	}

	public User get_user() {
		return message.user;
	}
}
