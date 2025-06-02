package su.rumishistem.rumiabot.aichan.SERVICE;

import static su.rumishistem.rumiabot.System.Main.MisskeyBOT;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.utils.FileUpload;
import su.rumishistem.rumi_java_lib.Misskey.Builder.NoteBuilder;
import su.rumishistem.rumi_java_lib.Misskey.TYPE.Note;
import su.rumishistem.rumi_java_lib.Misskey.TYPE.NoteVis;
import su.rumishistem.rumiabot.aichan.MODULE.GetDiscordMessage;

public class CreateNote {
	public static String Create(String TEXT, String ReplyID, List<File> FileList) throws IOException {
		TEXT = "藍:" + TEXT;

		NoteBuilder NB = new NoteBuilder();
		NB.setTEXT(TEXT);
		NB.setVIS(NoteVis.PUBLIC);

		if (ReplyID != null) {
			if (ReplyID.startsWith("M-")) {
				//Misskey返信
				ReplyID = ReplyID.replace("M-", "");
				Note ReplyNote = MisskeyBOT.GetNote(ReplyID);
				if (ReplyNote != null) {
					NB.setREPLY(ReplyNote);
				} else {
					throw new Error("リプライ先のノートが見つかりませんでした");
				}
			} else if (ReplyID.startsWith("D-")) {
				//Discord返信
				Message MSG = GetDiscordMessage.Get(ReplyID);
				if (MSG != null) {
					String[] PostMessageID = {null};

					//ファイル
					List<FileUpload> DiscordFileList = new ArrayList<FileUpload>();
					for (File F:FileList) {
						DiscordFileList.add(FileUpload.fromData(F));
					}

					MSG.reply(TEXT).addFiles(DiscordFileList).queue(
						(PostMessage)->{
							PostMessageID[0] = PostMessage.getId();
						}
					);

					//Discordはここで終了
					while (true) {
						if (PostMessageID[0] != null) {
							return GenResult(PostMessageID[0], TEXT);
						} else {
							System.out.flush();
						}
					}
				} else {
					throw new Error("あ");
				}
			} else {
				throw new Error("どっちの投稿かわかりません");
			}
		}

		//ファイル
		for (File F:FileList) {
			NB.AddFile(F);
		}

		//投稿
		Note PostedNote = MisskeyBOT.PostNote(NB.Build());
		return GenResult(PostedNote.getID(), TEXT);
	}

	private static String GenResult(String ID,String TEXT) throws JsonProcessingException {
		//misskey/note.tsの型に合うように作ったけどどうだろうか？
		HashMap<String, Object> RETURN = new HashMap<String, Object>();
		RETURN.put("id", ID);
		RETURN.put("text", TEXT);
		RETURN.put("reply", null);
		RETURN.put("poll", null);
		return new ObjectMapper().writeValueAsString(RETURN);
	}
}
