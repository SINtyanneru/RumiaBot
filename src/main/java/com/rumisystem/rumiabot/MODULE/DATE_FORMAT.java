package com.rumisystem.rumiabot.MODULE;

import java.time.OffsetDateTime;
import java.time.format.DateTimeFormatter;

public class DATE_FORMAT {
	public static String ZHUUNI_H(OffsetDateTime DATE) {
		return DATE.format(DateTimeFormatter.ofPattern("yyyy年MM月dd日E曜日 a hh時m分s秒"));
	}
}
