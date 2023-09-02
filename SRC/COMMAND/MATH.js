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
		
		for (let I = 0; I < RESULT.length; I++) {
			const NUM = RESULT[I];
			if(!isNaN(Number(NUM))){//数字か？
				if(I === 0){//1回目なので
					//初期値を設定
					MATH_RESULT = parseInt(NUM);
				}else{//2回目移行なので
					console.log(RESULT[I]);
				}
			}else{
				//数字ではない
				switch(RESULT[I]){
					case "+":
						//足し算
						if(!isNaN(Number(RESULT[I + 1]))){//数字か？
							MATH_RESULT = MATH_RESULT + parseInt(RESULT[I + 1]);
						}else{
							return "計算式がおかしいぞ";
						}
						break;
					case "-":
						//引き算
						if(!isNaN(Number(RESULT[I + 1]))){//数字か？
							MATH_RESULT = MATH_RESULT - parseInt(RESULT[I + 1]);
						}else{
							return "計算式がおかしいぞ";
						}
						break;
					case "*":
						//掛け算
						if(!isNaN(Number(RESULT[I + 1]))){//数字か？
							MATH_RESULT = MATH_RESULT * parseInt(RESULT[I + 1]);
						}else{
							return "計算式がおかしいぞ";
						}
						break;
					case "/":
						//割り算
						if(!isNaN(Number(RESULT[I + 1]))){//数字か？
							MATH_RESULT = MATH_RESULT / parseInt(RESULT[I + 1]);
						}else{
							return "計算式がおかしいぞ";
						}
						break;
				}
			}
		}
		
		return "計算式：" + RESULT.join(" ") + "\n" + "多分結果は：「" + MATH_RESULT.toString()  + "」です";
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