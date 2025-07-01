package su.rumishistem.rumiabot.System.TYPE;

import static su.rumishistem.rumiabot.System.Main.MisskeyBOT;

import java.sql.SQLException;

import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.channel.middleman.MessageChannel;
import su.rumishistem.rumi_java_lib.ArrayNode;
import su.rumishistem.rumi_java_lib.SQL;
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

	public void Delete() {
		if (DiscordMessage != null) {
			DiscordMessage.delete().queue();
		} else {
			throw new Error("Discordのメッセージ以外を消すことはできません");
		}
	}

	public boolean isKaiMention() {
		return KaiMention;
	}

	public MessageChannel GetDiscordChannel() {
		if (DiscordMessage != null) {
			return DiscordMessage.getChannel();
		} else {
			return null;
		}
	}

	public Guild GetDiscordGuild() {
		if (DiscordMessage != null) {
			return DiscordMessage.getGuild();
		} else {
			return null;
		}
	}

	public boolean CheckDiscordGuildFunctionEnabled(DiscordFunction FunctionName) throws SQLException {
		if (DiscordMessage != null) {
			ArrayNode SQL_RESULT = SQL.RUN("SELECT * FROM `CONFIG` WHERE `GID` = ? AND `FUNC_ID` = ? AND `CID` = '';", new Object[] {
				DiscordMessage.getGuildId(),
				FunctionName.name()
			});
			if (SQL_RESULT.asArrayList().size() == 1) {
				return true;
			} else {
				return false;
			}
		} else {
			return false;
		}
	}
}
