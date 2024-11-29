package com.rumisystem.rumiabot.MODULE;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import com.rumisystem.rumi_java_lib.Misskey.Builder.NoteBuilder;
import com.rumisystem.rumi_java_lib.Misskey.TYPE.Note;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.interactions.commands.OptionMapping;
import net.dv8tion.jda.api.utils.FileUpload;
import net.dv8tion.jda.api.utils.messages.MessageEditBuilder;
import static com.rumisystem.rumiabot.Main.MisskeyBOT;

public class COMMAND_INTERACTION {
	private String NAME;
	private List<OPTION_ITEM> OPTION_LIST;
	private SlashCommandInteractionEvent DISCORD_INTERACTION = null;
	private Note MISSKEY_NOTE = null;
	private boolean DEFER = false;
	private String ReplyTEXT = "";
	private List<File> ReplyFILE = new ArrayList<File>();

	//オプションの形式
	enum OPTION_TYPE{
		UNKNOWN,
		STRING,
		INT,
		BOOL,
		DISCORD_USER,
		DISCORD_ROLE,
		DISCORD_CANNEL
	}

	//オプションの型
	class OPTION_ITEM{
		private String NAME;
		private Object VALUE;
		private OPTION_TYPE TYPE;
		
		public OPTION_ITEM(String NAME, Object VALUE, OPTION_TYPE TYPE){
			this.NAME = NAME;
			this.VALUE = VALUE;
			this.TYPE = TYPE;
		}

		public String GetNAME() {
			return NAME;
		}

		public Object GetVALUE() {
			return VALUE;
		}

		public OPTION_TYPE GetTYPE() {
			return TYPE;
		}
	}

	public COMMAND_INTERACTION(SlashCommandInteractionEvent INTERACTION) {
		this.NAME = INTERACTION.getName().toString();
		this.DISCORD_INTERACTION = INTERACTION;

		//オプションを全て取得
		for (OptionMapping OPTION:INTERACTION.getOptions()) {
			String NAME = OPTION.getName().toString();
			OPTION_TYPE TYPE;
			Object VALUE;

			switch (OPTION.getType()) {
				case STRING: {
					TYPE = OPTION_TYPE.STRING;
					VALUE = OPTION.getAsString();
				}

				default:{
					TYPE = OPTION_TYPE.UNKNOWN;
					VALUE = null;
				}
			}

			//ばーん
			OPTION_LIST.add(new OPTION_ITEM(NAME, VALUE, TYPE));
		}
	}

	public COMMAND_INTERACTION(Note NOTE) {
		String[] SPLIT = NOTE.getTEXT().replace("@rumiabot ", "").replace("@rumiabot", "").split(" ");
		NAME = SPLIT[0];

		for (int I = 1; I < SPLIT.length; I++) {
			String KEY = SPLIT[I].split("=")[0];
			String VAL = SPLIT[I].split("=")[1];

			if (VAL.matches("\\d+")) {
				//数字
				OPTION_LIST.add(new OPTION_ITEM(KEY, Integer.parseInt(VAL), OPTION_TYPE.INT));
			} else {
				OPTION_LIST.add(new OPTION_ITEM(KEY, VAL, OPTION_TYPE.STRING));
			}
		}

		MISSKEY_NOTE = NOTE;
	}
	
	public String GetNAME() {
		return NAME;
	}

	//でふぁーりぷらい(Discordのみ)
	public void deferReply() {
		if (DISCORD_INTERACTION != null) {
			DISCORD_INTERACTION.deferReply().queue();
			DEFER = true;
		}
	}


	public void SetTEXT(String TEXT) {
		ReplyTEXT = TEXT;
	}

	public void AddFile(File FILE) {
		ReplyFILE.add(FILE);
	}

	public void Reply() {
		try {
			if (DISCORD_INTERACTION != null) {
				//Discord
				if (DEFER) {
					//デファーリプライ後
					MessageEditBuilder MEB = new MessageEditBuilder();

					//内容
					MEB.setContent(ReplyTEXT);

					if (ReplyFILE.size() == 0) {
						DISCORD_INTERACTION.getHook().editOriginal(MEB.build()).queue();;
					} else {
						DISCORD_INTERACTION.getHook().editOriginal(MEB.build()).setFiles(FileUpload.fromData(ReplyFILE.get(0))).queue();
					}
				} else {
				}
			} else {
				//Misskey
				NoteBuilder NB = new NoteBuilder();
				NB.setREPLY(MISSKEY_NOTE);
				NB.setTEXT(ReplyTEXT);

				MisskeyBOT.PostNote(NB.Build());
			}
		} catch (Exception EX) {
			EX.printStackTrace();
		}
	}
}
