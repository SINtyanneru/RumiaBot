package su.rumishistem.rumiabot.Voicevox;

import static su.rumishistem.rumi_java_lib.LOG_PRINT.Main.LOG;
import static su.rumishistem.rumiabot.System.FunctionModuleLoader.AddCommand;
import java.util.HashMap;
import java.util.List;

import net.dv8tion.jda.api.entities.channel.concrete.VoiceChannel;
import net.dv8tion.jda.api.events.guild.voice.GuildVoiceUpdateEvent;
import su.rumishistem.rumi_java_lib.LOG_PRINT.LOG_TYPE;
import su.rumishistem.rumiabot.System.MODULE.BlockManager;
import su.rumishistem.rumiabot.System.TYPE.CommandData;
import su.rumishistem.rumiabot.System.TYPE.CommandInteraction;
import su.rumishistem.rumiabot.System.TYPE.CommandOption;
import su.rumishistem.rumiabot.System.TYPE.CommandOptionType;
import su.rumishistem.rumiabot.System.TYPE.DiscordEvent;
import su.rumishistem.rumiabot.System.TYPE.DiscordEvent.EventType;
import su.rumishistem.rumiabot.System.TYPE.FunctionClass;
import su.rumishistem.rumiabot.System.TYPE.ReceiveMessageEvent;
import su.rumishistem.rumiabot.System.TYPE.SourceType;
import su.rumishistem.rumiabot.Voicevox.GenAudio.GenCommand;
import su.rumishistem.rumiabot.Voicevox.Jomiage.Jomiage;

public class Main implements FunctionClass{
	private static final String FUNCTION_NAME = "ユーザー情報をぶちまけよう";
	private static final String FUNCTION_VERSION = "1.0";
	private static final String FUNCTION_AUTOR = "Rumisan";
	public static List<HashMap<String, String>> SpeakersList = null;

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
		AddCommand(new CommandData("jomiage", new CommandOption[] {}, false));
	}

	@Override
	public void ReceiveMessage(ReceiveMessageEvent e) {
		if (e.GetSource() == SourceType.Discord) {
			//ブロック済みのユーザーなら此処で処理を中断する
			if (BlockManager.IsBlocked(SourceType.Discord, e.GetUser().GetID())) {
				return;
			}

			Jomiage.ReceiveMessage(e.GetMessage());
		}
	}

	@Override
	public boolean GetAllowCommand(String Name) {
		return (Name.equals("voicevox") || Name.equals("jomiage"));
	}

	@Override
	public void RunCommand(CommandInteraction CI) throws Exception {
		if (CI.GetCommand().GetName().equals("voicevox")) {
			GenCommand.RunCommand(CI);
		} else {
			if (CI.GetSource() != SourceType.Discord) {
				CI.Reply("Discordでしか使えません。");
				return;
			}

			Jomiage.RunCommand(CI);
		}
	}

	@Override
	public void DiscordEventReceive(DiscordEvent e) throws Exception {
		if (e.GetType() == EventType.VCMemberUpdate) {
			GuildVoiceUpdateEvent event = (GuildVoiceUpdateEvent)e.GetEventClass();

			String Ch = null;

			if (event.getChannelJoined() != null) {
				Ch = event.getChannelJoined().getId();
			} else if (event.getChannelLeft() != null) {
				Ch = event.getChannelLeft().getId();
			}

			Jomiage.VCMemberUpdate(Ch);
		}
	}
}
