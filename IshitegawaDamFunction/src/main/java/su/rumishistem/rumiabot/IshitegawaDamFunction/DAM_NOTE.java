package su.rumishistem.rumiabot.IshitegawaDamFunction;

import su.rumishistem.rumi_java_lib.MisskeyBot.Builder.NoteBuilder;

public class DAM_NOTE {
	public static void Main() {
		try {
			//Misskeyに投稿する
			NoteBuilder nb = new NoteBuilder();
			nb.set_text(DAMDAM.genTEXT() + "\n#石手川ダム");

			su.rumishistem.rumiabot.System.Main.get_misskey_bot().get_client().create_note(nb);
		} catch (Exception EX) {
			EX.printStackTrace();
		}
	}
}
