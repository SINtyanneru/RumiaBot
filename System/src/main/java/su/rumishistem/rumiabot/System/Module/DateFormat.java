package su.rumishistem.rumiabot.System.Module;

import java.time.OffsetDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Locale;

public class DateFormat {
	public static String _12H(OffsetDateTime DATE) {
		return DATE.format(DateTimeFormatter.ofPattern("yyyy年MM月dd日E曜日 a hh時m分s秒").withLocale(Locale.JAPANESE));
	}
}
