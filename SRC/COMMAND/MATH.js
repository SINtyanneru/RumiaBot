/**
 * JSがクソなので実装できず、封印
 */
class MATH{
	constructor(TEXT){
		this.TEXT = TEXT;
	}

	async main(){
		let TEXT = this.TEXT;

		let MATH_RESULT = await this.CALC(TEXT);

		return MATH_RESULT;
	}

	async CALC(TEXT){
		let MATH_RESULT = 0;

		let RESULT = this.FORMULA_PARSE(TEXT);
		console.log(RESULT);
		
		return MATH_RESULT;
	}

	//計算式を解析する(ChatGPT作)
	FORMULA_PARSE(FORMULA){
		const RESULT = [];
		let currentToken = "";

		for (let i = 0; i < FORMULA.length; i++) {
			const TEXT = FORMULA[i];

			if (TEXT === " ") {
				continue;
			}else if (TEXT.match(/[0-9]/)){
				currentToken += TEXT;
			}else if (TEXT.match(/[+\-*/]/)){
				if (currentToken !== "") {
					RESULT.push(currentToken);
					currentToken = "";
				}
				RESULT.push(TEXT);
			}else if (TEXT === "("){
				let SUB_FORMILA = "";
				let parenthesesCount = 1;
				i++;

				while (i < FORMULA.length) {
					if (FORMULA[i] === "(") {
						parenthesesCount++;
					}else if (FORMULA[i] === ")"){
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