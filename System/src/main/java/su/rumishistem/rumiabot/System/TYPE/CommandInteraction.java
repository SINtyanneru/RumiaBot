package su.rumishistem.rumiabot.System.TYPE;

import static su.rumishistem.rumiabot.System.Main.MisskeyBOT;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.utils.FileUpload;
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
	private List<File> FileList = new ArrayList<File>();

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

	public void AddFile(File F) {
		if (FileList.size() < 10) {
			FileList.add(F);
		} else {
			throw new Error("添付しようとしているファイルが多すぎます殺すぞ");
		}
	}

	public void Reply(String Text) throws IOException {
		//ファイルをアップロード
		List<FileUpload> DiscordUploadList = new ArrayList<FileUpload>();
		if (Source == SourceType.Discord) {
			for (File F:FileList) {
				DiscordUploadList.add(FileUpload.fromData(F));
			}
		}

		if (Command.isPrivate()) {
			//周りに見えないやつ
			if (Source == SourceType.Discord) {
				DiscordInteraction.reply(Text).setFiles(DiscordUploadList).queue();
			} else if (Source == SourceType.Misskey) {
				NoteBuilder NB = new NoteBuilder();
				NB.setTEXT(Text);
				NB.setREPLY(MisskeyNote);
				for (File F:FileList) {
					NB.AddFile(F);
				}
				MisskeyBOT.PostNote(NB.Build());
			}
		} else {
			//DeferRelyした後のやつ
			if (Source == SourceType.Discord) {
				DiscordInteraction.getHook().editOriginal(Text).setAttachments(DiscordUploadList).queue();
			} else if (Source == SourceType.Misskey) {
				NoteBuilder NB = new NoteBuilder();
				NB.setTEXT(Text);
				NB.setREPLY(MisskeyNote);
				for (File F:FileList) {
					NB.AddFile(F);
				}
				MisskeyBOT.PostNote(NB.Build());
			}
		}
	}
}
