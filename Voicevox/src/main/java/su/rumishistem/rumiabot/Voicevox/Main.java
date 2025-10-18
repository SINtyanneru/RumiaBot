package su.rumishistem.rumiabot.Voicevox;

import static su.rumishistem.rumi_java_lib.LOG_PRINT.Main.LOG;
import java.util.HashMap;
import java.util.List;

import net.dv8tion.jda.api.entities.channel.concrete.VoiceChannel;
import net.dv8tion.jda.api.events.guild.voice.GuildVoiceUpdateEvent;
import su.rumishistem.rumi_java_lib.LOG_PRINT.LOG_TYPE;
import su.rumishistem.rumiabot.System.CommandRegister;
import su.rumishistem.rumiabot.System.Type.CommandInteraction;
import su.rumishistem.rumiabot.System.Type.CommandOptionRegist;
import su.rumishistem.rumiabot.System.Type.FunctionClass;
import su.rumishistem.rumiabot.System.Type.OptionType;
import su.rumishistem.rumiabot.System.Type.ReceiveMessageEvent;
import su.rumishistem.rumiabot.System.Type.RunCommand;
import su.rumishistem.rumiabot.System.Type.SourceType;
import su.rumishistem.rumiabot.Voicevox.GenAudio.GenCommand;
import su.rumishistem.rumiabot.Voicevox.Jomiage.Jomiage;

public class Main implements FunctionClass{
	public static List<HashMap<String, String>> SpeakersList = null;
	public static boolean Enabled = false;

	@Override
	public String function_name() {
		return "VOICEVOX";
	}
	@Override
	public String function_version() {
		return "1.0";
	}
	@Override
	public String function_author() {
		return "るみ";
	}

	@Override
	public void init() {
		SpeakersList = VOICEVOX.getSpeakers();

		if (SpeakersList.size() != 0) {
			for (HashMap<String, String> Row:SpeakersList) {
				LOG(LOG_TYPE.OK, "[VOICEVOX]+ " + Row.get("NAME") + "(" + Row.get("ID") + ")");
			}

			LOG(LOG_TYPE.OK, "[VOICEVOX]" + SpeakersList.size() + "人の話者を読み込みました");
		} else {
			throw new Error("VOICEVOX：話者一覧の取得に失敗");
		}

		CommandRegister.add_command("voicevox", new CommandOptionRegist[] {
			new CommandOptionRegist("text", OptionType.String, true)
		}, false, new RunCommand() {
			@Override
			public void run(CommandInteraction e) throws Exception {
				GenCommand.RunCommand(e);
			}
		});

		CommandRegister.add_command("jomiage", new CommandOptionRegist[] {}, false, new RunCommand() {
			@Override
			public void run(CommandInteraction e) {
				if (e.get_source() != SourceType.Discord) {
					e.reply("Discordでしか使えません。");
				}
			}
		});
	}

	@Override
	public void message_receive(ReceiveMessageEvent e) {
		Jomiage.ReceiveMessage(e);
	}

	/*
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
	}*/
}
