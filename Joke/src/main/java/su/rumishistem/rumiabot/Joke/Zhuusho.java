package su.rumishistem.rumiabot.Joke;

import java.util.Base64;

import su.rumishistem.rumiabot.System.TYPE.ReceiveMessageEvent;

public class Zhuusho {
	public static void Main(ReceiveMessageEvent e) {
		String Content = e.GetMessage().GetText();

		if (Content.equals("ぐるぐる住所")) {
			String NENE_BASE64 = "44CSMTMzLTAwNTEg5p2x5Lqs6YO95rGf5oi45bed5Yy65YyX5bCP5bKpMeS4geebrjE04oiSNSDpg73llrbljJflsI/lsqnkuIDkuIHnm67jgqLjg5Hjg7zjg4jvvJXlj7fmo58g6YOo5bGL55Wq5Y+3MjA4Cg==";
			String NENE_ZHUUSHO = new String(Base64.getDecoder().decode(NENE_BASE64));
			e.GetMessage().Reply(NENE_BASE64);
		}
	}
}
