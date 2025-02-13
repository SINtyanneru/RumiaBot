package su.rumishistem.rumiabot.System.Discord;

import static su.rumishistem.rumiabot.System.Main.FunctionModuleList;

import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import su.rumishistem.rumi_java_lib.EXCEPTION_READER;
import su.rumishistem.rumiabot.System.MODULE.SearchCommand;
import su.rumishistem.rumiabot.System.MODULE.UserBlockCheck;
import su.rumishistem.rumiabot.System.TYPE.CommandData;
import su.rumishistem.rumiabot.System.TYPE.CommandInteraction;
import su.rumishistem.rumiabot.System.TYPE.FunctionClass;
import su.rumishistem.rumiabot.System.TYPE.MessageData;
import su.rumishistem.rumiabot.System.TYPE.MessageUser;
import su.rumishistem.rumiabot.System.TYPE.ReceiveMessageEvent;
import su.rumishistem.rumiabot.System.TYPE.SourceType;

public class DiscordEventListener extends ListenerAdapter {
	@Override
	public void onMessageReceived(MessageReceivedEvent E) {
		//ブロック済みのユーザーなら此処で処理を中断する
		if (!UserBlockCheck.isBlock(E.getAuthor().getId())) {
			//イベント着火
			for (FunctionClass Function:FunctionModuleList) {
				Function.ReceiveMessage(new ReceiveMessageEvent(
					SourceType.Discord,
					new MessageUser(),
					new MessageData(
						E.getMessageId(),
						E.getMessage().getContentRaw(),
						E.getMessage()
					)
				));
			}
		}
	}

	@Override
	public void onSlashCommandInteraction(SlashCommandInteractionEvent INTERACTION){
		try {
			//ブロック済みのユーザーなら此処で処理を中断する
			if (!UserBlockCheck.isBlock(INTERACTION.getUser().getId())) {
				CommandData Command = SearchCommand.Command(INTERACTION.getName());
				FunctionClass Function = SearchCommand.Function(INTERACTION.getName());
				if (Command != null && Function != null) {
					if (!Command.isPrivate()) {
						INTERACTION.deferReply().queue();
					}

					Function.RunCommand(new CommandInteraction(SourceType.Discord, INTERACTION, Command));
				} else {
					INTERACTION.reply("コマンドか機能が見つかりませんでした").queue();
				}
			} else {
				INTERACTION.reply("帰れ").queue();
			}
		} catch (Exception EX) {
			String EX_TEXT = EXCEPTION_READER.READ(EX);
			INTERACTION.getHook().editOriginal("エラー\n```\n" + EX_TEXT + "\n```").queue();
			EX.printStackTrace();
		}
	}
}
