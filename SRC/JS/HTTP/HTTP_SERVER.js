// @ts-check
import * as HTTP from "node:http";
import * as FS from "node:fs";
import { CONFIG } from "../MODULES/CONFIG.js";
import { SQL_OBJ } from "../Main.js";
import { PWS_SEND_MSG } from "../PROCESS_WS.js";

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
			 * /user
			 */
			if (REQ_PATH.startsWith("/user")) {
				/**
				 * API部分
				 */
				if (REQ_PATH.startsWith("/user/API")) {
					if(REQ_PATH.startsWith("/user/API/VERIFY_PANEL")){
						if(REQ.method === "POST"){
							let DATA = [];
							//POSTされたデータを受信する
							REQ.on("data", (CHUNK) => {
								// 受信したデータを配列に追加
								DATA.push(CHUNK);
							});

							//受信完了
							REQ.on("end", async () => {
								try{
									const POST_DATA = JSON.parse(Buffer.concat(DATA).toString());

									//TODO:戻す
									/*
									let AJAX = await fetch("https://challenges.cloudflare.com/turnstile/v0/siteverify", {
										method:"POST",
										headers:{
											"Content-Type":"application/json"//Content-Type入れないといけない系API死ね
										},
										body:JSON.stringify({
											"secret":/*CONFIG.CAPTCHA.SIKRET_KEY*//*"1x0000000000000000000000000000000AA",
											"response":POST_DATA.CFT_RESULT/*
										})
									});*/

									//const CFT_AJAX_RESULT = await AJAX.json();
									const CFT_AJAX_RESULT = {success:true};
									
									if(CFT_AJAX_RESULT.success){
										const JAVA_RESULT = await PWS_SEND_MSG("DISCORD;VERIFY_PANEL_OK;" + POST_DATA.PANEL_ID + ";" + POST_DATA.UID);
										
										if(JAVA_RESULT[0] === "200"){
											RES.statusCode = 200;
											RES.end(JSON.stringify({"STATUS":true}));
										} else {
											RES.statusCode = 400;
											RES.end(JSON.stringify({"STATUS":false, "MSG":"NOMAL_ERR"}));
										}
										return;
									}

									RES.statusCode = 400;
									RES.end(JSON.stringify({"STATUS":false, "MSG":"NOMAL_ERR"}));
								}catch(EX){
									console.error(EX);
									RES.statusCode = 500;
									RES.end(JSON.stringify({"STATUS":false, "MSG":"SYSTEM_ERR", "EX":EX}));
								}
							});

							//此のまま行くと404にまっしぐらなのでここで停止させる
							return;
						} else {
							RES.statusCode = 405;
							RES.end(JSON.stringify({"STATUS":false, "MSG":"POSTしろやカス"}));
							return;
						}
					}
				}
				//認証パネル
				if (REQ_PATH.startsWith("/user/verify_panel")) {
					let FILE = await this.LOAD_FILE("/user/verify_panel/index.html");

					//FILE.CONTENTS = FILE.CONTENTS.replace(/\$\{SITE_KEY\}/g, CONFIG.CAPTCHA.SITE_KEY);
					FILE.CONTENTS = FILE.CONTENTS.replace(/\$\{SITE_KEY\}/g, "1x00000000000000000000AA");
					//TODO:変える

					RES.statusCode = 200;
					RES.end(FILE.CONTENTS);
					return;
				}

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
			FS.readFile("./SRC/JS/HTTP/CONTENTS" + REQ_PATH, "utf8", (ERR, DATA) => {
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
