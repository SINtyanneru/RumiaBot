package su.rumishistem.rumisanbot.base_system.Software.RumiaBot;

import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.http.*;
import java.net.http.HttpResponse.BodyHandlers;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;
import java.util.Optional;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.regex.*;

import su.rumishistem.rumisanbot.base_system.Command;
import su.rumishistem.rumisanbot.base_system.Type.NotePublicSetting;

public class Ishitegawa {
	private static final String DAM_ID = "1368080150020";
	private static final String BASE_URL = "http://www1.river.go.jp";
	private static final int[] UPDATE_MIN = { 0, 30 };

	private static HttpClient ajax = HttpClient.newHttpClient();
	private static ScheduledExecutorService scheduler = Executors.newSingleThreadScheduledExecutor();

	private static LocalDateTime 時刻 = LocalDateTime.of(-659, 2, 11, 0, 0);
	private static double 流域平均雨量 = -1;
	private static int 貯水量 = -1;
	private static double 流入量 = -1;
	private static double 放流量 = -1;
	private static double 貯水率 = -1;

	public static void init() {
		next_schedule();
	}

	public  static String dam_format() {
		StringBuilder sb = new StringBuilder();
		sb.append(時刻.format(DateTimeFormatter.ofPattern("yyyy年MM月dd日 a hh時mm分")));
		sb.append(" 現在の石手川ダムの状況です。\n");

		sb.append("貯水率: " + 貯水率 + "%");
		sb.append(" (" + 貯水量 + "x10³m³)\n");

		sb.append("流入量: " + 流入量 + "m³/s\n");
		sb.append("放流量: " + 放流量 + "m³/s\n");

		sb.append("流域平均雨量: " + 流域平均雨量 + "mm/10min\n");

		sb.append("\n");
		sb.append("#石手川ダム\n");
		return sb.toString();
	}

	private static void next_schedule() {
		long delay = calc_delay();

		scheduler.schedule(new Runnable() {
			@Override
			public void run() {
				try {
					update();
				} catch (InterruptedException ex) {
					return;
				} catch (Exception ex) {
					StringBuilder sb = new StringBuilder();
					sb.append("#石手川ダム 貯水率の取得に失敗しました。\n");
					sb.append("```\n");
					sb.append(ex.getMessage() + "\n");
					sb.append("```");
					note(sb.toString());
					return;
				} finally {
					next_schedule();
				}
			}
		}, delay, TimeUnit.SECONDS);
	}

	private static long calc_delay() {
		LocalDateTime now = LocalDateTime.now();
		int min = now.getMinute();
		int sec = now.getSecond();

		for (int m:UPDATE_MIN) {
			if (min < m || (min == m && sec < 10)) {
				LocalDateTime target = now.withMinute(m).withSecond(10).withNano(0);
				return ChronoUnit.SECONDS.between(now, target);
			}
		}

		LocalDateTime target = now.plusHours(1).withMinute(0).withSecond(10).withNano(0);
		return ChronoUnit.SECONDS.between(now, target);
	}

	private static void note(String text) {
		Command.misskey_create_note(text, null, null, NotePublicSetting.Public, false);
	}

	private static void update() throws InterruptedException {
		Optional<String> data_url = get_data_url();
		if (data_url.isEmpty()) return;

		Optional<String> data = get_data(data_url.get());
		if (data.isEmpty()) return;

		//全行
		String[] all_line = data.get().split("\n");

		//最後の行を抽出し「,」で区切る
		String last_line = all_line[all_line.length - 1];
		String[] splited = last_line.split(",");

		//整理
		時刻 = LocalDateTime.parse(splited[0] + " " + splited[1], DateTimeFormatter.ofPattern("yyyy/MM/dd HH:mm"));
		流域平均雨量 = Double.parseDouble(splited[2]);
		貯水量 = Integer.parseInt(splited[4]);
		流入量 = Double.parseDouble(splited[6]);
		放流量 = Double.parseDouble(splited[8]);
		貯水率 = Double.parseDouble(splited[10]);

		System.out.println("[石手川] 更新しました。");
		System.out.println("[石手川]     時刻" + 時刻.toString());
		System.out.println("[石手川]     流域平均雨量" + 流域平均雨量 + "mm/10min");
		System.out.println("[石手川]     貯水量" + 貯水量 + "x10^3m^3 ("+貯水率+"%)");
		System.out.println("[石手川]     流入量" + 流入量 + "m^3/s");
		System.out.println("[石手川]     放流量" + 放流量 + "m^3/s");

		note(dam_format());
	}

	private static Optional<String> get_data_url() throws InterruptedException {
		HttpRequest.Builder b = HttpRequest.newBuilder();
		try {
			b.uri(new URI(BASE_URL + "/cgi-bin/DspDamData.exe?ID="+DAM_ID+"&KIND=3&PAGE=0"));
		} catch (URISyntaxException ex) {
			//あるわけねーだろ
		}
		b.GET();
		HttpRequest request = b.build();

		HttpResponse<String> response;
		try {
			response = ajax.send(request, BodyHandlers.ofString());
		} catch (IOException ex) {
			System.err.println("[石手川] 取得できませんでした。");
			StringBuilder sb = new StringBuilder();
			sb.append("#石手川ダム 貯水率の取得に失敗しました。\n");
			note(sb.toString());

			return Optional.empty();
		}

		if (response.statusCode() != 200) {
			System.err.println("[石手川] 取得できませんでした。");
			StringBuilder sb = new StringBuilder();
			sb.append("#石手川ダム 貯水率の取得に失敗しました。\n");
			note(sb.toString());

			return Optional.empty();
		}

		Matcher rgx = Pattern.compile("<A href=\"(.*?\\.dat)\"").matcher(response.body());
		if (rgx.find() == false) {
			System.err.println("[石手川] 正規表現エラー");
			StringBuilder sb = new StringBuilder();
			sb.append("#石手川ダム 貯水率の取得に失敗しました。\n");
			note(sb.toString());

			return Optional.empty();
		}

		//マナー」
		Thread.sleep(1000);

		return Optional.of(BASE_URL + rgx.group(1));
	}

	private static Optional<String> get_data(String url) throws InterruptedException {
		HttpRequest.Builder b = HttpRequest.newBuilder();
		try {
			b.uri(new URI(url));
		} catch (URISyntaxException ex) {
			//あるわけねーだろ
		}
		b.GET();
		HttpRequest request = b.build();

		HttpResponse<String> response;
		try {
			response = ajax.send(request, BodyHandlers.ofString());
		} catch (IOException ex) {
			System.err.println("[石手川] 取得できませんでした。");
			StringBuilder sb = new StringBuilder();
			sb.append("#石手川ダム 貯水率の取得に失敗しました。\n");
			note(sb.toString());

			return Optional.empty();
		}

		if (response.statusCode() != 200) {
			System.err.println("[石手川] 取得できませんでした。");
			StringBuilder sb = new StringBuilder();
			sb.append("#石手川ダム 貯水率の取得に失敗しました。\n");
			note(sb.toString());

			return Optional.empty();
		}

		return Optional.of(response.body());
	}
}
