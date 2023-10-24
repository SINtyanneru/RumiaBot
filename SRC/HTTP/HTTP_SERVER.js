import * as HTTP from "node:http";
import * as FS from "node:fs";

export class HTTP_SERVER {
	constructor() {
		this.PORT = 3000;
	}

	main() {
		const SERVER = HTTP.createServer((REQ, RES) => {
			let REQ_URI = REQ.url.replaceAll("../", "");
			RES.statusCode = 200;

			/**
			 * ヘッダー関連
			 */
			if (REQ.url.endsWith(".css")) {
				RES.setHeader("Content-Type", "text/css; charset=utf-8");
			} else if (REQ.url.endsWith(".js")) {
				RES.setHeader("Content-Type", "text/javascript; charset=utf-8");
			} else if (REQ.url.startsWith("/API")) {
				RES.setHeader("Content-Type", "application/json; charset=utf-8");
			} else {
				RES.setHeader("Content-Type", "text/html; charset=utf-8");
			}

			/**
			 * API部分
			 */
			if (REQ.url.startsWith("/API")) {
				switch (REQ.url) {
					case "/API/GUILD_LIST_GET":
						RES.end(
							JSON.stringify({
								STATUS: true
							})
						);
						break;
				}

				//これ以上処理する必要がないので殺す
				return;
			}

			/**
			 * HTMLとかのファイルを読み込んだり
			 */
			//レファラーがあればパスの前に入れる
			if (REQ.headers.referer) {
				const REFERAR = new URL(REQ.headers.referer).pathname;
				REQ_URI = REFERAR + REQ_URI;
			}
			//ファイルを読み込む
			FS.readFile("./SRC/HTTP/CONTENTS" + REQ_URI, "utf8", (ERR, DATA) => {
				if (ERR) {
					//ファイルがないのでindex.htmlが無いかをチェックする
					FS.readFile("./SRC/HTTP/CONTENTS/" + REQ_URI + "/index.html", "utf8", (ERR, DATA) => {
						if (ERR) {
							//無いので死ぬ
							RES.statusCode = 404;
							RES.end("ファイルロード時にエラー");
						} else {
							//ファイルの内容を返す
							RES.end(DATA);
						}
					});
				} else {
					//ファイルの内容を返す
					RES.end(DATA);
				}
			});
		});

		//サーバー起動
		SERVER.listen(this.PORT, "127.0.0.1", () => {
			console.log("[ OK ][ HTTP ]HTTP Server runing. port " + this.PORT);
		});
	}
}
