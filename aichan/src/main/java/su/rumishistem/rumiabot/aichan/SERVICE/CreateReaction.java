package su.rumishistem.rumiabot.aichan.SERVICE;

import static su.rumishistem.rumiabot.System.Main.MisskeyBOT;
import java.io.IOException;

public class CreateReaction {
	public static void Create(String Reaction, String NoteID) throws IOException {
		if (NoteID.startsWith("Misskey_")) {
			NoteID = NoteID.replace("Misskey_", "");
			MisskeyBOT.CreateReaction(MisskeyBOT.GetNote(NoteID), Reaction);
		} else if (NoteID.startsWith("Discord_")) {
			//TODO:Discordを組め
		} else {
			throw new Error("どっちの投稿かわかりません");
		}
	}
}
