export function REON4213(TEXT){
	let COMMAND = TEXT;

	COMMAND = COMMAND.replaceAll("\n", "");
	COMMAND = COMMAND.replaceAll("\t", "");

	if(COMMAND.startsWith("Quell->{") && COMMAND.match(/\}->ExeC->\{.*\}/)){
		const ACTIVATOR = COMMAND.match(/\}->ExeC->\{([^}]*)\}/);
		if(ACTIVATOR[1] === "BOT"){
			COMMAND = COMMAND.replace("Quell->{", "");
			COMMAND = COMMAND.replace(/\}->ExeC->\{.*\}/, "");

			const COMMAND_SPLIT = COMMAND.split("");
			let I = 0;
			if((COMMAND_SPLIT[I] === "C") && (COMMAND_SPLIT[I + 1] === "l") && (COMMAND_SPLIT[I + 2] === "s")){
				//カーソルを2個進める
				I = I + 2;

				//主語の括弧内を取得
				let CLS_A = GROUP_PARSE(COMMAND_SPLIT, "(", ")", I + 1);
				I = CLS_A.OFSET;

				let CLS_B = GROUP_PARSE(COMMAND_SPLIT, "{", "}", I + 1);
				I = CLS_B.OFSET;

				//AはCMDか
				if(CLS_A.CONTENTS === "CMD"){
					//B内のVブロックを解析する
					const V_BLOCK = CLS_B.CONTENTS.split(";");
					let V_BLOCK_CONTENTS = [];

					V_BLOCK.forEach((ROW) => {
						if(ROW !== ""){
							const MATCH = ROW.match(/^EX\[([^}]*)\]->\(([^}]*)\)$/);
							V_BLOCK_CONTENTS.push({
								A:MATCH[1],
								B:MATCH[2]
							});
						}
					});

					return V_BLOCK_CONTENTS;
				}
			}
		}
	}

	function GROUP_PARSE(TEXT, A, B, OFSET){
		let RESULT = "";
		let I = 0;
		for (I = OFSET; I < TEXT.length; I++) {
			const CHAR_ = TEXT[I];
			//(は飛ばす
			if(CHAR_ !== A){
				//)なら終わる
				if(CHAR_ === B){
					break;
				}
				//結果に追加する
				RESULT += CHAR_;
			}
		}

		return {
			CONTENTS:RESULT,
			OFSET:I
		};
	}
}