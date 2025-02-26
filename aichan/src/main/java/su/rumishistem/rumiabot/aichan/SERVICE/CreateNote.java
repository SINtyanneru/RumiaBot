package su.rumishistem.rumiabot.aichan.SERVICE;

import static su.rumishistem.rumiabot.System.Main.MisskeyBOT;
import java.io.IOException;
import java.util.HashMap;
import com.fasterxml.jackson.databind.ObjectMapper;

import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.emoji.Emoji;
import su.rumishistem.rumi_java_lib.Misskey.Builder.NoteBuilder;
import su.rumishistem.rumi_java_lib.Misskey.TYPE.Note;
import su.rumishistem.rumi_java_lib.Misskey.TYPE.NoteVis;
import su.rumishistem.rumiabot.aichan.MODULE.GetDiscordMessage;

public class CreateNote {
	public static String Create(String TEXT, String ReplyID) throws IOException {
		TEXT = "藍:" + TEXT;

		NoteBuilder NB = new NoteBuilder();
		NB.setTEXT(TEXT);
		NB.setVIS(NoteVis.PUBLIC);

		if (ReplyID != null) {
			if (ReplyID.startsWith("Misskey_")) {
				//Misskey返信
				ReplyID = ReplyID.replace("Misskey_", "");
				Note ReplyNote = MisskeyBOT.GetNote(ReplyID);
				NB.setREPLY(ReplyNote);
			} else if (ReplyID.startsWith("Discord_")) {
				//Discord返信
				Message MSG = GetDiscordMessage.Get(ReplyID);
				if (MSG != null) {
					MSG.reply(TEXT).queue();
				}
				//Discordはここで終了
				return "";
			} else {
				throw new Error("どっちの投稿かわかりません");
			}
		}

		//投稿
		MisskeyBOT.PostNote(NB.Build());

		//misskey/note.tsの型に合うように作ったけどどうだろうか？
		HashMap<String, Object> RETURN = new HashMap<String, Object>();
		RETURN.put("id", "a");
		RETURN.put("text", TEXT);
		RETURN.put("reply", null);
		RETURN.put("poll", null);
		return new ObjectMapper().writeValueAsString(RETURN);
	}
}
