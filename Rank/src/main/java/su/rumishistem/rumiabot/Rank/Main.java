package su.rumishistem.rumiabot.Rank;

import su.rumishistem.rumiabot.System.TYPE.CommandInteraction;
import su.rumishistem.rumiabot.System.TYPE.FunctionClass;
import su.rumishistem.rumiabot.System.TYPE.ReceiveMessageEvent;
import su.rumishistem.rumiabot.System.TYPE.SourceType;

public class Main implements FunctionClass{
	private static final String FUNCTION_NAME = "ランキング";
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
		
	}

	@Override
	public void ReceiveMessage(ReceiveMessageEvent e) {
		if (e.GetSource() == SourceType.Discord) {
			String user_id = e.GetUser().GetID();
			
		}
	}

	@Override
	public boolean GetAllowCommand(String Name) {
		return false;
	}

	@Override
	public void RunCommand(CommandInteraction CI) throws Exception {
		
	}
}
