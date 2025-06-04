package su.rumishistem.rumiabot.Voicevox.GenAudio;

import java.io.File;
import java.util.UUID;
import su.rumishistem.rumiabot.System.TYPE.CommandInteraction;
import su.rumishistem.rumiabot.Voicevox.VOICEVOX;

public class GenCommand {
	public static void RunCommand(CommandInteraction CI) throws Exception {
		//HashMap<String, String> Speakers = Main.SpeakersList.get(0);
		int SpeakersIndex = 0;

		String AudioQuery = VOICEVOX.genAudioQuery(SpeakersIndex, CI.GetCommand().GetOption("text").GetValueAsString());
		File AudioFile = VOICEVOX.genAudioFile(SpeakersIndex, AudioQuery);
		File AudioFileRename = new File("/tmp/" + UUID.randomUUID().toString() + ".wav");
		AudioFile.renameTo(AudioFileRename);

		CI.AddFile(AudioFileRename);
		CI.Reply("生成した");

		AudioFileRename.delete();
		AudioFile.delete();
	}
}
