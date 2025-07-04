package su.rumishistem.rumiabot.Joke;

import static su.rumishistem.rumiabot.System.FunctionModuleLoader.AddCommand;
import static su.rumishistem.rumiabot.System.FunctionModuleLoader.AddDiscordContextMenu;
import net.dv8tion.jda.api.interactions.commands.Command.Type;
import net.dv8tion.jda.api.interactions.commands.build.Commands;
import su.rumishistem.rumi_java_lib.LOG_PRINT.LOG_TYPE;
import su.rumishistem.rumiabot.System.TYPE.CommandData;
import su.rumishistem.rumiabot.System.TYPE.CommandInteraction;
import su.rumishistem.rumiabot.System.TYPE.CommandOption;
import su.rumishistem.rumiabot.System.TYPE.CommandOptionType;
import su.rumishistem.rumiabot.System.TYPE.DiscordEvent;
import su.rumishistem.rumiabot.System.TYPE.DiscordFunction;
import su.rumishistem.rumiabot.System.TYPE.FunctionClass;
import su.rumishistem.rumiabot.System.TYPE.ReceiveMessageEvent;
import su.rumishistem.rumiabot.System.TYPE.ReturnInteractionEvent;
import su.rumishistem.rumiabot.System.TYPE.RunInteractionEvent;
import su.rumishistem.rumiabot.System.TYPE.SourceType;
import su.rumishistem.rumiabot.System.TYPE.DiscordEvent.EventType;
import su.rumishistem.rumiabot.System.TYPE.RunInteractionEvent.InteractionType;

public class Main implements FunctionClass {
	private static final String FUNCTION_NAME = "よけ";
	private static final String FUNCTION_VERSION = "1.0";
	private static final String FUNCTION_AUTOR = "Rumisan";

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
		AddCommand(new CommandData("cam", new CommandOption[] {
			new CommandOption("user", CommandOptionType.User, null, true),
			new CommandOption("text", CommandOptionType.String, null, true),
			new CommandOption("file", CommandOptionType.File, null, false)
		}, true));

		AddDiscordContextMenu(Commands.context(Type.MESSAGE, "reply-cam"));
	}

	@Override
	public void ReceiveMessage(ReceiveMessageEvent e) {
		try {
			if (e.GetSource() == SourceType.Discord) {
				//設定の有効化をチェック
				if (!e.GetMessage().CheckDiscordGuildFunctionEnabled(DiscordFunction.Joke)) {
					return;
				}

				//そういうのよくないよ
				SouiunoYokunaiyo.Main(e);
				//きも
				Kimo.Main(e);
				//住所
				Zhuusho.Main(e);
				//うんこ
				Unko.Main(e);
			}
		} catch (Exception EX) {
			EX.printStackTrace();
		}
	}

	@Override
	public boolean GetAllowCommand(String Name) {
		return (
			Name.equals("cam") ||

			Name.equals("Message:reply-cam") ||
			Name.equals("EntitySelect:reply-cam_user") ||
			Name.equals("Modal:reply-cam-modal")
		);
	}

	@Override
	public void RunCommand(CommandInteraction CI) throws Exception {
		if (CI.GetSource() != SourceType.Discord) {
			CI.Reply("Discordのみで使用可能です");
			return;
		}

		cam.Command(CI);
	}

	@Override
	public void RunInteraction(RunInteractionEvent Interaction) throws Exception {
		if (Interaction.getType() == InteractionType.MessageContext) {
			reply_cam.RunContextmenu(Interaction.getMessageContext());
		}
	}

	@Override
	public void ReturnInteraction(ReturnInteractionEvent Interaction) throws Exception {
		if (Interaction.getType() == su.rumishistem.rumiabot.System.TYPE.ReturnInteractionEvent.InteractionType.EntitySelector) {
			reply_cam.ReturnUserSelect(Interaction.getEntitySelect());
		} else if (Interaction.getType() == su.rumishistem.rumiabot.System.TYPE.ReturnInteractionEvent.InteractionType.Modal) {
			reply_cam.ReturnModal(Interaction.getModal());
		}
	}
}
