import { SlashCommandBuilder } from "@discordjs/builders";

//@ts-check
export class LETTER {
	static command = new SlashCommandBuilder()
		.setName("letter")
		.setDescription("文字を色々変換してくれます、たぶん")
		.addStringOption(o =>
			o
				.setName("old")
				.setDescription("変換前")
				.setChoices(
					{
						name: "ひらがな",
						value: "hilagana"
					},
					{
						name: "ラテン文字",
						value: "latin"
					}
				)
				.setRequired(true)
		)
		.addStringOption(o =>
			o
				.setName("new")
				.setDescription("変換後")
				.setChoices(
					{
						name: "ひらがな",
						value: "hilagana"
					},
					{
						name: "ラテン文字",
						value: "latin"
					}
				)
				.setRequired(true)
		);
	constructor(INTERACTION) {
		this.E = INTERACTION;
		this.LETTER_JSON = {
			HILAGANA: {
				"あ": "ä",
				"あ゛": "ʕ",
				"あ゙": "ʕ",
				"い": "i",
				"う": "ɯ̹",
				"う゚": "ŋ",
				"う゜": "ŋ",
				"え": "e̞",
				"お": "o̞",

				"ぁ": "ä",
				"ぃ": "i",
				"ぅ": "ɯ̹",
				"ぇ": "e̞",
				"ぉ": "o̞",

				"か": "kä",
				"き": "ki",
				"く": "kɯ̹",
				"け": "ke̞",
				"こ": "ko̞",

				"か゜": "ŋä",
				"き゜": "ŋʲi̞",
				"く゜": "ŋɯ̹˕",
				"け゜": "ŋe̞",
				"こ゜": "ŋo̜",

				"か゚": "ŋä",
				"き゚": "ŋʲi̞",
				"く゚": "ŋɯ̹˕",
				"け゚": "ŋe̞",
				"こ゚": "ŋo̜",

				"が": "gä",
				"ぎ": "gi",
				"ぐ": "gɯ̹",
				"げ": "ge̞",
				"ご": "go̞",

				"きゃ": "kjä",
				"きぃ": "kji",
				"きゅ": "kjɯ̹",
				"きぇ": "kje̞",
				"きょ": "kjo̞",

				"ぎゃ": "gjä",
				"ぎぃ": "gji",
				"ぎゅ": "gjɯ̹",
				"ぎぇ": "gje̞",
				"ぎょ": "gjo̞",

				"さ": "sä",
				"し": "ɕi",
				"す": "sɯ̹",
				"せ": "se̞",
				"そ": "so̞",

				"ざ": "zä",
				"じ": "zi",
				"ず": "zɯ̹",
				"ぜ": "ze̞",
				"ぞ": "zo̞",

				"しゃ": "ɕä",
				"しぃ": "ɕi",
				"しゅ": "ɕɯ̹",
				"しぇ": "ɕe̞",
				"しょ": "ɕo̞",

				"じゃ": "d͡ʑä",
				"じぃ": "d͡ʑi",
				"じゅ": "d͡ʑɯ̹",
				"じぇ": "d͡ʑe̞",
				"じぉ": "d͡ʑo̞",

				"た": "tä",
				"ち": "t͡ɕʲi",
				"つ": "tɯ̹",
				"て": "te̞",
				"と": "to̞",

				"だ": "dä",
				"ぢ": "di",
				"づ": "dɯ̹",
				"で": "de̞",
				"ど": "do̞",

				"ちゃ": "t͡ɕʲä",
				"ちぃ": "t͡ɕʲi",
				"ちゅ": "t͡ɕʲɯ̹",
				"ちぇ": "t͡ɕʲe̞",
				"ちょ": "t͡ɕʲo̞",

				"な": "nä",
				"に": "ni",
				"ぬ": "nɯ̹",
				"ね": "ne̞",
				"の": "no̞",

				"にゃ": "njä",
				"にぃ": "nji",
				"にゅ": "njɯ̹",
				"にぇ": "nje̞",
				"にょ": "njo̞",

				"は": "hä",
				"ひ": "hi",
				"ふ": "hɯ̹",
				"へ": "he̞",
				"ほ": "ho̞",

				"ひゃ": "hjä",
				"ひぃ": "hji",
				"ひゅ": "hjɯ̹",
				"ひぇ": "hje̞",
				"ひょ": "hjo̞",

				"ば": "bä",
				"び": "bi",
				"ぶ": "bɯ̹",
				"べ": "be̞",
				"ぼ": "bo̞",

				"びゃ": "hjä",
				"びぃ": "hji",
				"びゅ": "hjɯ̹",
				"びぇ": "hje̞",
				"びょ": "hjo̞",

				"ま": "mä",
				"み": "mi",
				"む": "mɯ̹",
				"め": "me̞",
				"も": "mo̞",

				"みゃ": "mjä",
				"みぃ": "mji",
				"みゅ": "mjɯ̹",
				"みぇ": "mje̞",
				"みょ": "mjo̞",

				"や": "jä",
				"ゆ": "jɯ̹",
				"よ": "jo̞",

				"ゃ": "jä",
				"ゅ": "jɯ̹",
				"ょ": "jo̞",

				"ら": "ɺä",
				"り": "ɺi",
				"る": "ɺɯ̹",
				"れ": "ɺe̞",
				"ろ": "ɺo̞",

				"ら゚": "lä",
				"り゚": "li",
				"る゚": "lɯ̹",
				"れ゚": "le̞",
				"ろ゚": "lo̞",

				"ら゜": "lä",
				"り゜": "li",
				"る゜": "lɯ̹",
				"れ゜": "le̞",
				"ろ゜": "lo̞",

				"わ": "wä",
				"ゐ": "wi",
				"う〻": "wɯ̹",
				"ゑ": "we̞",
				"を": "wo̞",

				"ん": "n'",

				"ー": "ː"
			},
			LATIN: {
				A: "A"
			}
		};
	}

	async main() {
		let E = this.E;
		try {
			let OLD_TEXT = E.options.getString("text");
			let OLD_LETTER = E.options.getString("old").toUpperCase();

			let NEW_TEXT = await this.XEST_IPA(OLD_TEXT, OLD_LETTER);

			E.editReply("```" + OLD_TEXT + "```\n" + "↓↓↓↓↓↓↓↓↓↓\n" + "```\n" + NEW_TEXT + "\n```");
		} catch (EX) {
			E.editReply("変換時にエラーが発生したよ☆");
			console.error("[ ERR ][ LETTER ]" + EX);
		}
	}

	async XEST_IPA(TEXT, OLD_LETTER) {
		let LETTER_JSON = this.LETTER_JSON;

		let LATIN_TEXT = "";

		for (let I = 0; I < TEXT.split("").length; I++) {
			const SPLIT_TEXT = TEXT.split("")[I];

			//複合文字の場合をチェック
			if (LETTER_JSON[OLD_LETTER][SPLIT_TEXT + TEXT.split("")[I + 1]] !== undefined) {
				LATIN_TEXT += LETTER_JSON[OLD_LETTER][SPLIT_TEXT + TEXT.split("")[I + 1]].toUpperCase();
				I++;
			} else if (LETTER_JSON[OLD_LETTER][SPLIT_TEXT] !== undefined) {
				//無いので普通に入れる
				LATIN_TEXT += LETTER_JSON[OLD_LETTER][SPLIT_TEXT].toUpperCase();
			}
		}

		return LATIN_TEXT;
	}
}
