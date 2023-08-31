class LETTER{
	constructor(INTERACTION) {
		this.E = INTERACTION;
		this.LETTER_JSON = {
			"HILAGANA":{
				"あ":"ä",
				"あ゛":"ʕ",
				"あ゙":"ʕ",
				"い":"i",
				"う":"ɯ̹",
				"う゚":"ŋ",
				"う゜":"ŋ",
				"え":"e̞",
				"お":"o̞",

				"か":"kä",
				"き":"ki",
				"く":"kɯ̹",
				"け":"ke̞",
				"こ":"ko̞",

				"か゜":"ŋä",
				"き゜":"ŋʲi̞",
				"く゜":"ŋɯ̹˕",
				"け゜":"ŋe̞",
				"こ゜":"ŋo̜",

				"か゚":"ŋä",
				"き゚":"ŋʲi̞",
				"く゚":"ŋɯ̹˕",
				"け゚":"ŋe̞",
				"こ゚":"ŋo̜",

				"が":"gä",
				"ぎ":"gi",
				"ぐ":"gɯ̹",
				"げ":"ge̞",
				"ご":"go̞",

				"さ":"sä",
				"し":"si",
				"す":"sɯ̹",
				"せ":"se̞",
				"そ":"so̞",

				
			},
			"LATIN":{
				"A":"A"
			}
		}
	}
	
	async main(){
		let E = this.E;
		try{
			let LETTER_JSON = this.LETTER_JSON;
			let OLD_TEXT = E.options.getString("text");
			let OLD_LETTER = E.options.getString("old").toUpperCase();
			let NEW_LETTER = E.options.getString("new").toUpperCase();

			let NEW_TEXT = await this.XEST_LATIN(OLD_TEXT, OLD_LETTER);

			E.editReply("```\n" + NEW_TEXT + "\n```");
		}catch(EX){
			E.editReply("変換時にエラーが発生したよ☆");
			console.log("[ ERR ][ LETTER ]" + EX)
		}
	}

	async XEST_LATIN(TEXT, OLD_LETTER){
		let LETTER_JSON = this.LETTER_JSON;

		let LATIN_TEXT = "";

		for (let I = 0; I < TEXT.split("").length; I++) {
			const SPLIT_TEXT = TEXT.split("")[I];
			if(LETTER_JSON[OLD_LETTER][SPLIT_TEXT] !== undefined){
				LATIN_TEXT += LETTER_JSON[OLD_LETTER][SPLIT_TEXT].toUpperCase();
			}
		}

		return LATIN_TEXT;
	}
}