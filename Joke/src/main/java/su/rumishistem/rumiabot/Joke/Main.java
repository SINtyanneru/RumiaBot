package su.rumishistem.rumiabot.Joke;

import java.util.Arrays;
import java.util.Base64;
import java.util.Random;

import su.rumishistem.rumiabot.System.TYPE.CommandInteraction;
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
		String Content = e.GetMessage().GetText();

		if (e.GetSource() == SourceType.Discord) {
			if (Content.equals("ち") || Content.equals("ちん") || Content.equals("ま") || Content.equals("まん")) {
				if (new Random().nextInt(1000) == 0) {
					e.GetMessage().Reply("そういうのめっちゃ良い！");
				} else {
					e.GetMessage().Reply("そういうのよくないと思うよ。");
				}
			}

			String[] NGWordList = new String[] {
				"まんこ"
			};

			if (Arrays.asList(NGWordList).contains(Content)) {
				e.GetMessage().Reply("キモ...");
			}

			if (Content.equals("ぐるぐる住所")) {
				String NENE_BASE64 = "44CSMTMzLTAwNTEg5p2x5Lqs6YO95rGf5oi45bed5Yy65YyX5bCP5bKpMeS4geebrjE04oiSNSDpg73llrbljJflsI/lsqnkuIDkuIHnm67jgqLjg5Hjg7zjg4jvvJXlj7fmo58g6YOo5bGL55Wq5Y+3MjA4Cg==";
				String NENE_ZHUUSHO = new String(Base64.getDecoder().decode(NENE_BASE64));
				e.GetMessage().Reply(NENE_BASE64);
			}
		}
	}

	@Override
	public boolean GetAllowCommand(String Name) {
		return false;
	}

	@Override
	public void RunCommand(CommandInteraction CI) throws Exception {}
}
