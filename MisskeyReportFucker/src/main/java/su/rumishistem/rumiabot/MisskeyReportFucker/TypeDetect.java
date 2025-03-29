package su.rumishistem.rumiabot.MisskeyReportFucker;

public class TypeDetect {
	public enum Software {
		None,
		Misskey,
		Mastodon
	}

	public static Software Detect(String Content) {
		if (Content.contains("Note:") && Content.contains("Local Note:")) {
			return Software.Misskey;
		} else if (Content.contains("[") && Content.contains("]")) {
			return Software.Mastodon;
		} else {
			return Software.None;
		}
	}
}
