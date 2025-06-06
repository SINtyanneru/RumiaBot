package su.rumishistem.rumiabot.Joke;

import static su.rumishistem.rumiabot.System.FunctionModuleLoader.AddCommand;
import su.rumishistem.rumiabot.System.TYPE.CommandData;
import su.rumishistem.rumiabot.System.TYPE.CommandInteraction;
import su.rumishistem.rumiabot.System.TYPE.CommandOption;
import su.rumishistem.rumiabot.System.TYPE.CommandOptionType;
import su.rumishistem.rumiabot.System.TYPE.DiscordFunction;
import su.rumishistem.rumiabot.System.TYPE.FunctionClass;
import su.rumishistem.rumiabot.System.TYPE.ReceiveMessageEvent;
import su.rumishistem.rumiabot.System.TYPE.SourceType;

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
	}

	@Override
	public void ReceiveMessage(ReceiveMessageEvent e) {
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
	}

	@Override
	public boolean GetAllowCommand(String Name) {
		return Name.equals("cam");
	}

	@Override
	public void RunCommand(CommandInteraction CI) throws Exception {
		if (CI.GetSource() != SourceType.Discord) {
			CI.Reply("Discordのみで使用可能です");
			return;
		}

		cam.Command(CI);
	}
}
