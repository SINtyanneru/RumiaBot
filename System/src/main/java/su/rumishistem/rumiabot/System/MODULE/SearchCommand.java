package su.rumishistem.rumiabot.System.MODULE;

import static su.rumishistem.rumiabot.System.Main.FunctionModuleList;
import static su.rumishistem.rumiabot.System.Main.CommandList;
import su.rumishistem.rumiabot.System.TYPE.CommandData;
import su.rumishistem.rumiabot.System.TYPE.FunctionClass;

public class SearchCommand {
	public static CommandData Command(String Name) {
		for (CommandData ROW:CommandList) {
			if (ROW.GetName().equals(Name)) {
				return ROW;
			}
		}

		return null;
	}

	public static FunctionClass Function(String Name) {
		for (FunctionClass ROW:FunctionModuleList) {
			if (ROW.GetAllowCommand(Name)) {
				return ROW;
			}
		}

		return null;
	}
}
