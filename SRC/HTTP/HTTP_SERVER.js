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

			if (REQ.url.endsWith(".css")) {
				RES.setHeader("Content-Type", "text/css; charset=utf-8");
			} else if (REQ.url.endsWith(".js")) {
				RES.setHeader("Content-Type", "text/javascript; charset=utf-8");
			} else if (REQ.url.startsWith("/API")) {
				RES.setHeader("Content-Type", "application/json; charset=utf-8");
			} else {
				RES.setHeader("Content-Type", "text/html; charset=utf-8");
			}

			if (REQ_URI.endsWith("/")) {
				REQ_URI += "index.html";
			}

			FS.readFile("./SRC/HTTP/CONTENTS/" + REQ_URI, "utf8", (ERR, DATA) => {
				if (ERR) {
					RES.statusCode = 404;
					RES.end("ファイルロード時にエラー");
				} else {
					RES.end(DATA);
				}
			});
		});

		SERVER.listen(this.PORT, "127.0.0.1", () => {
			console.log("[ OK ][ HTTP ]HTTP Server runing. port " + this.PORT);
		});
	}
}
