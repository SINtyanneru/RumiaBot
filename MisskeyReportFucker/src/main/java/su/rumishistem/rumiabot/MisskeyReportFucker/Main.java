package su.rumishistem.rumiabot.MisskeyReportFucker;

import static su.rumishistem.rumiabot.System.Main.CONFIG_DATA;
import static su.rumishistem.rumiabot.System.Main.MisskeyBOT;
import static su.rumishistem.rumi_java_lib.LOG_PRINT.Main.LOG;

import java.net.MalformedURLException;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

import su.rumishistem.rumi_java_lib.FETCH;
import su.rumishistem.rumi_java_lib.FETCH_RESULT;
import su.rumishistem.rumi_java_lib.LOG_PRINT.LOG_TYPE;
import su.rumishistem.rumi_java_lib.Misskey.Builder.NoteBuilder;
import su.rumishistem.rumi_java_lib.Misskey.TYPE.NoteVis;
import su.rumishistem.rumiabot.MisskeyReportFucker.TypeDetect.Software;
import su.rumishistem.rumiabot.System.TYPE.CommandInteraction;
import su.rumishistem.rumiabot.System.TYPE.FunctionClass;
import su.rumishistem.rumiabot.System.TYPE.ReceiveMessageEvent;

public class Main implements FunctionClass {
	private static final String FUNCTION_NAME = "Misskey通報Fucker";
	private static final String FUNCTION_VERSION = "1.0";
	private static final String FUNCTION_AUTOR = "Rumisan";
	private static final String AdminToken = CONFIG_DATA.get("MISSKEY").getData("ADMINTOKEN").asString();

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
	public void ReceiveMessage(ReceiveMessageEvent e) {}
	@Override
	public boolean GetAllowCommand(String Name) {	return false;}
	@Override
	public void RunCommand(CommandInteraction CI) throws Exception {}

	@Override
	public void Init() {
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
						NoteBuilder NB = new NoteBuilder();
						NB.setTEXT(Text);
						NB.setVIS(NoteVis.PUBLIC);
						MisskeyBOT.PostNote(NB.Build());

						//リモートの通報は破棄する
						if (!R.get("reporter").get("host").isNull()) {
							ReportHaki(R.get("id").asText());
						}
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
			return CONFIG_DATA.get("MISSKEY").getData("DOMAIN").asString();
		} else {
			return Report.get("reporter").get("host").asText();
		}
	}

	private static String Sanitize(String Text) {
		return Text.replace("`", "'");
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

		//ローカルなら消さない
		if (Host.equals(CONFIG_DATA.get("MISSKEY").getData("DOMAIN").asString())) {
			SB.append("ローカルからの通報のため、通報は破棄しません！\n");
			SB.append("@Rumisan");
		} else {
			SB.append("リモートからの通報のため、破棄しました！");
		}

		return SB.toString();
	}
	
	private static void ReportHaki(String ID) throws MalformedURLException {
		FETCH AJAX = new FETCH("https://" + CONFIG_DATA.get("MISSKEY").getData("DOMAIN").asString() + "/api/admin/resolve-abuse-user-report");
		AJAX.SetHEADER("Content-Type", "application/json; charset=UTF-8");
		AJAX.POST(("{\"reportId\":\"" + ID + "\",\"resolvedAs\":\"reject\",\"i\":\"" + AdminToken + "\"}").getBytes());
	}
}
