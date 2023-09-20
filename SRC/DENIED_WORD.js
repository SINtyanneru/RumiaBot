// eslint-disable-next-line no-unused-vars
import { Message } from "discord.js";
import moji from "moji";
import { rumiserver } from "./MODULES/SYNTAX_SUGER.js";
import { WebHook_FIND } from "./MODULES/WebHook_FIND.js";

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
			},
			{
				WORD: /lolbeans\.io/g,
				WHITE_LIST: [],
				WH:false
			}
		]
	};
	/**
	 * @param {Message} MESSAGE
	 */
	async main(MESSAGE) {
		try {
			if (MESSAGE.guild.id === rumiserver) {
				// HACK 合理的じゃないから後でなんとかして(しろ)
				const DWL = DENIED_WORD.DENIED_WORD_LIST[rumiserver];
				//投稿された鯖に、禁止ワードリストが登録されているか
				if (DWL) {
					const hiragana_content = moji(MESSAGE.content)
						.convert("KK", "HG")
						.toString(); /* カタカナをひらがなに */
					const ISDETECTED = DWL.find(ROW => ROW.WORD.test(hiragana_content));

					//禁止ワードだったか
					if (ISDETECTED) {
						//元メッセージを削除
						if (MESSAGE.content) {
							if(ISDETECTED.WH){
								let WEB_HOOK = await WebHook_FIND(MESSAGE.channel);
								let TEXT = this.OVERWRITE_REGEX_MATCH(MESSAGE.content, ISDETECTED.WORD, "○");

								//WHでめっせーじを送る
								WEB_HOOK.send({
									username: MESSAGE.author.username,
									avatarURL:
										"https://cdn.discordapp.com/avatars/" +
										MESSAGE.author.id +
										"/" +
										MESSAGE.author.avatar +
										".png",
									content: TEXT
								});
							}
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

	OVERWRITE_REGEX_MATCH(inputString, regexPattern, overwriteText) {
		return inputString.replace(regexPattern, (match) => {
			const middle = Math.floor(match.length / 2);
			const leftPart = match.substring(0, middle);
			const rightPart = match.substring(middle + overwriteText.length); // 上書きするテキストの長さ分を右側から削除
			return leftPart + overwriteText + rightPart;
		});
	}
}
