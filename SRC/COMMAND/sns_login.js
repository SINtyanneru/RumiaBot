import * as crypto from "node:crypto";
import { CONFIG } from "../MODULES/CONFIG.js";
//import { MessageActionRow, MessageButton } from "discord.js";

export class sns_login {
	constructor(INTERACTION) {
		this.E = INTERACTION;
	}

	async main() {
		try {
			const TYPE = this.E.options.getString("type");

			//インスタンスの設定を取得
			let SNS_CONFIG = CONFIG.SNS.find(ROW => ROW.ID === TYPE);

			//設定があるか
			if (SNS_CONFIG) {
				//ある
				if (SNS_CONFIG.TYPE === "MISSKEY") {
					const UUID = crypto.randomUUID();
					await this.E.reply({
						content: "次のURLをクリックしてログインしてね！！！\nhttps://" + SNS_CONFIG.DOMAIN + "/miauth/" + UUID + "?name=るみさんBOT&icon=https://rumiserver.com/Asset/RUMI_BOT/db719e41bcea5ba6337fd109a06aa277.png&permission=read:account",
						ephemeral: true
					});

					let TIMER = setInterval(async () => {
						let AJAX = await fetch("https://" + SNS_CONFIG.DOMAIN + "/api/miauth/" + UUID + "/check", {
							method: "POST"
						});

						if(AJAX.ok){
							let RESULT = await AJAX.json();
							if(RESULT.ok){
								clearInterval(TIMER);
							}
						}
					}, 5000);
				}
			}
		}catch(EX){
			await this.E.editReply("えらー");
		}
	}
}