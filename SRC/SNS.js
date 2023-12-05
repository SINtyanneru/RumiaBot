import { CONFIG } from "./MODULES/CONFIG.js";
import { MessageEmbed } from "discord.js";
import { RND_COLOR } from "./MODULES/RND_COLOR.js";
import { MSG_SEND } from "./MODULES/MSG_SEND.js";
import { client } from "./MODULES/loadClient.js";
import { WebSocket } from "ws";
import { SQL_OBJ } from "./Main.js";
import { general_channel, rumiserver } from "./MODULES/SYNTAX_SUGER.js";

export class SNS {
	constructor() {
		this.USER = [];
	}

	//SQLを再読込するやつ
	SQL_RELOAD() {
		//VSCさ、勝手にコード変えるのまじでやめてくれん？
		SQL_OBJ.SCRIPT_RUN("SELECT * FROM `SNS`; ", [])
			.then(RESULT => {
				this.USER = RESULT;
			})
			.catch(EX => {
				console.error("[ OK ][ MISSKEY ]SQL ERR:" + EX);
			});
	}

	//ごちゃごちゃ
	main() {
		this.SQL_RELOAD();

		CONFIG.SNS.forEach(ROW => {
			switch (ROW.TYPE) {
				case "MISSKEY":
					this.misskey(ROW.DOMAIN, ROW.API, ROW.ID);
					break;
				case "MASTODON":
					this.mastodon(ROW.DOMAIN, ROW.API, ROW.ID);
					break;
			}
		});
	}

	SEND_EMBEDED(GID, CID, NOTE_URL, USER_NAME, USER_ID, NID, NOTE_TEXT, NOTE_FILES, RENOTE_USER_NAME, RID, RENOTE_TEXT, RENOTE_FILES) {
		const EB = new MessageEmbed();

		//ユーザー名
		EB.setTitle(USER_NAME);
		//色
		EB.setColor(RND_COLOR());
		//URL
		EB.setURL("https://ussr.rumiserver.com/@" + USER_ID);

		//本文
		EB.setDescription(NOTE_TEXT || "テキストなし");

		//ノートのファイル
		if (NOTE_FILES) {
			if (NOTE_FILES.length !== 0) {
				if (!NOTE_FILES[0].isSensitive) {
					EB.setImage(NOTE_FILES[0].thumbnailUrl);
				}
			}
		}

		//リノートがあるか
		if (RID) {
			EB.addFields({
				name: "リノート元\n" + RENOTE_USER_NAME,
				value: RENOTE_TEXT || "テキストなし",
				inline: false
			});

			//リノートじの画像
			if (NOTE_FILES) {
				if (NOTE_FILES.length === 0) {
					//既に画像が有るか
					//リノート元に画像は有るか
					if (RENOTE_FILES) {
						if (RENOTE_FILES.length !== 0) {
							if (!RENOTE_FILES[0].isSensitive) {
								EB.setImage(RENOTE_FILES[0].thumbnailUrl);
							}
						}
					}
				}
			}
		}

		// アクション
		EB.addFields({
			name: "ｱクション",
			value: "[見に行く](" + NOTE_URL + ")|" + "[何もしない](https://google.com)",
			inline: false
		});

		MSG_SEND(client, GID, CID, { embeds: [EB] });
	}

