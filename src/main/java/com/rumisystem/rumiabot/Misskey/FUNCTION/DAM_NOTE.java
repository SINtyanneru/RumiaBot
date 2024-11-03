package com.rumisystem.rumiabot.Misskey.FUNCTION;

import static com.rumisystem.rumiabot.Main.MisskeyBOT;

import com.rumisystem.rumi_java_lib.Misskey.Builder.NoteBuilder;
import com.rumisystem.rumiabot.MODULE.ISHITEGAWA.ISHITEGAWA_DAM;

public class DAM_NOTE {
	public static void Main() {
		try {
			//Misskeyに投稿する
			NoteBuilder NB = new NoteBuilder();
			NB.setTEXT(ISHITEGAWA_DAM.genTEXT() + "\n#石手川ダム");

			MisskeyBOT.PostNote(NB.Build());
		} catch (Exception EX) {
			EX.printStackTrace();
		}
	}
}
