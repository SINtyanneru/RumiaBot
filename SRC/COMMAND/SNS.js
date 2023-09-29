import { CONFIG } from "../MODULES/CONFIG.js";
import { RND_COLOR } from "../MODULES/RND_COLOR.js";
import { MessageEmbed } from "discord.js";
import { SQL_OBJ } from "../Main.js";

export class SNS {
	constructor(INTERACTION) {
		this.E = INTERACTION;
	}

	async main() {
		let E = this.E;
		const TYPE = E.options.getString("type");
		const USER_NAME = E.options.getString("username");

		//インスタンスの設定を取得
		let SNS_CONFIG = CONFIG.SNS.find(ROW => ROW.ID === TYPE);

		//設定があるか
		if (SNS_CONFIG) {
			//ある
			if (SNS_CONFIG.TYPE === "MISSKEY") {
				const RES = await fetch("https://" + SNS_CONFIG.DOMAIN + "/api/users/show", {
					method: "POST",
					headers: {
						"Content-Type": "application/json"
					},
					body: JSON.stringify({ username: USER_NAME })
				});

				if (RES.ok) {
					const RESULT = await RES.json();
					console.log(RESULT);

					//埋め込みつくるマン
					const EB = new MessageEmbed();
					EB.setTitle("このチャンネルに「" + RESULT.name + "」さんの投稿を垂れ流します");
					EB.setDescription("こっち見んな");
					EB.setColor(RND_COLOR());

					//既に登録されているかをチェック
					SQL_OBJ.SCRIPT_RUN("SELECT count(*) FROM `SNS` WHERE `SNS_ID` = ? AND `SNS_UID` = ?; ",
						[
							SNS_CONFIG.ID,
							RESULT.id,
						]
					).then(async (RESULT) => {
						if(RESULT[0]["count(*)"] === 0){
							//まだ未登録なので、登録する
							SQL_OBJ.SCRIPT_RUN(
								"INSERT INTO `SNS` (`ID`, `SNS_ID`, `SNS_UID`, `DID`) VALUES (NULL, ?, ?, ?);",
								[
									SNS_CONFIG.ID,
									RESULT.id,
									E.member.id
								]
							).then(async (RESULT) => {
								//成功
								await E.editReply({ embeds: [EB] });
							}).catch(async (EX) => {
								//エラー処理
								await E.editReply("エラー、SQLに登録出来ませんでした\n" + EX);
							});
						}else{//登録済みなので、登録しない
							await E.editReply("すでに登録されてます<:blod_sad:1155039115709005885>");
						}
					}).catch(async (EX) => {
						//エラー処理
						await E.editReply("エラー、SQLに登録出来ませんでした\n" + EX);
					});
					/*

					*/
				} else {
					if (RES.status === 404) {
						await E.editReply("指定されたアカウントは見つかりませんでした！");
					} else {
						await E.editReply("APIがエラーを吐きました");
					}
				}
			} else {
				await E.editReply("Misskeyしかまだ対応してない");
			}
		} else {
			//無い
			await E.editReply("インスタンスが見つかりませんでした");
		}
	}
}
