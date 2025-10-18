package su.rumishistem.rumiabot.VerifyPanelFunction;

import java.util.HashMap;

import net.dv8tion.jda.api.events.interaction.component.ButtonInteractionEvent;
import net.dv8tion.jda.api.interactions.components.buttons.ButtonInteraction;
import su.rumishistem.rumiabot.System.CommandRegister;
import su.rumishistem.rumiabot.System.Type.CommandInteraction;
import su.rumishistem.rumiabot.System.Type.CommandOptionRegist;
import su.rumishistem.rumiabot.System.Type.FunctionClass;
import su.rumishistem.rumiabot.System.Type.OptionType;
import su.rumishistem.rumiabot.System.Type.RunCommand;
import su.rumishistem.rumiabot.System.Type.SourceType;

public class Main implements FunctionClass {
	@Override
	public String function_name() {
		return "認証パネル";
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
		CommandRegister.add_command("verify_panel", new CommandOptionRegist[] {
			new CommandOptionRegist("role", OptionType.DiscordRole, true)
		}, false, new RunCommand() {
			@Override
			public void run(CommandInteraction e) throws Exception {
				if (e.get_source() != SourceType.Discord) {
					e.reply("Discordでのみ使用可能です");
					return;
				}

				CreatePanel.Create(e.get_discprd_event());
			}
		});

		PanelSystem.HTTPEP();
	}

	@Override
	public void discord_button_event(String id, HashMap<String, String> param, ButtonInteractionEvent e) {
		if (!id.equals("verify_panel")) return;

		PanelSystem.ButtonFunction(e);
	}
}
