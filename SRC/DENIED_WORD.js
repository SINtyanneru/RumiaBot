/**
 * 事実上機能停止
 */

// eslint-disable-next-line no-unused-vars
import { Message } from "discord.js";
import moji from "moji";
import { rumiserver } from "./MODULES/SYNTAX_SUGER.js";
import { WebHook_FIND } from "./MODULES/WebHook_FIND.js";
import { sanitize } from "./MODULES/sanitize.js";

export class DENIED_WORD {
	static DENIED_WORD_LIST = {
		[rumiserver]: [
			// るみサーバーにて
			{
				WORD: /(?:チ|ち|千|テ|〒)(?:ン|ん|ソ)(?:コ|こ|ポ|ぽ)/g,
				WHITE_LIST: [],
				WH: true
			},
			{
				WORD: /(?:(?:チ|ち|千|テ|〒)(?:ン|ん|ソ)){2}/g,
				WHITE_LIST: [],
				WH: true
			},
			{
				WORD: /まんこ|マンコ/g,
				WHITE_LIST: [],
				WH: true
			},
			{
				WORD: /まんちん|マンチン/g,
				WHITE_LIST: [],
				WH: true
			},
			{
				WORD: /BGA/g,
				WHITE_LIST: [],
				WH: true
			},
			{
				WORD: /lolbeans\.io/g,
				WHITE_LIST: [],
				WH: false
			},
			{
				WORD: /恒心/g,
				WHITE_LIST: ["1155798824472805476"],
				WH: false
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
					const hiragana_content = moji(MESSAGE.content).convert("KK", "HG").toString(); /* カタカナをひらがなに */
					//filterとかいうよくわからん関数で、禁止ワードを検知する
					const ISDETECTEDS = DWL.filter(ROW => ROW.WORD.test(hiragana_content));

					//禁止ワードがあったか
					if (ISDETECTEDS.length !== 0) {
						const WHITE_LIST_DETECT = ISDETECTEDS[0].WHITE_LIST.filter(CID => CID === MESSAGE.channel.id)[0];

						//ホワイトリストになければ処理する
						if (!(WHITE_LIST_DETECT === MESSAGE.channel.id)) {
							//元メッセージの文字列を入れる
							let TEXT = this.NULL_REP(MESSAGE.content);
							//検出された全ての禁止ワードを置き換える
							for (let I = 0; I < ISDETECTEDS.length; I++) {
								const ISDETECTED = ISDETECTEDS[I];
								TEXT = sanitize(this.OVERWRITE_REGEX_MATCH(TEXT, ISDETECTED.WORD, "○"));
							}

							//メッセージが有るか
							if (MESSAGE.content) {
								//元メッセージを消す
								MESSAGE.delete();

								//伏せ字にして再投稿するか
								if (ISDETECTEDS[0].WH) {
									//WHを準部
									let WEB_HOOK = await WebHook_FIND(MESSAGE.channel);
									//WHでメッセージを送る
									WEB_HOOK.send({
										username: MESSAGE.author.username,
										avatarURL: "https://cdn.discordapp.com/avatars/" + MESSAGE.author.id + "/" + MESSAGE.author.avatar + ".png",
										content: TEXT
									});
								}
							}
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
		return inputString.replaceAll(regexPattern, match => {
			const middle = Math.floor(match.length / 2);
			const leftPart = match.substring(0, middle);
			const rightPart = match.substring(middle + overwriteText.length); // 上書きするテキストの長さ分を右側から削除
			return leftPart + overwriteText + rightPart;
		});
	}

	NULL_REP(TEXT) {
		let RESULT = TEXT;
		RESULT.replaceAll("\u0000", "");
		RESULT.replaceAll("\u200C", "");
		RESULT.replaceAll("\u2061", "");
		return RESULT;
	}
}
