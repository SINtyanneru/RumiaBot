package su.rumishistem.rumiabot.Joke;

import su.rumishistem.rumiabot.System.TYPE.CommandInteraction;
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
	public void Init() {}

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
		return false;
	}

	@Override
	public void RunCommand(CommandInteraction CI) throws Exception {}
}