	misskey(DOMAIN, API_TOKEN, ID) {
		//WebSocketサーバーのURL
		const serverURL = "wss://" + DOMAIN + "/streaming?i=" + API_TOKEN;

		//WebSocket接続を作成
		const socket = new WebSocket(serverURL);

		//接続が確立された際のイベントハンドラ
		socket.on("open", () => {
			console.log("[ OK ][ MISSKEY ][ " + DOMAIN + " ]WS Connected!");

			//メッセージをサーバーに送信
			socket.send('{"type":"connect","body":{"channel":"localTimeline","id":"1","params":{"withReplies":false}}}');
		});

		//サーバーからメッセージを受信した際のイベントハンドラ
		socket.on("message", DATA => {
			try {
				const RESULT = JSON.parse(DATA);
				if (RESULT.body.type === "note") {
					//投稿者のID
					let IT_MIS_USER = RESULT.body.body.user;
					//SQLにそのIDがあるか探す
					let IT_DIS_USER = [];

					for (let I = 0; I < this.USER.length; I++) {
						const SNS_USER = this.USER[I];
						if (SNS_USER.SNS_UID === IT_MIS_USER.id && SNS_USER.SNS_ID === ID) {
							IT_DIS_USER.push(SNS_USER);
						}
					}

					//Ej! そのIDはあるか？？？
					if (IT_DIS_USER.length > 0) {
						let NOTE_ID = RESULT.body.body.id; //ノートのID
						let NOTE_TEXT = RESULT.body.body.text; //ノートのテキスト
						let NOTE_FILES = RESULT.body.body.files; //ノートのファイル
						let RENOTE_ID = RESULT.body.body.renoteId; //リノートのID
						let RENOTE_NOTE = RESULT.body.body.renote; //リノートのデータ

						console.log("[ INFO ][ MISSKEY ]Note res:" + NOTE_ID); //ログを吐く
						if (!RENOTE_ID) {
							IT_DIS_USER.forEach(ROW => {
								this.SEND_EMBEDED(
									ROW.GID,
									ROW.CID,
									"https://" + DOMAIN + "/notes/" + NOTE_ID,
									IT_MIS_USER.name,
									IT_MIS_USER.username,
									NOTE_ID,
									NOTE_TEXT,
									NOTE_FILES,
									null,
									null,
									null
								);
							});
						} else {
							IT_DIS_USER.forEach(ROW => {
								this.SEND_EMBEDED(
									ROW.GID,
									ROW.CID, "https://" + DOMAIN + "/notes/" + NOTE_ID,
									IT_MIS_USER.name,
									IT_MIS_USER.username,
									NOTE_ID,
									NOTE_TEXT,
									NOTE_FILES,
									RENOTE_NOTE.user.name,
									RENOTE_ID,
									RENOTE_NOTE.text,
									RENOTE_NOTE.files
								);
							});
						}
					}
				}
			} catch (EX) {
				console.error("[ ERR ][ MISSKEY ][ " + DOMAIN + " ]" + EX);
				return;
			}
		});

		//エラー発生時のイベントハンドラ
		socket.on("error", ERR => {
			console.error("エラーが発生しました:", ERR);
		});

		//接続が閉じられた際のイベントハンドラ
		socket.on("close", (CODE, REASON) => {
			console.log("[ INFO ][ MISSKEY ][ " + DOMAIN + " ]Disconnected!" + CODE + "REASON:" + REASON);
			clearInterval(SEND_H);
			setTimeout(() => {
				console.log("[ *** ][ MISSKEY ][ " + DOMAIN + " ]Re Connecting...");
				this.misskey(DOMAIN, API_TOKEN, ID); //再接続する
			}, 5000);
		});

		let SEND_H = setInterval(() => {
			socket.send("h");
		}, 60000);
	}

	mastodon(DOMAIN, API_TOKEN, ID) {
		//WebSocketサーバーのURL
		const serverURL = "wss://" + DOMAIN + "/api/v1/streaming?access_token=" + API_TOKEN;

		//WebSocket接続を作成
		const socket = new WebSocket(serverURL);

		//接続が確立された際のイベントハンドラ
		socket.on("open", () => {
			console.log("[ OK ][ MASTODON ][ " + DOMAIN + " ]WS Connected!");

			//メッセージをサーバーに送信
			socket.send('{"type":"subscribe","stream":"public:local"}');
		});

		//サーバーからメッセージを受信した際のイベントハンドラ
		socket.on("message", DATA => {
			try {
				const RESULT = JSON.parse(DATA);
				//トゥートされたら実行する
				if (RESULT.event === "update") {
					const TOOT = JSON.parse(RESULT.payload);
					if (TOOT.account.id) {
						console.log(TOOT);
						const FILES = (function () {
							if (TOOT.media_attachments.length > 0) {
								[{ thumbnailUrl: TOOT.media_attachments[0].preview_url }]
							} else {
								return [];
							}
						})();
						this.SEND_EMBEDED(
							rumiserver,
							general_channel,
							"https://" + DOMAIN + "/@" + TOOT.account.acct + "/" + TOOT.id,
							TOOT.account.display_name,
							TOOT.account.acct,
							TOOT.id,
							TOOT.content,
							FILES,
							null,
							null,
							null,
							null
						);
					}
				}
			} catch (EX) {
				console.error("[ ERR ][ MASTODON ][ " + DOMAIN + " ]" + EX);
				return;
			}
		});

		//エラー発生時のイベントハンドラ
		socket.on("error", ERR => {
			console.error("エラーが発生しました:", ERR);
		});

		//接続が閉じられた際のイベントハンドラ
		socket.on("close", (CODE, REASON) => {
			console.log("[ INFO ][ MASTODON ][ " + DOMAIN + " ]Disconnected!" + CODE + "REASON:" + REASON);

			setTimeout(() => {
				console.log("[ *** ][ MASTODON ][ " + DOMAIN + " ]Re Connecting...");
				this.mastodon(DOMAIN, API_TOKEN, ID); //再接続する
			}, 5000);
		});
	}
}
