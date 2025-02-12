package su.rumishistem.rumiabot.TestFunction;

import su.rumishistem.rumiabot.System.TYPE.CommandData;
import su.rumishistem.rumiabot.System.TYPE.CommandInteraction;
import su.rumishistem.rumiabot.System.TYPE.CommandOption;
import su.rumishistem.rumiabot.System.TYPE.FunctionClass;
import su.rumishistem.rumiabot.System.TYPE.ReceiveMessageEvent;
import su.rumishistem.rumiabot.System.TYPE.SourceType;

import static su.rumishistem.rumiabot.System.FunctionModuleLoader.AddCommand;

import java.io.IOException;

public class Main implements FunctionClass {
	private static final String FUNCTION_NAME = "Test";
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
		AddCommand(new CommandData("test", new CommandOption[] {}, false));
	}

	@Override
	public void ReceiveMessage(ReceiveMessageEvent e) {
		if (e.GetMessage().isKaiMention()) {
			if (e.GetMessage().GetText().contains("test")) {
				e.GetMessage().Reply("f**k");
			}
		}
	}

	@Override
	public boolean GetAllowCommand(String Name) {
		return Name.equals("test");
	}

	@Override
	public void RunCommand(CommandInteraction Interaction) throws IOException {
		Interaction.Reply("はい");
	}
}
