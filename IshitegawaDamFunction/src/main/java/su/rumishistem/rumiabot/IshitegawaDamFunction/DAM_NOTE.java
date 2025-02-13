package su.rumishistem.rumiabot.IshitegawaDamFunction;

import static su.rumishistem.rumiabot.System.Main.MisskeyBOT;
import su.rumishistem.rumi_java_lib.Misskey.Builder.NoteBuilder;

public class DAM_NOTE {
	public static void Main() {
		try {
			//Misskeyに投稿する
			NoteBuilder NB = new NoteBuilder();
			NB.setTEXT(DAMDAM.genTEXT() + "\n#石手川ダム");

			MisskeyBOT.PostNote(NB.Build());
		} catch (Exception EX) {
			EX.printStackTrace();
		}
	}
}
