export function RUMI_HAPPY_BIRTHDAY(){
	//2007年10月29日の日付を作成
	const targetDate = new Date(2007, 9, 29); //月は0から始まるため、9は10月を表す
		
	//今日の日付を取得
	const today = new Date();
		
	//年数の差を計算
	let yearDifference = today.getFullYear() - targetDate.getFullYear();
		
	//10月29日以前の場合、1年引く
	if(
		today.getMonth() < targetDate.getMonth() ||
		(today.getMonth() === targetDate.getMonth() &&
		today.getDate() < targetDate.getDate())
	){
		yearDifference--;
	}
	return yearDifference;
}