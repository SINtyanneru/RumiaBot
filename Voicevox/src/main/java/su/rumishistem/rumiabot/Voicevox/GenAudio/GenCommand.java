package su.rumishistem.rumiabot.Voicevox.GenAudio;

import java.io.File;
import java.util.UUID;
import su.rumishistem.rumiabot.System.Type.CommandInteraction;
import su.rumishistem.rumiabot.Voicevox.VOICEVOX;

public class GenCommand {
	public static void RunCommand(CommandInteraction e) throws Exception {
		//HashMap<String, String> Speakers = Main.SpeakersList.get(0);
		int SpeakersIndex = 0;

		String AudioQuery = VOICEVOX.genAudioQuery(SpeakersIndex, e.get_option_as_string("text"));
		File AudioFile = VOICEVOX.genAudioFile(SpeakersIndex, AudioQuery);
		File AudioFileRename = new File("/tmp/" + UUID.randomUUID().toString() + ".wav");
		AudioFile.renameTo(AudioFileRename);

		e.add_file(AudioFileRename);
		e.reply("生成した");

		AudioFileRename.delete();
		AudioFile.delete();
	}
}
