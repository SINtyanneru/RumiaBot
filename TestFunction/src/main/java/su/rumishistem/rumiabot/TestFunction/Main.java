package su.rumishistem.rumiabot.TestFunction;

import java.io.IOException;

import su.rumishistem.rumiabot.System.CommandRegister;
import su.rumishistem.rumiabot.System.FunctionLoader;
import su.rumishistem.rumiabot.System.Type.CommandInteraction;
import su.rumishistem.rumiabot.System.Type.CommandOptionRegist;
import su.rumishistem.rumiabot.System.Type.FunctionClass;
import su.rumishistem.rumiabot.System.Type.RunCommand;

public class Main implements FunctionClass {
	@Override
	public String function_name() {
		return "Test";
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
		CommandRegister.add_command("test", new CommandOptionRegist[] {}, false, new RunCommand() {
			@Override
			public void run(CommandInteraction e) {
				StringBuilder SB = new StringBuilder();
				SB.append("るみさんBOT\n");
				SB.append("ビルド時刻：" + su.rumishistem.rumiabot.System.Main.get_build_date() + "\n");
				SB.append("インストール済みモジュール↓\n");
				SB.append("```\n");

				for (FunctionClass Func:FunctionLoader.get_list()) {
					SB.append("┌[" + Func.function_name() + "]\n");
					SB.append("├V" + Func.function_version() + "\n");
					SB.append("└" + Func.function_version() + "\n");
				}

				SB.append("```\n");

				e.reply(SB.toString());
			}
		});
	}
}
