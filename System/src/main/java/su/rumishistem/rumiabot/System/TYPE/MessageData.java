package su.rumishistem.rumiabot.System.TYPE;

import net.dv8tion.jda.api.entities.Message;

public class MessageData {
	private String ID;
	private String Text;
	private Message DiscordMessage;

	public MessageData(String ID, String Text, Message DiscordMessage) {
		this.ID = ID;
		this.Text = Text;
		this.DiscordMessage = DiscordMessage;
	}

	public String GetID() {
		return ID;
	}

	public String GetText() {
		return Text;
	}

	public void Reply(String Text) {
		if (DiscordMessage != null) {
			DiscordMessage.reply(Text).queue();
		}
	}
}
