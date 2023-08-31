/**
 * 漢字と旧漢字の置き換え
 */
class KANJI{
	constructor(INTERACTION) {
		this.E = INTERACTION;
		this.KANJI_JSON = [{"old":"堯","new":"尭"},{"old":"亞","new":"亜"},{"old":"惡","new":"悪"},{"old":"壓","new":"圧"},{"old":"圍","new":"囲"},{"old":"爲","new":"為"},{"old":"醫","new":"医"},{"old":"壹","new":"壱"},{"old":"逸","new":"逸"},{"old":"稻","new":"稲"},{"old":"飮","new":"飲"},{"old":"隱","new":"隠"},{"old":"羽","new":"羽"},{"old":"營","new":"営"},{"old":"榮","new":"栄"},{"old":"衞","new":"衛"},{"old":"益","new":"益"},{"old":"驛","new":"駅"},{"old":"悅","new":"悦"},{"old":"圓","new":"円"},{"old":"艷","new":"艶"},{"old":"鹽","new":"塩"},{"old":"奧","new":"奥"},{"old":"應","new":"応"},{"old":"橫","new":"横"},{"old":"歐","new":"欧"},{"old":"毆","new":"殴"},{"old":"穩","new":"穏"},{"old":"假","new":"仮"},{"old":"價","new":"価"},{"old":"畫","new":"画"},{"old":"會","new":"会"},{"old":"壞","new":"壊"},{"old":"懷","new":"懐"},{"old":"繪","new":"絵"},{"old":"擴","new":"拡"},{"old":"殼","new":"殻"},{"old":"覺","new":"覚"},{"old":"學","new":"学"},{"old":"嶽","new":"岳"},{"old":"樂","new":"楽"},{"old":"勸","new":"勧"},{"old":"卷","new":"巻"},{"old":"寬","new":"寛"},{"old":"歡","new":"歓"},{"old":"罐","new":"缶"},{"old":"觀","new":"観"},{"old":"閒","new":"間"},{"old":"關","new":"関"},{"old":"陷","new":"陥"},{"old":"館","new":"館"},{"old":"巖","new":"巌"},{"old":"顏","new":"顔"},{"old":"歸","new":"帰"},{"old":"氣","new":"気"},{"old":"龜","new":"亀"},{"old":"僞","new":"偽"},{"old":"戲","new":"戯"},{"old":"犧","new":"犠"},{"old":"舊","new":"旧"},{"old":"據","new":"拠"},{"old":"擧","new":"挙"},{"old":"峽","new":"峡"},{"old":"挾","new":"挟"},{"old":"敎","new":"教"},{"old":"狹","new":"狭"},{"old":"曉","new":"暁"},{"old":"區","new":"区"},{"old":"驅","new":"駆"},{"old":"勳","new":"勲"},{"old":"薰","new":"薫"},{"old":"徑","new":"径"},{"old":"惠","new":"恵"},{"old":"溪","new":"渓"},{"old":"經","new":"経"},{"old":"繼","new":"継"},{"old":"莖","new":"茎"},{"old":"螢","new":"蛍"},{"old":"輕","new":"軽"},{"old":"鷄","new":"鶏"},{"old":"藝","new":"芸"},{"old":"缺","new":"欠"},{"old":"儉","new":"倹"},{"old":"劍","new":"剣"},{"old":"圈","new":"圏"},{"old":"檢","new":"検"},{"old":"權","new":"権"},{"old":"獻","new":"献"},{"old":"縣","new":"県"},{"old":"險","new":"険"},{"old":"顯","new":"顕"},{"old":"驗","new":"験"},{"old":"嚴","new":"厳"},{"old":"效","new":"効"},{"old":"廣","new":"広"},{"old":"恆","new":"恒"},{"old":"鑛","new":"鉱"},{"old":"號","new":"号"},{"old":"國","new":"国"},{"old":"黑","new":"黒"},{"old":"濟","new":"済"},{"old":"碎","new":"砕"},{"old":"齋","new":"斎"},{"old":"劑","new":"剤"},{"old":"櫻","new":"桜"},{"old":"册","new":"冊"},{"old":"雜","new":"雑"},{"old":"參","new":"参"},{"old":"慘","new":"惨"},{"old":"棧","new":"桟"},{"old":"蠶","new":"蚕"},{"old":"贊","new":"賛"},{"old":"殘","new":"残"},{"old":"絲","new":"糸"},{"old":"飼","new":"飼"},{"old":"齒","new":"歯"},{"old":"兒","new":"児"},{"old":"辭","new":"辞"},{"old":"濕","new":"湿"},{"old":"實","new":"実"},{"old":"舍","new":"舎"},{"old":"寫","new":"写"},{"old":"釋","new":"釈"},{"old":"壽","new":"寿"},{"old":"收","new":"収"},{"old":"從","new":"従"},{"old":"澁","new":"渋"},{"old":"獸","new":"獣"},{"old":"縱","new":"縦"},{"old":"肅","new":"粛"},{"old":"處","new":"処"},{"old":"緖","new":"緒"},{"old":"諸","new":"諸"},{"old":"敍","new":"叙"},{"old":"奬","new":"奨"},{"old":"將","new":"将"},{"old":"燒","new":"焼"},{"old":"祥","new":"祥"},{"old":"稱","new":"称"},{"old":"證","new":"証"},{"old":"乘","new":"乗"},{"old":"剩","new":"剰"},{"old":"壤","new":"壌"},{"old":"孃","new":"嬢"},{"old":"條","new":"条"},{"old":"淨","new":"浄"},{"old":"疊","new":"畳"},{"old":"穰","new":"穣"},{"old":"讓","new":"譲"},{"old":"釀","new":"醸"},{"old":"囑","new":"嘱"},{"old":"觸","new":"触"},{"old":"寢","new":"寝"},{"old":"愼","new":"慎"},{"old":"晉","new":"晋"},{"old":"眞","new":"真"},{"old":"神","new":"神"},{"old":"盡","new":"尽"},{"old":"圖","new":"図"},{"old":"粹","new":"粋"},{"old":"醉","new":"酔"},{"old":"隨","new":"随"},{"old":"髓","new":"髄"},{"old":"數","new":"数"},{"old":"樞","new":"枢"},{"old":"瀨","new":"瀬"},{"old":"晴","new":"晴"},{"old":"淸","new":"清"},{"old":"精","new":"精"},{"old":"聲","new":"声"},{"old":"靑","new":"青"},{"old":"靜","new":"静"},{"old":"齊","new":"斉"},{"old":"攝","new":"摂"},{"old":"竊","new":"窃"},{"old":"專","new":"専"},{"old":"戰","new":"戦"},{"old":"淺","new":"浅"},{"old":"潛","new":"潜"},{"old":"纖","new":"繊"},{"old":"踐","new":"践"},{"old":"錢","new":"銭"},{"old":"禪","new":"禅"},{"old":"雙","new":"双"},{"old":"壯","new":"壮"},{"old":"搜","new":"捜"},{"old":"插","new":"挿"},{"old":"爭","new":"争"},{"old":"總","new":"総"},{"old":"聰","new":"聡"},{"old":"莊","new":"荘"},{"old":"裝","new":"装"},{"old":"騷","new":"騒"},{"old":"增","new":"増"},{"old":"臟","new":"臓"},{"old":"藏","new":"蔵"},{"old":"屬","new":"属"},{"old":"續","new":"続"},{"old":"墮","new":"堕"},{"old":"體","new":"体"},{"old":"對","new":"対"},{"old":"帶","new":"帯"},{"old":"滯","new":"滞"},{"old":"臺","new":"台"},{"old":"瀧","new":"滝"},{"old":"擇","new":"択"},{"old":"澤","new":"沢"},{"old":"單","new":"単"},{"old":"擔","new":"担"},{"old":"膽","new":"胆"},{"old":"團","new":"団"},{"old":"彈","new":"弾"},{"old":"斷","new":"断"},{"old":"癡","new":"痴"},{"old":"遲","new":"遅"},{"old":"晝","new":"昼"},{"old":"蟲","new":"虫"},{"old":"鑄","new":"鋳"},{"old":"猪","new":"猪"},{"old":"廳","new":"庁"},{"old":"聽","new":"聴"},{"old":"鎭","new":"鎮"},{"old":"塚","new":"塚"},{"old":"遞","new":"逓"},{"old":"鐵","new":"鉄"},{"old":"轉","new":"転"},{"old":"點","new":"点"},{"old":"傳","new":"伝"},{"old":"都","new":"都"},{"old":"黨","new":"党"},{"old":"盜","new":"盗"},{"old":"燈","new":"灯"},{"old":"當","new":"当"},{"old":"鬪","new":"闘"},{"old":"德","new":"徳"},{"old":"獨","new":"独"},{"old":"讀","new":"読"},{"old":"屆","new":"届"},{"old":"繩","new":"縄"},{"old":"貳","new":"弐"},{"old":"惱","new":"悩"},{"old":"腦","new":"脳"},{"old":"廢","new":"廃"},{"old":"拜","new":"拝"},{"old":"賣","new":"売"},{"old":"麥","new":"麦"},{"old":"發","new":"発"},{"old":"髮","new":"髪"},{"old":"拔","new":"抜"},{"old":"飯","new":"飯"},{"old":"蠻","new":"蛮"},{"old":"祕","new":"秘"},{"old":"濱","new":"浜"},{"old":"甁","new":"瓶"},{"old":"福","new":"福"},{"old":"拂","new":"払"},{"old":"佛","new":"仏"},{"old":"竝","new":"並"},{"old":"變","new":"変"},{"old":"邊","new":"辺"},{"old":"辨","new":"弁"},{"old":"辯","new":"弁"},{"old":"瓣","new":"弁"},{"old":"舖","new":"舗"},{"old":"穗","new":"穂"},{"old":"寶","new":"宝"},{"old":"豐","new":"豊"},{"old":"沒","new":"没"},{"old":"槇","new":"槙"},{"old":"萬","new":"万"},{"old":"滿","new":"満"},{"old":"默","new":"黙"},{"old":"彌","new":"弥"},{"old":"藥","new":"薬"},{"old":"譯","new":"訳"},{"old":"靖","new":"靖"},{"old":"藪","new":"薮"},{"old":"豫","new":"予"},{"old":"餘","new":"余"},{"old":"與","new":"与"},{"old":"譽","new":"誉"},{"old":"搖","new":"揺"},{"old":"樣","new":"様"},{"old":"謠","new":"謡"},{"old":"遙","new":"遥"},{"old":"來","new":"来"},{"old":"賴","new":"頼"},{"old":"亂","new":"乱"},{"old":"覽","new":"覧"},{"old":"隆","new":"隆"},{"old":"龍","new":"竜"},{"old":"兩","new":"両"},{"old":"獵","new":"猟"},{"old":"綠","new":"緑"},{"old":"壘","new":"塁"},{"old":"勵","new":"励"},{"old":"禮","new":"礼"},{"old":"隸","new":"隷"},{"old":"靈","new":"霊"},{"old":"齡","new":"齢"},{"old":"戀","new":"恋"},{"old":"爐","new":"炉"},{"old":"勞","new":"労"},{"old":"朗","new":"朗"},{"old":"樓","new":"楼"},{"old":"郞","new":"郎"},{"old":"祿","new":"禄"},{"old":"灣","new":"湾"},{"old":"瑤","new":"瑶"},{"old":"鄕","new":"郷"},{"old":"敕","new":"勅"},{"old":"霸","new":"覇"},{"old":"襃","new":"褒"},{"old":"飜","new":"翻"},{"old":"亙","new":"亘"}];
	}
	
	async main(){
		try{
			let E = this.E;
			let KANJI_JSON = this.KANJI_JSON;
			let TEXT = E.options.getString("text");
			let MODE = E.options.getString("mode");
			
			if(MODE === "n_o"){
				this.N_O_XEST(E, KANJI_JSON, TEXT);
			}else{
				this.O_N_XEST(E, KANJI_JSON, TEXT);
			}
		}catch(EX){

		}
	}

	//新漢字を旧漢字へ変換する
	async N_O_XEST(E, KANJI_JSON, TEXT){
		for (let I = 0; I < KANJI_JSON.length; I++) {
			const KANJI_TEXT = KANJI_JSON[I];
			TEXT = TEXT.replaceAll(KANJI_TEXT.new, KANJI_TEXT.old);
		}

		E.editReply(TEXT);
	}

	//旧漢字を新漢字へ変換する
	async O_N_XEST(E, KANJI_JSON, TEXT){
		for (let I = 0; I < KANJI_JSON.length; I++) {
			const KANJI_TEXT = KANJI_JSON[I];
			TEXT = TEXT.replaceAll(KANJI_TEXT.old, KANJI_TEXT.new);
		}

		E.editReply(TEXT);
	}
}