export class MATH {
	constructor(TEXT) {
		this.TEXT = TEXT;
	}

	main() {
		let TEXT = this.TEXT;
		TEXT = TEXT.replace(/[^0-9.+\-*/%()]/g, "");
		if (TEXT === "") {
			console.warn("計算をしようとしましたが、計算式に有効な文字列が含まれていませんでした。\n元の計算式:" + this.TEXT);
			return "計算式に有効な文字列が含まれていません";
		}
		try {
			let MATH_RESULT = /*this.CALC(TEXT)*/ `多分結果は：「${eval(TEXT)}」です`;
			return MATH_RESULT;
		} catch (error) {
			console.error(error);
			return "計算式がおかしいぞ";
		}
	}

	CALC(TEXT) {
		let MATH_RESULT = 0;

		let RESULT = this.FORMULA_PARSE(TEXT);

		for (let I = 0; I < RESULT.length; I++) {
			const NUM = RESULT[I];
			if (!isNaN(Number(NUM))) {
				//数字か？
				if (I === 0) {
					//1回目なので
					//初期値を設定
					MATH_RESULT = parseInt(NUM);
				}
			} else {
				// 数字ではない
				if (isNaN(Number(RESULT[I + 1]))) return "計算式がおかしいぞ";
				switch (RESULT[I]) {
					case "+":
						//足し算
						MATH_RESULT = MATH_RESULT + parseInt(RESULT[I + 1]);
						break;
					case "-":
						//引き算
						MATH_RESULT = MATH_RESULT - parseInt(RESULT[I + 1]);
						break;
					case "*":
						//掛け算
						MATH_RESULT = MATH_RESULT * parseInt(RESULT[I + 1]);
						break;
					case "/":
						//割り算
						MATH_RESULT = MATH_RESULT / parseInt(RESULT[I + 1]);
						break;
				}
			}
		}

		return "計算式：" + RESULT.join(" ") + "\n" + "多分結果は：「" + MATH_RESULT.toString() + "」です";
	}

	//計算式を解析する(ChatGPT作)
	FORMULA_PARSE(FORMULA) {
		const RESULT = [];
		let currentToken = "";

		for (let i = 0; i < FORMULA.length; i++) {
			const TEXT = FORMULA[i];

			if (TEXT === " ") {
				continue;
			} else if (TEXT.match(/[0-9]/)) {
				currentToken += TEXT;
			} else if (TEXT.match(/[+\-*/]/)) {
				if (currentToken !== "") {
					RESULT.push(currentToken);
					currentToken = "";
				}
				RESULT.push(TEXT);
			} else if (TEXT === "(") {
				let SUB_FORMILA = "";
				let parenthesesCount = 1;
				i++;

				while (i < FORMULA.length) {
					if (FORMULA[i] === "(") {
						parenthesesCount++;
					} else if (FORMULA[i] === ")") {
						parenthesesCount--;
						if (parenthesesCount === 0) {
							break;
						}
					}

					SUB_FORMILA += FORMULA[i];
					i++;
				}

				RESULT.push(this.FORMULA_PARSE(SUB_FORMILA));
			}
		}

		if (currentToken !== "") {
			RESULT.push(currentToken);
		}
		return RESULT;
	}
}
