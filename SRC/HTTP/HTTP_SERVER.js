// @ts-check
import * as HTTP from "node:http";
import * as FS from "node:fs";
import { client } from "../MODULES/loadClient.js";
import { CONFIG } from "../MODULES/CONFIG.js";
import { SQL_OBJ } from "../Main.js";

/**
 * @typedef {{ ID: string; NAME: string; TYPE: "GUILD_CATEGORY" | "GUILD_NEWS" | "GUILD_STAGE_VOICE" | "GUILD_STORE" | "GUILD_TEXT" | import("discord.js").ThreadChannelTypes | "GUILD_VOICE" | "GUILD_FORUM"; PARENT: import("discord.js").CategoryChannel | import("discord.js").NewsChannel | import("discord.js").ForumChannel | null; POS: any; }} CHANNEL

 */
export class HTTP_SERVER {
	constructor() {
		this.HOST_NAME = "0.0.0.0";
		this.PORT = 3000;
	}

	main() {
		const SERVER = HTTP.createServer();
		SERVER.on("request", async (REQ, RES) => {
			console.log(REQ.url);
			/**@param {{}} payload */
			function res_send_api(payload) {
				RES.end(JSON.stringify(payload));
			}

			/**
			 * @param {string} searchParam
			 */
			function parseSearchParams(searchParam) {
				/** @type {Object.<string, string>} */
				let retval = {};

				[...new URLSearchParams(searchParam).entries()].forEach(([name, value]) => {
					retval[name] = value;
				});
				return retval;
			}

			if (!REQ.url) throw "urlがない"; // 型チェック通過のため
			let [REQ_PATH, REQ_QUERY] = REQ.url.split("?");
			let URI_PARAM = REQ_QUERY ? parseSearchParams(REQ_QUERY) : {};

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
			if (REQ_PATH.startsWith("/API")) {
				//鯖一覧
				if (REQ_PATH === "/API/GUILD_LIST_GET") {
					const GUILDS = client.guilds.cache;
					if (GUILDS) {
						/**@type {{}[]} */
						let GUILDS_ARRAY = [];
						GUILDS.forEach((/** @type {{ id: any; name: any; iconURL: () => any; }} */ GUILD) => {
							GUILDS_ARRAY.push({
								"ID": GUILD.id,
								"NAME": GUILD.name,
								"ICON_URL": GUILD.iconURL()
							});
						});
						res_send_api({
							"STATUS": true,
							"GUILDS": GUILDS_ARRAY
						});
					}
				}
				//チャンネル一覧
				if (REQ_PATH === "/API/CHANNEL_LIST_GET") {
					if (REQ.method === "GET") {
						if (URI_PARAM["ID"]) {
							const GUILD = client.guilds.cache.get(URI_PARAM["ID"]);
							if (GUILD) {
								const CHANNELS = GUILD.channels.cache;
								if (CHANNELS.size > 0) {
									/**@type {CHANNEL[]} */
									let CHANNEL_ARRAY = [];

									CHANNELS.forEach(CHANNEL => {
										CHANNEL_ARRAY.push({
											"ID": CHANNEL.id,
											"NAME": CHANNEL.name,
											"TYPE": CHANNEL.type,
											// @ts-expect-error アサーションで着ないので静かにさせた
											"PARENT": CHANNEL.parent,
											// @ts-expect-error アサーションできない、だるすぎ侍
											"POS": CHANNEL.position || null
										});
									});
									//成功
									res_send_api({
										"STATUS": true,
										"CHANNELS": CHANNEL_ARRAY
									});
								}
							} else {
								//エラー
								res_send_api({
									"STATUS": false
								});
							}
						} else {
							//エラー
							res_send_api({
								"STATUS": false
							});
						}
					}
				}
				//鯖の情報
				if (REQ_PATH === "/API/GUILD_INFO_GET") {
					if (REQ.method === "GET") {
						if (URI_PARAM["ID"]) {
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
							} else {
								//エラー
								RES.end(
									JSON.stringify({
										"STATUS": false
									})
								);
							}
						} else {
							//エラー
							RES.end(
								JSON.stringify({
									"STATUS": false
								})
							);
						}
					} else {
						//エラー
						RES.end(
							JSON.stringify({
								"STATUS": false
							})
						);
					}
				}
				//チャンネルの情報
				if (REQ_PATH === "/API/CHANNEL_INFO_GET") {
					if (REQ.method === "GET") {
						if (URI_PARAM["GID"] && URI_PARAM["CID"]) {
							const GUILD = client.guilds.cache.get(URI_PARAM["GID"]);
							if (GUILD) {
								const CHANNEL = GUILD.channels.cache.get(URI_PARAM["CID"]);
								if (CHANNEL) {
									let MESSAGE_LOG = [];
									Array.from(await CHANNEL.messages.fetch({ "limit": 10 })).forEach(MESSAGE => {
										MESSAGE_LOG.push({
											"MSG": {
												"ID": MESSAGE[1].id,
												"TEXT": MESSAGE[1].content
											},
											"AUTHOR": {
												"ID": MESSAGE[1].author.id,
												"NAME": MESSAGE[1].author.username,
												"ICON": MESSAGE[1].author.avatarURL(),
												"DEF_ICON": MESSAGE[1].author.defaultAvatarURL
											}
										});
									});
									//成功
									RES.end(
										JSON.stringify({
											"STATUS": true,
											"CHANNEL": {
												"ID": CHANNEL.id,
												"NAME": CHANNEL.name,
												"MESSAGES": MESSAGE_LOG
											}
										})
									);
								} else {
									//エラー
									RES.end(
										JSON.stringify({
											"STATUS": false
										})
									);
								}
							} else {
								//エラー
								RES.end(
									JSON.stringify({
										"STATUS": false
									})
								);
							}
						} else {
							//エラー
							RES.end(
								JSON.stringify({
									"STATUS": false
								})
							);
						}
					} else {
						//エラー
						RES.end(
							JSON.stringify({
								"STATUS": false
							})
						);
					}
				}

				//チャンネルの情報
				if (REQ_PATH === "/API/MSG_SEND") {
					if (REQ.method === "POST") {
						let POST_BODY = "";

						REQ.on("data", chunk => {
							POST_BODY += chunk.toString();
						});

						REQ.on("end", () => {
							const POST_RESULT = JSON.parse(POST_BODY);
							const GUILD = client.guilds.cache.get(POST_RESULT.GID);
							if (GUILD) {
								/**@type { import("discord.js").TextChannel} */
								// @ts-expect-error アサーションが(ry
								const CHANNEL = GUILD.channels.cache.get(POST_RESULT.CID);
								if (CHANNEL) {
									if (POST_RESULT.TEXT) {
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
					} else {
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
			 * /user
			 */
			if (REQ_PATH.startsWith("/user")) {
				//ログイン
				if (REQ_PATH.startsWith("/user/login/misskey/")) {
					const TYPE = REQ_PATH.replace("/user/login/misskey/", "").split("/")[0];
					const DID = REQ_PATH.replace("/user/login/misskey/", "").split("/")[1];
					const UUID = URI_PARAM["session"];

					//インスタンスの設定を取得
					let SNS_CONFIG = CONFIG.SNS.find(ROW => ROW.ID === TYPE);

					//設定があるか
					if (SNS_CONFIG) {
						//ある
						if (SNS_CONFIG.TYPE === "MISSKEY") {
							let AJAX = await fetch("https://" + SNS_CONFIG.DOMAIN + "/api/miauth/" + UUID + "/check", {
								method: "POST"
							});

							if(AJAX.ok){
								let RESULT = await AJAX.json();
								if(RESULT.ok){
									try{
										let RESULT_SQL = await SQL_OBJ.SCRIPT_RUN("SELECT count(*) FROM `USER` WHERE `DID` = ?; ", [DID]);
										if (RESULT_SQL[0]["count(*)"] === 0) {
											await SQL_OBJ.SCRIPT_RUN("INSERT INTO `USER` (`ID`, `DID`, `NAME`, `SNS_TOKEN`) VALUES (NULL, ?, ?, ?);", [DID, "名無し", SNS_CONFIG.ID + "/" + RESULT.token]);
										}else{
											await SQL_OBJ.SCRIPT_RUN("UPDATE `USER` SET `SNS_TOKEN` = ? WHERE `USER`.`DID` = ?;", [SNS_CONFIG.ID + "/" + RESULT.token, DID]);
										}

										let FILE = await this.LOAD_FILE("/user/login/misskey.html");

										FILE.CONTENTS = FILE.CONTENTS.replace(/\$\{BANNER_URL\}/g, RESULT.user.bannerUrl);
										FILE.CONTENTS = FILE.CONTENTS.replace(/\$\{ICON_URL\}/g, RESULT.user.avatarUrl);
										FILE.CONTENTS = FILE.CONTENTS.replace(/\$\{USER_NAME\}/g, RESULT.user.name);

										RES.statusCode = 200;
										RES.end(FILE.CONTENTS);
										return;
									}catch(EX){
										console.error("[ ERR in Promise ][ SNS ]", EX);

										RES.statusCode = 500;
										RES.end("AJAXがNG吐いたわ！！！！：" + REQ.statusCode.toString());
										return;
									}
								}
							}else{
								RES.statusCode = 500;
								RES.end("AJAXがNG吐いたわ！！！！：" + REQ.statusCode.toString());
							}
						}
					}

					RES.statusCode = 500;
					RES.end("インスタンスが登録されていません");
					return;
				}

				RES.statusCode = 404;
				RES.end("ページがないかも");
				return;
			}

			/*
			 * HTMLとかのファイルを読み込んだり
			 */
			//レファラーがあればパスの前に入れる
			if (REQ.headers.referer) {
				const REFERAR = new URL(REQ.headers.referer).pathname;
				REQ_PATH = REFERAR + REQ_PATH;
			}
			//ファイルを読み込む
			let FILE = await this.LOAD_FILE(REQ_PATH);
			RES.statusCode = FILE.STATUS;
			RES.end(FILE.CONTENTS);
		});

		//サーバー起動
		SERVER.listen(this.PORT, this.HOST_NAME, () => {
			console.log("[ OK ][ HTTP ]HTTP Server runing. port " + this.PORT);
		});
	}

	LOAD_FILE(REQ_PATH){
		return new Promise((resolve) => {
			//ファイルを読み込む
			FS.readFile("./SRC/HTTP/CONTENTS" + REQ_PATH, "utf8", (ERR, DATA) => {
				if (ERR) {
					//ファイルがないのでindex.htmlが無いかをチェックする
					FS.readFile("./SRC/HTTP/CONTENTS/" + REQ_PATH + "/index.html", "utf8", (ERR, DATA) => {
						if (ERR) {
							//無いので死ぬ
							resolve({
								CONTENTS:"ファイルロード時にエラー",
								STATUS:404
							});
						} else {
							//ファイルの内容を返す
							resolve({
								CONTENTS:DATA,
								STATUS:200
							});
						}
					});
				} else {
					//ファイルの内容を返す
					resolve({
						CONTENTS:DATA,
						STATUS:200
					});
				}
			});
		});
	}
}
