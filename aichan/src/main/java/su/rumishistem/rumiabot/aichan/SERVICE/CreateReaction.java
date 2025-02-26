package su.rumishistem.rumiabot.aichan.SERVICE;

import static su.rumishistem.rumiabot.System.Main.MisskeyBOT;
import static su.rumishistem.rumiabot.System.Main.DISCORD_BOT;
import java.io.IOException;
import java.util.Iterator;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.channel.concrete.TextChannel;
import net.dv8tion.jda.api.entities.emoji.CustomEmoji;
import net.dv8tion.jda.api.entities.emoji.Emoji;
import su.rumishistem.rumiabot.aichan.MODULE.GetDiscordMessage;

public class CreateReaction {
	public static void Create(String Reaction, String NoteID) throws IOException {
		if (NoteID.startsWith("Misskey_")) {
			NoteID = NoteID.replace("Misskey_", "");
			MisskeyBOT.CreateReaction(MisskeyBOT.GetNote(NoteID), Reaction);
		} else if (NoteID.startsWith("Discord_")) {
			Message MSG = GetDiscordMessage.Get(NoteID);
			if (MSG != null) {
				//相互変換必須
				String DiscordEmoji = "♥️";
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
