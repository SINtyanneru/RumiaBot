package su.rumishistem.rumiabot.MisskeyReportFucker;

import static su.rumishistem.rumiabot.System.Main.*;
import static su.rumishistem.rumi_java_lib.LOG_PRINT.Main.LOG;

import java.util.concurrent.*;
import java.util.regex.*;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import su.rumishistem.rumi_java_lib.LOG_PRINT.LOG_TYPE;
import su.rumishistem.rumi_java_lib.MisskeyBot.Builder.NoteBuilder;
import su.rumishistem.rumiabot.MisskeyReportFucker.TypeDetect.Software;
import su.rumishistem.rumiabot.System.Type.FunctionClass;

public class Main implements FunctionClass {
	private static final String AdminToken = config.get("MISSKEY").getData("ADMINTOKEN").asString();

	@Override
	public String function_name() {
		return "Misskey通報Fucker";
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
		ScheduledExecutorService SES = Executors.newScheduledThreadPool(1);
		Runnable Task = new Runnable() {
			@Override
			public void run() {
				try {
					JsonNode B = ReportGet.Get(AdminToken);
					for (int I = 0; I < B.size(); I++) {
						JsonNode R = B.get(I);
						String Text = GenNoteText(R);

						//ノート
						NoteBuilder nb = new NoteBuilder();
						nb.set_text(Text);
						su.rumishistem.rumiabot.System.Main.get_misskey_bot().get_client().create_note(nb);
					}

					LOG(LOG_TYPE.OK, "通報をチェックしました");
				} catch (Exception EX) {
					EX.printStackTrace();
					LOG(LOG_TYPE.FAILED, "通報をチェックしました");
				}
			}
		};

		SES.scheduleAtFixedRate(Task, 0, 5, TimeUnit.MINUTES);
	}

	private static String GetHost(JsonNode Report) {
		if (Report.get("reporter").get("host").isNull()) {
			return config.get("MISSKEY").getData("DOMAIN").asString();
		} else {
			return Report.get("reporter").get("host").asText();
		}
	}

	private static String GenNoteText(JsonNode R) {
		String Content = R.get("comment").asText();
		String Host = GetHost(R);
		Software ReportSoftware = TypeDetect.Detect(Content);

		StringBuilder SB = new StringBuilder();
		SB.append("ピピッ！通報を受信しました！\n");
		SB.append("送信元：`" + Sanitize(Host) + "`\n");

		if (ReportSoftware == Software.Misskey) {
			//Misskeyからの通報
			Matcher MTC = Pattern.compile("notes\\/(.*)\n").matcher(Content);
			MTC.find();
			SB.append("ノート：`" + Sanitize(MTC.group(1)) + "`\n");
			SB.append("```\n" + Content.replaceAll("[\\s\\S]*\n-*\n", "").replaceAll("\\[[\\s\\S]*\\]", "") + "```\n");
		} else if (ReportSoftware == Software.Mastodon) {
			//Mastodonからの通報
			try {
				Matcher MTC = Pattern.compile("([\\s\\S]*)(\\[[\\s\\S]*\\])").matcher(Content);
				MTC.find();

				//添付情報
				JsonNode ReportJson = new ObjectMapper().readTree(MTC.group(2));
				for (int J = 0; J < ReportJson.size(); J++) {
					String ROW = Sanitize(ReportJson.get(J).asText());
					if (ROW.startsWith("users/")) {
						SB.append("ユーザー：`" + ROW + "`\n");
					} else if (ROW.startsWith("notes/")) {
						SB.append("ノート：`" + ROW + "`\n");
					} else {
						SB.append("備考：`" + ROW + "`\n");
					}
				}

				//通報の理由
				SB.append("```\n" + Sanitize(MTC.group(1)) + "\n```\n");
			} catch (Exception EX) {
				//JSONエラーだと思うのでもみ消す
				SB.append("```\n" + Sanitize(Content) + "\n```\n");
			}
		} else {
			//不明なソフトウェア
			SB.append("```\n" + Sanitize(Content) + "\n```\n");
		}

		SB.append("@rumisan@fedi.rumi-room.net");

		return SB.toString();
	}

	private static String Sanitize(String Text) {
		String[] FuckingLetter = new String[] {
			"`", "@", "'", "\"", "\\", "$"
		};

		for (String S:FuckingLetter) {
			Text = Text.replace(S, "F");
		}

		return Text;
	}
}
