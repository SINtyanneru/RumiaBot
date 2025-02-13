package su.rumishistem.rumiabot.IshitegawaDamFunction;

import static su.rumishistem.rumiabot.System.FunctionModuleLoader.AddCommand;
import static su.rumishistem.rumiabot.IshitegawaDamFunction.DAMDAM.DamSchedule;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import su.rumishistem.rumiabot.System.TYPE.CommandData;
import su.rumishistem.rumiabot.System.TYPE.CommandInteraction;
import su.rumishistem.rumiabot.System.TYPE.CommandOption;
import su.rumishistem.rumiabot.System.TYPE.FunctionClass;
import su.rumishistem.rumiabot.System.TYPE.ReceiveMessageEvent;

public class Main implements FunctionClass {
	private static final String FUNCTION_NAME = "石手川ダム";
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
		AddCommand(new CommandData("dam", new CommandOption[] {}, false));

		ScheduledExecutorService SCHE = Executors.newScheduledThreadPool(1);
		Runnable TASK = new Runnable() {
			@Override
			public void run() {
				DamSchedule();
			}
		};
		SCHE.scheduleAtFixedRate(TASK, 0, 30, TimeUnit.MINUTES);
	}

	@Override
	public void ReceiveMessage(ReceiveMessageEvent e) {
	}

	@Override
	public boolean GetAllowCommand(String Name) {
		return Name.equals("dam");
	}

	@Override
	public void RunCommand(CommandInteraction CI) {
		CI.Reply(DAMDAM.genTEXT());
	}

}
