// @ts-check

/**
 *
 * @param {Date} date
 * @param {Date} NOW_DATE
 * @returns {string}
 */
export function toDateFormatted(date, NOW_DATE = new Date()) {
	const DAY_FORMAT = ["日", "月", "火", "水", "木", "金", "土"];
	return (
		date.getFullYear().toString() +
		"年 " +
		(date.getMonth() + 1).toString() +
		"月 " +
		date.getDate().toString() +
		"日 " +
		DAY_FORMAT[date.getDay()] +
		"曜日 " +
		date.getHours().toString() +
		"時 " +
		date.getMinutes().toString() +
		"分 " +
		date.getSeconds().toString() +
		"秒 " +
		date.getMilliseconds().toString() +
		"ミリ秒\n" +
		//アメリカ表記
		Math.floor((NOW_DATE.valueOf() - date.valueOf()) / (1000 * 60 * 60 * 24)).toString() +
		"日前"
	);
}
