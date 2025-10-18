package su.rumishistem.rumiabot.System.Type;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.utils.FileUpload;
import su.rumishistem.rumi_java_lib.MisskeyBot.Builder.NoteBuilder;
import su.rumishistem.rumi_java_lib.MisskeyBot.Type.Note;
import su.rumishistem.rumiabot.System.Main;

public class CommandInteraction {
	private SourceType source;
	private HashMap<String, Object> option;
	private boolean is_private = false;

	private Note misskey_note;
	private SlashCommandInteractionEvent discord_event;

	private List<File> file_list = new ArrayList<File>();

	public CommandInteraction(Note note, HashMap<String, Object> option, boolean is_private) {
		source = SourceType.Misskey;
		this.misskey_note = note;
		this.option = option;
		this.is_private = is_private;
	}

	public CommandInteraction(SlashCommandInteractionEvent e, HashMap<String, Object> option, boolean is_private) {
		source = SourceType.Discord;
		this.discord_event = e;
		this.option = option;
		this.is_private = is_private;
	}

	public SourceType get_source() {
		return source;
	}

	public SlashCommandInteractionEvent get_discprd_event() {
		return discord_event;
	}

	public Note get_misskey_event() {
		return misskey_note;
	}

	public void add_file(File f) {
		file_list.add(f);
	}

	public void reply(String text) {
		if (source == SourceType.Misskey) {
			NoteBuilder nb = new NoteBuilder();
			nb.set_reply(misskey_note);
			nb.set_text(text);

			//ファイル
			for (File file:file_list) {
				nb.add_file(file);
			}

			Main.get_misskey_bot().get_client().create_note(nb);
		} else if (source == SourceType.Discord) {
			//ファイル
			List<FileUpload> upload_list = new ArrayList<>();
			for (File file:file_list) {
				upload_list.add(FileUpload.fromData(file));
			}

			if (is_private) {
				discord_event.reply(text).setFiles(upload_list).setEphemeral(true).queue();
			} else {
				discord_event.getHook().editOriginal(text).setAttachments(upload_list).queue();
			}
		}
	}

	public Object get_option(String name) {
		return option.get(name.toUpperCase());
	}

	public String get_option_as_string(String name) {
		return (String) option.get(name.toUpperCase());
	}
}
