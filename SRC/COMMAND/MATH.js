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
		let TEXT_SPLIT = TEXT.split("");
		let MATH_RESULT = 0;
		
		for (let I = 0; I < TEXT_SPLIT.length; I++) {
			const NUM = TEXT_SPLIT[I];
			switch(NUM){
				case "+":
					I++;
					var RESULT = this.IS_INT(TEXT_SPLIT, I);

					MATH_RESULT = parseInt(MATH_RESULT + RESULT.RESULT);
					I = RESULT.I;
					break;
				case "-":
					I++;
					var RESULT = this.IS_INT(TEXT_SPLIT, I);
	
					MATH_RESULT = parseInt(MATH_RESULT - RESULT.RESULT);
					I = RESULT.I;
					break;
				default:
					var RESULT = this.IS_INT(TEXT_SPLIT, I);

					MATH_RESULT = parseInt(MATH_RESULT + RESULT.RESULT);
					I = RESULT.I;
					break;
			}
		}

		return MATH_RESULT;
	}

	//数字かをチェックする
	IS_INT(TEXT, I){
		let REGEX = /^\d+$/;
		let RESULT_TEXT = "";

		for (; I < TEXT.length; I++) {
			const NUM = TEXT[I];
			if(REGEX.test(NUM)){
				RESULT_TEXT += NUM;
			}
		}

		return {
			RESULT:parseInt(RESULT_TEXT),
			I:I
		};
	}
}