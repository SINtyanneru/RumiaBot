/**
 * 男「今日は何の日？」
 * ズール人「ああ、今日はお前ら英国人がズール人を16ポンド砲で虐殺した日だな」
 * 男「まじでごめんて」
 */
const WND_JSON = {
	"9/3":{
		TITLE:"クソ見てえな感じ",
		TEXT:"とてもクソです"
	}
}
class WHAT_NOW_DAY{
	main(message){
		const NOW_DATE = new Date();
		const WND = WND_JSON[(NOW_DATE.getMonth() + 1) + "/" + NOW_DATE.getDate()];
		if(WND !== null && WND !== undefined){
			message.reply("今日は" + WND.TITLE + "の日です！\n" + WND.TEXT);
		}else{
			message.reply("今日は何の日でもありません");
		}
	}
}