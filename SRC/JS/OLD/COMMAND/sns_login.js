import * as crypto from "node:crypto";
import { CONFIG } from "../../MODULES/CONFIG.js";
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
						content: "次のURLをクリックしてログインしてね！！！\nhttps://" + SNS_CONFIG.DOMAIN + "/miauth/" + UUID + "?name=るみさんBOT&icon=https://rumiserver.com/Asset/RUMI_BOT/db719e41bcea5ba6337fd109a06aa277.png&callback=https://rumiserver.com/rumiabot/login/misskey/" + SNS_CONFIG.ID + "/" + this.E.member.id + "&permission=read:account,write:notes,write:reactions,read:drive,write:drive",
						ephemeral: true
					});
				}
			}
		}catch(EX){
			await this.E.editReply("えらー");
		}
	}
}