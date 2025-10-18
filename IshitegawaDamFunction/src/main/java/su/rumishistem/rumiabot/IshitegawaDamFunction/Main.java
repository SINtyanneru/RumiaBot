package su.rumishistem.rumiabot.IshitegawaDamFunction;

import static su.rumishistem.rumiabot.IshitegawaDamFunction.DAMDAM.DamSchedule;

import java.io.IOException;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import su.rumishistem.rumiabot.System.CommandRegister;
import su.rumishistem.rumiabot.System.Type.CommandInteraction;
import su.rumishistem.rumiabot.System.Type.CommandOptionRegist;
import su.rumishistem.rumiabot.System.Type.FunctionClass;
import su.rumishistem.rumiabot.System.Type.RunCommand;

public class Main implements FunctionClass {
	@Override
	public String function_name() {
		return "石手川ダム";
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
		CommandRegister.add_command("dam", new CommandOptionRegist[] {}, false, new RunCommand() {
			@Override
			public void run(CommandInteraction e) throws Exception {
				e.reply(DAMDAM.genTEXT());
			}
		});

		ScheduledExecutorService SCHE = Executors.newScheduledThreadPool(1);
		Runnable TASK = new Runnable() {
			@Override
			public void run() {
				DamSchedule();
			}
		};
		SCHE.scheduleAtFixedRate(TASK, 0, 30, TimeUnit.MINUTES);
	}
}
