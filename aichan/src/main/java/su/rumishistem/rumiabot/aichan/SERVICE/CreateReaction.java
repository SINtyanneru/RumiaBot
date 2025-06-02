package su.rumishistem.rumiabot.aichan.SERVICE;

import static su.rumishistem.rumiabot.System.Main.MisskeyBOT;
import java.io.IOException;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.emoji.Emoji;
import su.rumishistem.rumiabot.aichan.MODULE.GetDiscordMessage;

public class CreateReaction {
	public static void Create(String Reaction, String NoteID) throws IOException {
		if (NoteID.startsWith("M-")) {
			NoteID = NoteID.replace("M-", "");
			MisskeyBOT.CreateReaction(MisskeyBOT.GetNote(NoteID), Reaction);
		} else if (NoteID.startsWith("D-")) {
			Message MSG = GetDiscordMessage.Get(NoteID);
			if (MSG != null) {
				//相互変換必須
				String DiscordEmoji = Reaction;
				switch (Reaction) {
					case "love":
						DiscordEmoji = "♥️";
						break;
				}

				//リアクション
				MSG.addReaction(Emoji.fromFormatted(DiscordEmoji)).queue();
			}
		} else {
			throw new Error("どっちの投稿かわかりません");
		}
	}
}
