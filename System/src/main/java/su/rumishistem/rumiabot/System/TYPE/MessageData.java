package su.rumishistem.rumiabot.System.TYPE;

import static su.rumishistem.rumiabot.System.Main.MisskeyBOT;
import net.dv8tion.jda.api.entities.Message;
import su.rumishistem.rumi_java_lib.Misskey.Builder.NoteBuilder;
import su.rumishistem.rumi_java_lib.Misskey.TYPE.Note;

public class MessageData {
	private String ID;
	private String Text;
	private Message DiscordMessage;
	private Note MisskeyNote;
	private boolean KaiMention;

	public MessageData(String ID, String Text, Message DiscordMessage, Note MisskeyNote, boolean KaiMention) {
		this.ID = ID;
		this.Text = Text;
		this.DiscordMessage = DiscordMessage;
		this.MisskeyNote = MisskeyNote;
		this.KaiMention = KaiMention;
	}

	public String GetID() {
		return ID;
	}

	public String GetText() {
		return Text;
	}

	public void Reply(String Text) {
		try {
			if (DiscordMessage != null) {
				DiscordMessage.reply(Text).queue();
			} else if (MisskeyNote != null) {
				NoteBuilder NB = new NoteBuilder();
				NB.setTEXT(Text);
				NB.setREPLY(MisskeyNote);
				MisskeyBOT.PostNote(NB.Build());
			} else {
				throw new Error("メッセージの型がわけわからん");
			}
		} catch (Exception EX) {
			EX.printStackTrace();
		}
	}

	public boolean isKaiMention() {
		return KaiMention;
	}
}
