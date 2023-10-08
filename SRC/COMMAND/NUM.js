import { sanitize } from "../MODULES/sanitize.js";

export class NUM {
	constructor(INTERACTION) {
		this.E = INTERACTION;
	}

	async main() {
		const NUM = this.E.options.getString("num");
		const INP = this.E.options.getString("input");
		const OUT = this.E.options.getString("output");

		console.log("はい");

		if (NUM && INP && OUT) {
			switch (OUT) {
				case "national_arabic_jp":
					await this.E.editReply("日本式区切りに変換\n" + sanitize(this.n_a_jp(NUM)));
					break;
				case "national_arabic_usa":
					await this.E.editReply("アメリカ式区切りに変換\n" + sanitize(this.n_a_usa(NUM)));
					break;
				case "roma":
					await this.E.editReply("ローマ数字変換\n" + sanitize(this.roma(NUM)));
					break;
				default:
					await this.E.editReply("エラー");
					break;
			}
		} else {
			await this.E.editReply("NG");
		}
	}

	n_a_jp(NUM) {
		const REGEX = /.{1,4}/g;
		const RESULT = NUM.match(REGEX).join(", ");

		return RESULT;
	}

	roma(NUM) {
		let RESULT = NUM;

		RESULT = RESULT.replaceAll("1000", "M ");
		RESULT = RESULT.replaceAll("100", "C ");
		RESULT = RESULT.replaceAll("10", "X ");
		RESULT = RESULT.replaceAll("1", "I ");

		RESULT = RESULT.replaceAll("2000", "MM ");
		RESULT = RESULT.replaceAll("200", "CC ");
		RESULT = RESULT.replaceAll("20", "XX ");
		RESULT = RESULT.replaceAll("2", "II ");

		RESULT = RESULT.replaceAll("3000", "MMM ");
		RESULT = RESULT.replaceAll("300", "CCC ");
		RESULT = RESULT.replaceAll("30", "XXX ");
		RESULT = RESULT.replaceAll("3", "III ");

		RESULT = RESULT.replaceAll("400", "CD ");
		RESULT = RESULT.replaceAll("40", "XL ");
		RESULT = RESULT.replaceAll("4", "IV ");

		RESULT = RESULT.replaceAll("500", "D ");
		RESULT = RESULT.replaceAll("50", "L ");
		RESULT = RESULT.replaceAll("5", "V ");

		RESULT = RESULT.replaceAll("600", "DC ");
		RESULT = RESULT.replaceAll("60", "LX ");
		RESULT = RESULT.replaceAll("6", "VI ");

		RESULT = RESULT.replaceAll("700", "DCC ");
		RESULT = RESULT.replaceAll("70", "LXX ");
		RESULT = RESULT.replaceAll("7", "VII ");

		RESULT = RESULT.replaceAll("800", "DCCC ");
		RESULT = RESULT.replaceAll("80", "LXXX ");
		RESULT = RESULT.replaceAll("8", "VIII ");

		RESULT = RESULT.replaceAll("900", "CM ");
		RESULT = RESULT.replaceAll("90", "XC ");
		RESULT = RESULT.replaceAll("9", "IX ");

		return RESULT;
	}
}
