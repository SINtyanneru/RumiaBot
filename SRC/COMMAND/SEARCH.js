import { MessageEmbed } from "discord.js";
import { RND_COLOR } from "../MODULES/RND_COLOR.js";
import { CONFIG } from "../MODULES/CONFIG.js";
import https from "https";
export class SEARCH {
	constructor(MSG, SEARCH_WORD) {
		this.E = MSG;
		this.SEARCH_WORD = SEARCH_WORD;

		this.DENIED_URL = ["pornhub.com", "xvideos.com", "eroterest.net"];
	}

	async main() {
		// リクエストのオプションを設定
		const OPTION = {
			hostname: "www.googleapis.com",
			path: "/customsearch/v1" + "?key=" + encodeURIComponent(CONFIG.GOOGLE_API_KEY) + "&cx=" + encodeURIComponent(CONFIG.GOOGLE_API_ENGINE_ID) + "&q=" + encodeURIComponent(this.SEARCH_WORD),
			method: "GET"
		};

		// リクエストを作成
		const REQ = https.request(OPTION, RES => {
			//レスポンスを受け取るためのコールバック
			let DATA = "";

			//レスポンスデータを受信したときのイベントハンドラ
			RES.on("data", CHUNK => {
				DATA += CHUNK;
			});

			//レスポンスデータをすべて受信したときのイベントハンドラ
			RES.on("end", async () => {
				try {
					const RESULT = JSON.parse(DATA);
					if (RESULT.error != null) {
						console.log("[ ERR ][ SEARCH ]", RESULT.error);
						this.E.reply("検索中にエラー:" + RESULT.error.code + "\n" + RESULT.error.message);
						return;
					}

					//埋め込み生成くん
					let EB = new MessageEmbed();
					EB.setTitle("「" + this.SEARCH_WORD + "」の検索結果");
					//EB.setDescription();
					EB.setColor(RND_COLOR());

					let WORD_DESC = null;

					for (let I = 0; I < RESULT.items.length; I++) {
						if (I > 5) {
							const SEARCH_DATA = RESULT.items[I];
							const SEARCH_RESULT_URL = new URL(SEARCH_DATA.link);

							let DENIED = false; //禁止URLか

							//禁止されているURLを回す
							this.DENIED_URL.forEach(ROW => {
								//禁止されているか？
								if (SEARCH_RESULT_URL.hostname.endsWith(ROW)) {
									//禁止されていることを伝える
									DENIED = true;
								}
							});

							if (!DENIED) {
								//禁止されていなければ
								//追加
								let TITLE = SEARCH_DATA.title;
								if (TITLE.length > 253) {
									TITLE = TITLE.slice(0, 256) + "...";
								}

								let DESC = SEARCH_DATA.snippet;
								if (DESC.length > 253) {
									DESC = DESC.slice(0, 256) + "...";
								}

								EB.addFields({
									name: TITLE,
									value: DESC + "\n[見に行く](" + SEARCH_DATA.link + ")",
									inline: false
								});


								if (!WORD_DESC) {
									WORD_DESC = DESC;
								}
							}
						}
					}

					//検索結果の説明を入れる
					if (WORD_DESC) {
						EB.setDescription("「" + this.SEARCH_WORD + "」とは↓\n" + WORD_DESC + "\n---------------------");
					}

					//返答
					await this.E.reply({ embeds: [EB] });
				} catch (EX) {
					console.error("[ ERR ][ SEARCH ]" + EX);
				}
			});
		});

		//エラーハンドリング
		REQ.on("error", ERR => {
			console.error("エラー:", ERR);
		});

		//リクエストを送信
		REQ.end();
	}
}
