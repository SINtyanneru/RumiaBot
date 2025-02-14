package su.rumishistem.rumiabot.System.TYPE;

import static su.rumishistem.rumiabot.System.Main.MisskeyBOT;

import java.io.IOException;

import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import su.rumishistem.rumi_java_lib.Misskey.Builder.NoteBuilder;
import su.rumishistem.rumi_java_lib.Misskey.TYPE.Note;

/**
 * コマンドを実行したときのやつ、インタラクション？
 */
public class CommandInteraction {
	private SourceType Source;
	private SlashCommandInteractionEvent DiscordInteraction;
	private Note MisskeyNote;
	private CommandData Command;

	public CommandInteraction(SourceType Source, Object Interaction, CommandData Command) {
		this.Source = Source;
		this.Command = Command;

		if (Interaction instanceof SlashCommandInteractionEvent) {
			this.DiscordInteraction = (SlashCommandInteractionEvent) Interaction;
		} else if (Interaction instanceof Note) {
			this.MisskeyNote = (Note) Interaction;
		} else {
			throw new Error("Interaction erer");
		}
	}

	public SourceType GetSource() {
		return Source;
	}

	public CommandData GetCommand() {
		return Command;
	}

	public void Reply(String Text) throws IOException {
		if (Command.isPrivate()) {
			//周りに見えないやつ
			if (Source == SourceType.Discord) {
				DiscordInteraction.reply(Text).queue();
			} else if (Source == SourceType.Misskey) {
				NoteBuilder NB = new NoteBuilder();
				NB.setTEXT(Text);
				NB.setREPLY(MisskeyNote);
				MisskeyBOT.PostNote(NB.Build());
			}
		} else {
			//DeferRelyした後のやつ
			if (Source == SourceType.Discord) {
				DiscordInteraction.getHook().editOriginal(Text).queue();
			} else if (Source == SourceType.Misskey) {
				NoteBuilder NB = new NoteBuilder();
				NB.setTEXT(Text);
				NB.setREPLY(MisskeyNote);
				MisskeyBOT.PostNote(NB.Build());
			}
		}
	}
}
