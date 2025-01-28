package su.rumishistem.rumiabot.MODULE.ISHITEGAWA;

import static com.rumisystem.rumi_java_lib.LOG_PRINT.Main.LOG;
import static su.rumishistem.rumiabot.Main.MisskeyBOT;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.time.format.DateTimeFormatter;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.rumisystem.rumi_java_lib.FILER;
import com.rumisystem.rumi_java_lib.LOG_PRINT.LOG_TYPE;
import com.rumisystem.rumi_java_lib.Misskey.Builder.NoteBuilder;

import su.rumishistem.rumiabot.MODULE.DATE_FORMAT;
import su.rumishistem.rumiabot.Misskey.FUNCTION.DAM_NOTE;

public class ISHITEGAWA_DAM {
	private static String DAM_ID = "1368080150020";
	private static String BaseURL = "http://www1.river.go.jp";
	private static String CACHE_PATH = "/tmp/dam.txt";
	private static boolean SHOKAI = false;
	public static DAM_STATUS STATUS;

	public static void Main() {
		ScheduledExecutorService SCHE = Executors.newScheduledThreadPool(1);
		Runnable TASK = new Runnable() {
			@Override
			public void run() {
				try {
					//起動時に実行されるのキショイから対策
					if (SHOKAI) {
						//石手川ダムを取得
						getDATA();
						LOG(LOG_TYPE.OK, "石手川を取得しました");

						//Misskeyに投稿する
						DAM_NOTE.Main();
					} else {
						//次はジッコするうようにする
						SHOKAI = true;

						//ダム情報をキャッシュから引っ張ってくる
						File CACHE_FILE = new File(CACHE_PATH);
						if (CACHE_FILE.exists()) {
							STATUS = parseDATA(new FILER(CACHE_FILE).OPEN_STRING());
							LOG(LOG_TYPE.OK, "キャッシュから石手川を取得しました");
						} else {
							getDATA();
							LOG(LOG_TYPE.OK, "キャッシュが無いので、石手川を取得しキャッシュに保存しました");
						}
					}
				} catch (Exception EX) {
					EX.printStackTrace();
				}
			}
		};
		SCHE.scheduleAtFixedRate(TASK, 0, 30, TimeUnit.MINUTES);
	}

	public static String genTEXT() {
		StringBuilder SB = new StringBuilder();
		SB.append("和暦" + DATE_FORMAT.KOUKI(STATUS.getDATE().atOffset(ZoneOffset.ofHours(9))) + "\n");
		SB.append("西暦" + DATE_FORMAT.ZHUUNI_H(STATUS.getDATE().atOffset(ZoneOffset.ofHours(9))) + "\n");
		SB.append("貯水率は" + STATUS.getPOSOS() + "です、\n");
		SB.append("流入量は" + STATUS.getIN() + "㌧、放流量は" + STATUS.getOUT() + "㌧です。\n");

		return SB.toString();
	}

	private static void getDATA() throws IOException, InterruptedException {
		String AJAX = HTTPReq(BaseURL + "/cgi-bin/DspDamData.exe?ID=" + DAM_ID + "&KIND=3&PAGE=1");
		String DataURL = "";

		Pattern PAT = Pattern.compile("<A href=\"(.*?\\.dat)\"");
		Matcher MAT = PAT.matcher(AJAX);
		while (MAT.find()) {
			DataURL = BaseURL + MAT.group(1);
			break;
		}

		//マナー的に1秒待つ
		Thread.sleep(1000);

		//データを取得
		String[] DATA = HTTPReq(DataURL).split("\n");
		String LINE = DATA[DATA.length - 1];


		STATUS = parseDATA(LINE);

		//ファイルにキャッシュとして保存
		Files.writeString(Paths.get(CACHE_PATH), LINE);
	}

	private static DAM_STATUS parseDATA(String LINE) {
		String[] SPLIT = LINE.split(",");

		return new DAM_STATUS(
			LocalDateTime.parse(SPLIT[0] + " " + SPLIT[1], DateTimeFormatter.ofPattern("yyyy/MM/dd HH:mm")),
			Float.parseFloat(SPLIT[10]),
			Float.parseFloat(SPLIT[6]),
			Float.parseFloat(SPLIT[8])
		);
	}

	private static String HTTPReq(String URI) throws MalformedURLException, IOException {
		HttpURLConnection HUC = (HttpURLConnection) new URL(URI).openConnection();

		//GETリクエストだと主張する
		HUC.setRequestMethod("GET");

		//レスポンスコード
		int RES_CODE = HUC.getResponseCode();

		BufferedReader BR = new BufferedReader(new InputStreamReader(HUC.getInputStream(), Charset.forName("SHIFT_JIS")));
		StringBuilder RES_STRING = new StringBuilder();

		String INPUT_LINE;
		while ((INPUT_LINE = BR.readLine()) != null){
			RES_STRING.append(INPUT_LINE + "\n");
		}

		BR.close();
		return RES_STRING.toString();
	}
}