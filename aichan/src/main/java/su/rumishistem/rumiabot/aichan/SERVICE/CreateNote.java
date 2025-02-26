package su.rumishistem.rumiabot.aichan.SERVICE;

import static su.rumishistem.rumiabot.System.Main.MisskeyBOT;
import java.io.IOException;
import java.util.HashMap;
import com.fasterxml.jackson.databind.ObjectMapper;
import su.rumishistem.rumi_java_lib.Misskey.Builder.NoteBuilder;
import su.rumishistem.rumi_java_lib.Misskey.TYPE.Note;
import su.rumishistem.rumi_java_lib.Misskey.TYPE.NoteVis;

public class CreateNote {
	public static String Create(String TEXT, String ReplyID) throws IOException {
		NoteBuilder NB = new NoteBuilder();
		NB.setTEXT(TEXT);
		NB.setVIS(NoteVis.PUBLIC);

		if (ReplyID != null) {
			if (ReplyID.startsWith("Misskey_")) {
				ReplyID = ReplyID.replace("Misskey_", "");
				Note ReplyNote = MisskeyBOT.GetNote(ReplyID);
				NB.setREPLY(ReplyNote);
			} else if (ReplyID.startsWith("Discord_")) {
				//TODO:Discordを組め
				return "";
			} else {
				throw new Error("どっちの投稿かわかりません");
			}
		}

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
