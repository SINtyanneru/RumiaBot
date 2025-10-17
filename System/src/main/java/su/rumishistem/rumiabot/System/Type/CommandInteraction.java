package su.rumishistem.rumiabot.System.Type;

import java.util.HashMap;

import su.rumishistem.rumi_java_lib.MisskeyBot.Builder.NoteBuilder;
import su.rumishistem.rumi_java_lib.MisskeyBot.Type.Note;
import su.rumishistem.rumiabot.System.Main;

public class CommandInteraction {
	private SourceType source;
	private HashMap<String, Object> option;
	private Note misskey_note;

	public CommandInteraction(Note note, HashMap<String, Object> option) {
		source = SourceType.Misskey;
		this.misskey_note = note;
		this.option = option;
	}

	public SourceType get_type() {
		return source;
	}

	public void reply(String text) {
		if (source == SourceType.Misskey) {
			NoteBuilder nb = new NoteBuilder();
			nb.set_reply(misskey_note);
			nb.set_text(text);
			Main.get_misskey_bot().get_client().create_note(nb);
		}
	}

	public String get_option_as_string(String name) {
		return (String) option.get(name);
	}
}
