package su.rumishistem.rumiabot.System.TYPE;

import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;

/**
 * コマンドを実行したときのやつ、インタラクション？
 */
public class CommandInteraction {
	private SourceType Source;
	private SlashCommandInteractionEvent DiscordInteraction;
	private CommandData Command;

	public CommandInteraction(SourceType Source, Object Interaction, CommandData Command) {
		this.Source = Source;
		this.Command = Command;

		if (Interaction instanceof SlashCommandInteractionEvent) {
			this.DiscordInteraction = (SlashCommandInteractionEvent) Interaction;
		} else {
			throw new Error("Interaction erer");
		}
	}

	public void Reply(String Text) {
		if (Command.isPrivate()) {
			//周りに見えないやつ
			if (Source == SourceType.Discord) {
				DiscordInteraction.reply(Text).queue();
			}
		} else {
			//DeferRelyした後のやつ
			if (Source == SourceType.Discord) {
				DiscordInteraction.getHook().editOriginal(Text).queue();
			}
		}
	}
}
