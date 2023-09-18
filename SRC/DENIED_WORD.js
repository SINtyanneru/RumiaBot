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
				WHITE_LIST: []
			},
			{
				WORD: /(?:(?:チ|ち|千|テ|〒)(?:ン|ん|ソ)){2}/g,
				WHITE_LIST: []
			},
			{
				WORD: /まんこ|マンコ/g,
				WHITE_LIST: []
			},
			{
				WORD: /まんちん|マンチン/g,
				WHITE_LIST: []
			},
			{
				WORD: /BGA/g,
				WHITE_LIST: []
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
					// someは、Boolを返す、どれか一つがtrueを返したらtrueになる関数
					// 読んで理解できたら消してね
					// わからなかったら https://developer.mozilla.org/ja/docs/Web/JavaScript/Reference/Global_Objects/Array/some
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
