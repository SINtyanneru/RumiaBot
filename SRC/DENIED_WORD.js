// eslint-disable-next-line no-unused-vars
import { Message } from "discord.js";
import moji from "moji";
import { rumiserver } from "./MODULES/SYNTAX_SUGER.js";
export class DENIED_WORD {
	static DENIED_WORD_LIST = {
		"836142496563068929": [
			// るみサーバーにて
			{
				WORD: /(?:チ|ち|千|テ|〒)(?:ン|ん|ソ)(?:コ|こ|ポ|ぽ)/g,
				WHITE_LIST: [],
				WH:true
			},
			{
				WORD: /(?:(?:チ|ち|千|テ|〒)(?:ン|ん|ソ)){2}/g,
				WHITE_LIST: [],
				WH:true
			},
			{
				WORD: /まんこ|マンコ/g,
				WHITE_LIST: [],
				WH:true
			},
			{
				WORD: /まんちん|マンチン/g,
				WHITE_LIST: [],
				WH:true
			},
			{
				WORD: /BGA/g,
				WHITE_LIST: [],
				WH:true
			}
		]
	};
	/**
	 * @param {Message} MESSAGE
	 */
	main(MESSAGE) {
		try {
			if (MESSAGE.guild.id === rumiserver) {
				// HACK 合理的じゃないから後でなんとかして(しろ)
				const DWL = DENIED_WORD.DENIED_WORD_LIST[rumiserver];
				//投稿された鯖に、禁止ワードリストが登録されているか
				if (DWL) {
					const hiragana_content = moji(MESSAGE.content)
						.convert("KK", "HG")
						.toString(); /* カタカナをひらがなに */
					const ISDETECTED = DWL.some(ROW => ROW.WORD.test(hiragana_content));

					//禁止ワードだったか
					if (ISDETECTED) {
						//元メッセージを削除
						if (MESSAGE.content) {
							MESSAGE.delete();
						}
					}
				}
			}
		} catch (EX) {
			console.log("[ ERR ][ DEN_WORD ]" + EX);
			return;
		}
	}
}
