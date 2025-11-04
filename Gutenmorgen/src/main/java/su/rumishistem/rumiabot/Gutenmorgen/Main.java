package su.rumishistem.rumiabot.Gutenmorgen;

import static su.rumishistem.rumiabot.System.Main.get_discord_bot;
import static su.rumishistem.rumiabot.System.Main.get_misskey_bot;

import java.io.IOException;
import java.net.MalformedURLException;
import java.sql.SQLException;
import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.format.TextStyle;
import java.util.*;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.entities.channel.concrete.TextChannel;
import su.rumishistem.rumi_java_lib.ArrayNode;
import su.rumishistem.rumi_java_lib.SQL;
import su.rumishistem.rumi_java_lib.Ajax.Ajax;
import su.rumishistem.rumi_java_lib.Ajax.AjaxResult;
import su.rumishistem.rumi_java_lib.MisskeyBot.Builder.NoteBuilder;
import su.rumishistem.rumi_java_lib.MisskeyBot.Type.NoteVisibility;
import su.rumishistem.rumiabot.Gutenmorgen.Type.WetherCode;
import su.rumishistem.rumiabot.Gutenmorgen.Type.WetherData;
import su.rumishistem.rumiabot.System.Type.FunctionClass;

public class Main implements FunctionClass {
	private static final String latitude = "33.8391";	//緯度
	private static final String longitude = "132.7655";	//緯度

	@Override
	public String function_name() {
		return "Guten morgen";
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
		Timer timer = new Timer(true);
		TimerTask task = new TimerTask() {
			@Override
			public void run() {
				guten_morgen();
			}
		};

		//次の午前8時を計算
		Calendar calendar = Calendar.getInstance();
		calendar.set(Calendar.HOUR_OF_DAY, 8);
		calendar.set(Calendar.MINUTE, 0);
		calendar.set(Calendar.SECOND, 0);
		calendar.set(Calendar.MILLISECOND, 0);

		//既に今日の8時を過ぎてたら翌日に時刻を設定する
		Date now_date = new Date();
		if (calendar.getTime().before(now_date)) {
			calendar.add(Calendar.DAY_OF_MONTH, 1);
		}

		//最初に実行する時刻
		Date first_run_time = calendar.getTime();
		long period = 1000L * 60 * 60 * 24;		//24時間
		timer.scheduleAtFixedRate(task, first_run_time, period);
	}

	private JsonNode get_wether() {
		try {
			Ajax ajax = new Ajax("https://api.open-meteo.com/v1/forecast?latitude="+latitude+"&longitude="+longitude+"&daily=temperature_2m_max,temperature_2m_min,weather_code,uv_index_max,rain_sum,wind_speed_10m_max,wind_gusts_10m_max&current=temperature_2m&timezone=Asia%2FTokyo");
			AjaxResult result = ajax.GET();

			JsonNode body = new ObjectMapper().readTree(result.get_body_as_string());
			return body;
		} catch (MalformedURLException ex) {
			//イラン
		} catch (IOException ex) {
			//要らん
		}

		return null;
	}

	private void guten_morgen() {
		JsonNode data = get_wether();
		LocalDate now_date = LocalDate.now();

		WetherData now_wether = new WetherData(
			data.get("current").get("temperature_2m").asDouble(),
			data.get("daily").get("temperature_2m_min").get(0).asDouble(),
			data.get("daily").get("temperature_2m_max").get(0).asDouble(),
			WetherCode.get_from_code(data.get("daily").get("weather_code").get(0).asInt())
		);

		StringBuilder text = new StringBuilder();
		text.append("おはよっ！\n");
		text.append(now_date.getMonthValue() + "月"+now_date.getDayOfMonth()+"日"+now_date.getDayOfWeek().getDisplayName(TextStyle.SHORT, Locale.JAPANESE)+"曜日、今日は{}の日だよ！\n");
		text.append("\n");
		text.append("今日の天気は"+convert_wether(now_wether.get_wether())+"、気温"+now_wether.get_now_temp()+"℃。\n");
		text.append("最高気温は"+now_wether.get_max_temp()+"℃、最低気温は"+now_wether.get_min_temp()+"℃だよ！\n");
		double rain_sum = data.get("daily").get("rain_sum").get(0).asDouble();
		if (rain_sum != 0.0) {
			text.append("降水量は"+rain_sum+"mm\n");
		}
		text.append("\n");

		if (now_date.getDayOfWeek() == DayOfWeek.MONDAY) {
			text.append("今週の7日間の天気予報は、\n");

			JsonNode daily = data.get("daily");
			for (int i = 1; i < daily.get("time").size(); i++) {
				LocalDate date = LocalDate.parse(daily.get("time").get(i).asText());
				double max_temp = daily.get("temperature_2m_max").get(i).asDouble();
				double min_temp = daily.get("temperature_2m_min").get(i).asDouble();
				WetherCode wether = WetherCode.get_from_code(daily.get("weather_code").get(i).asInt());

				text.append(date.getDayOfMonth()+"月"+date.getDayOfWeek()+"日　"+convert_wether(wether)+"　最高気温"+max_temp+"℃　最低気温"+min_temp+"℃\n");
			}
			text.append("\n");
		}

		text.append("今日も一日がんばろ！");
		String complete_text = text.toString();

		//Misskey
		NoteBuilder nb = new NoteBuilder();
		nb.set_text(complete_text);
		nb.set_visibility(NoteVisibility.Public);
		get_misskey_bot().get_client().create_note(nb);

		//Discord
		try {
			ArrayNode sql = SQL.RUN("SELECT `CID` FROM `CONFIG` WHERE `FUNC_ID` = 'guten_morgen';", new Object[] {});
			for (int i = 0; i < sql.length(); i++) {
				String channel_id = sql.get(i).getData("CID").asString();
				TextChannel channel = get_discord_bot().get_primary_bot().getTextChannelById(channel_id);
				if (channel == null) continue;

				//権限チェック
				if (!channel.getGuild().getSelfMember().hasPermission(channel, Permission.MESSAGE_SEND)) {
					continue;
				}

				channel.sendMessage(complete_text).queue();
			}
		} catch (SQLException ex) {
			//SQLエラー
		}
	}

	private String convert_wether(WetherCode code) {
		switch (code) {
			case WetherCode.快晴: return "☀️快晴";
			case WetherCode.晴れ: return "☀️晴れ";
			case WetherCode.一部曇り: return "🌤️一部曇り";
			case WetherCode.曇り: return "☁️曇り";
			case WetherCode.霧: return "🌫️霧";
			case WetherCode.霧雨: return "霧雨";
			case WetherCode.雨: return "🌧️雨";
			case WetherCode.雪: return "☃雪";
			case WetherCode.俄雨: return "🌧️俄雨";
			case WetherCode.雹: return "雹";
			case WetherCode.雷雨: return "⛈️雷雨";

			default: return "?";
		}
	}
}