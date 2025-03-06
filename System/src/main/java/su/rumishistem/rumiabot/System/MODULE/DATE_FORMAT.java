package su.rumishistem.rumiabot.System.MODULE;

import java.time.OffsetDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Locale;

public class DATE_FORMAT {
	public static String ZHUUNI_H(OffsetDateTime DATE) {
		return DATE.format(DateTimeFormatter.ofPattern("yyyy年MM月dd日E曜日 a hh時m分s秒").withLocale(Locale.JAPANESE));
	}

	public static String KOUKI(OffsetDateTime DATE) {
		int NEN = Integer.parseInt(DATE.format(DateTimeFormatter.ofPattern("yyyy")));
		NEN = NEN + 660;

		return DATE.format(DateTimeFormatter.ofPattern(NEN + "年MM月dd日E曜日 a hh時m分s秒").withLocale(Locale.JAPANESE));
	}

	public static String KOUKI_SEIREKI(OffsetDateTime DATE) {
		int SEIREKI = Integer.parseInt(DATE.format(DateTimeFormatter.ofPattern("yyyy")));
		int KOUKI = SEIREKI + 660;

		return DATE.format(DateTimeFormatter.ofPattern(KOUKI + "(" + SEIREKI + ")" + "年MM月dd日E曜日 a hh時m分s秒").withLocale(Locale.JAPANESE));
	}
}
