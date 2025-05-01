package su.rumishistem.rumiabot.VerifyPanelFunction;

import net.dv8tion.jda.api.events.interaction.component.ButtonInteractionEvent;
import static su.rumishistem.rumiabot.System.FunctionModuleLoader.AddCommand;
import su.rumishistem.rumiabot.System.TYPE.CommandData;
import su.rumishistem.rumiabot.System.TYPE.CommandInteraction;
import su.rumishistem.rumiabot.System.TYPE.CommandOption;
import su.rumishistem.rumiabot.System.TYPE.CommandOptionType;
import su.rumishistem.rumiabot.System.TYPE.FunctionClass;
import su.rumishistem.rumiabot.System.TYPE.ReceiveMessageEvent;
import su.rumishistem.rumiabot.System.TYPE.SourceType;

public class Main implements FunctionClass {
	private static final String FUNCTION_NAME = "認証パネル";
	private static final String FUNCTION_VERSION = "1.0";
	private static final String FUNCTION_AUTOR = "Rumisan";

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
		AddCommand(new CommandData("veryfy_panel", new CommandOption[] {
			new CommandOption("role", CommandOptionType.Role, null, true)
		}, false));
		PanelSystem.HTTPEP();
	}

	@Override
	public void ReceiveMessage(ReceiveMessageEvent e) {
	}

	@Override
	public boolean GetAllowCommand(String Name) {
		return (Name.equals("verify_panel") || Name.equals("Button:verify_panel"));
	}

	@Override
	public void RunCommand(CommandInteraction CI) throws Exception {
		if (CI.GetSource() == SourceType.Discord) {
			CreatePanel.Create(CI.GetDiscordInteraction());
		} else {
			CI.Reply("Discordでのみ使用できます!");
		}
	}

	@Override
	public void RunButton(ButtonInteractionEvent BI) {
		PanelSystem.ButtonFunction(BI);
	}
}
