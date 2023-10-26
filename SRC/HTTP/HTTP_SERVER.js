import * as HTTP from "node:http";
import * as FS from "node:fs";
import { client } from "../MODULES/loadClient.js";

export class HTTP_SERVER {
	constructor() {
		this.PORT = 3000;
	}

	main() {
		const SERVER = HTTP.createServer((REQ, RES) => {
			let REQ_URI = REQ.url.split("?");
			let URI_PARAM = {};

			if(REQ_URI[1]){
				URI_PARAM = this.URI_PARAM_PARSE(REQ_URI[1]);
			}

			console.log(REQ_URI);

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
			if (REQ_URI[0].startsWith("/API")) {
				//鯖一覧
				if (REQ_URI[0] === "/API/GUILD_LIST_GET") {
					const GUILDS = client.guilds.cache;
					if (GUILDS) {
						let GUILDS_ARRAY = [];
						GUILDS.forEach(GUILD => {
							GUILDS_ARRAY.push({
								"ID": GUILD.id,
								"NAME": GUILD.name,
								"ICON_URL": GUILD.iconURL()
							});
						});
						RES.end(
							JSON.stringify({
								"STATUS": true,
								"GUILDS": GUILDS_ARRAY
							})
						);
					}
				}
				//チャンネル一覧
				if (REQ_URI[0] === "/API/CHANNEL_LIST_GET") {
					if(REQ.method === "GET"){
						if(URI_PARAM["ID"]){
							const GUILD = client.guilds.cache.get(URI_PARAM["ID"]);
							if (GUILD) {
								const CHANNELS = GUILD.channels.cache;
								if(CHANNELS.size > 0){
									let CHANNEL_ARRAY = [];

									CHANNELS.forEach(CHANNEL => {
										CHANNEL_ARRAY.push({
											"ID": CHANNEL.id,
											"NAME": CHANNEL.name,
											"TYPE": CHANNEL.type,
											"FORALDER": CHANNEL.parent,
											"POS": CHANNEL.position
										});
									});
									//成功
									RES.end(
										JSON.stringify({
											"STATUS": true,
											"CHANNELS": CHANNEL_ARRAY
										})
									);
								}
							}else{
								//エラー
								RES.end(
									JSON.stringify({
										"STATUS": false
									})
								);
							}
						}else{
							//エラー
							RES.end(
								JSON.stringify({
									"STATUS": false
								})
							);
						}
					}
				}
				//鯖の情報
				if (REQ_URI[0] === "/API/GUILD_INFO_GET") {
					if(REQ.method === "GET"){
						if(URI_PARAM["ID"]){
							const GUILD = client.guilds.cache.get(URI_PARAM["ID"]);
							if (GUILD) {
								//成功
								RES.end(
									JSON.stringify({
										"STATUS": true,
										"GUILD": {
											"ID": GUILD.id,
											"NAME": GUILD.name
										}
									})
								);
							}else{
								//エラー
								RES.end(
									JSON.stringify({
										"STATUS": false
									})
								);
							}
						}else{
							//エラー
							RES.end(
								JSON.stringify({
									"STATUS": false
								})
							);
						}
					}else{
						//エラー
						RES.end(
							JSON.stringify({
								"STATUS": false
							})
						);
					}
				}
				//チャンネルの情報
				if (REQ_URI[0] === "/API/CHANNEL_INFO_GET") {
					if(REQ.method === "GET"){
						if(URI_PARAM["GID"] && URI_PARAM["CID"]){
							const GUILD = client.guilds.cache.get(URI_PARAM["GID"]);
							if (GUILD) {
								const CHANNEL = GUILD.channels.cache.get(URI_PARAM["CID"]);
								if (CHANNEL) {
									//成功
									RES.end(
										JSON.stringify({
											"STATUS": true,
											"CHANNEL": {
												"ID": CHANNEL.id,
												"NAME": CHANNEL.name,
												"MESSAGES": []//TODO:メッセージ履歴をここに入れる
											}
										})
									);
								}else{
									//エラー
									RES.end(
										JSON.stringify({
											"STATUS": false
										})
									);
								}
							}else{
								//エラー
								RES.end(
									JSON.stringify({
										"STATUS": false
									})
								);
							}
						}else{
							//エラー
							RES.end(
								JSON.stringify({
									"STATUS": false
								})
							);
						}
					}else{
						//エラー
						RES.end(
							JSON.stringify({
								"STATUS": false
							})
						);
					}
				}

				//チャンネルの情報
				if (REQ_URI[0] === "/API/MSG_SEND") {
					if(REQ.method === "POST"){
						let POST_BODY = "";

						REQ.on("data", (chunk) => {
							POST_BODY += chunk.toString();
						});
					
						REQ.on("end", () => {
							const POST_RESULT = JSON.parse(POST_BODY);
							const GUILD = client.guilds.cache.get(POST_RESULT.GID);
							if(GUILD){
								const CHANNEL = GUILD.channels.cache.get(POST_RESULT.CID);
								if(CHANNEL){
									if(POST_RESULT.TEXT){
										CHANNEL.send(POST_RESULT.TEXT);
										RES.end(
											JSON.stringify({
												"STATUS": true
											})
										);
										return;
									}
								}
							}
							//エラー
							RES.end(
								JSON.stringify({
									"STATUS": false
								})
							);
						});
					}else{
						//エラー
						RES.end(
							JSON.stringify({
								"STATUS": false
							})
						);
					}
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

	URI_PARAM_PARSE(URI){
		const PARAMS = URI.split("&");

		let RESULT = {};

		for (let I = 0; I < PARAMS.length; I++) {
			const PARAM = PARAMS[I].split("=");
			RESULT[PARAM[0]] = PARAM[1];
		}

		return RESULT;
	}
}
