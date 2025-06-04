package su.rumishistem.rumiabot.Voicevox;

import static su.rumishistem.rumi_java_lib.LOG_PRINT.Main.LOG;
import static su.rumishistem.rumiabot.System.FunctionModuleLoader.AddCommand;

import java.io.File;
import java.util.HashMap;
import java.util.List;
import java.util.UUID;

import su.rumishistem.rumi_java_lib.LOG_PRINT.LOG_TYPE;
import su.rumishistem.rumiabot.System.TYPE.CommandData;
import su.rumishistem.rumiabot.System.TYPE.CommandInteraction;
import su.rumishistem.rumiabot.System.TYPE.CommandOption;
import su.rumishistem.rumiabot.System.TYPE.CommandOptionType;
import su.rumishistem.rumiabot.System.TYPE.FunctionClass;
import su.rumishistem.rumiabot.System.TYPE.ReceiveMessageEvent;

public class Main implements FunctionClass{
	private static final String FUNCTION_NAME = "ユーザー情報をぶちまけよう";
	private static final String FUNCTION_VERSION = "1.0";
	private static final String FUNCTION_AUTOR = "Rumisan";
	private static List<HashMap<String, String>> SpeakersList = null;

	public static boolean Enabled = false;

	@Override
	public String FUNCTION_NAME() {
		return FUNCTION_NAME;
	}
	@Override
	public String FUNCTION_VERSION() {
		return FUNCTION_VERSION;
	}
	@Override
	public String FUNCTION_AUTOR() {
		return FUNCTION_AUTOR;
	}

	@Override
	public void Init() {
		SpeakersList = VOICEVOX.getSpeakers();

		if (SpeakersList.size() != 0) {
			for (HashMap<String, String> Row:SpeakersList) {
				LOG(LOG_TYPE.OK, "[VOICEVOX]+ " + Row.get("NAME") + "(" + Row.get("ID") + ")");
			}

			LOG(LOG_TYPE.OK, "[VOICEVOX]" + SpeakersList.size() + "人の話者を読み込みました");
		} else {
			throw new Error("VOICEVOX：話者一覧の取得に失敗");
		}

		AddCommand(new CommandData("voicevox", new CommandOption[] {
			new CommandOption("text", CommandOptionType.String, null, true)
		}, false));
	}

	@Override
	public void ReceiveMessage(ReceiveMessageEvent e) {}

	@Override
	public boolean GetAllowCommand(String Name) {
		return Name.equals("voicevox");
	}

	@Override
	public void RunCommand(CommandInteraction CI) throws Exception {
		HashMap<String, String> Speakers = SpeakersList.get(0);
		int SpeakersIndex = 0;

		String AudioQuery = VOICEVOX.genAudioQuery(SpeakersIndex, CI.GetCommand().GetOption("text").GetValueAsString());
		File AudioFile = VOICEVOX.genAudio(SpeakersIndex, AudioQuery);
		File AudioFileRename = new File("/tmp/" + UUID.randomUUID().toString() + ".wav");
		AudioFile.renameTo(AudioFileRename);

		CI.AddFile(AudioFileRename);
		CI.Reply("生成した");

		AudioFileRename.delete();
		AudioFile.delete();
	}
}
