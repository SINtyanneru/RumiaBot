import { sanitize } from "../MODULES/sanitize.js";

export class WH_CLEAR {
	constructor(INTERACTION) {
		this.E = INTERACTION;
	}

	async main() {
		const PERM = this.E.channel.permissionsFor(this.E.member).has("MANAGE_MESSAGES");
		if (PERM) {
			let FWH = await this.E.channel.fetchWebhooks();

			FWH = Array.from(FWH);

			if (FWH.length > 0) {
				let TEXT = "";

				await this.E.editReply("このチャンネルのWebHookを削除しています。。。\n```" + TEXT.toString() + "");

				for (let I = 0; I < FWH.length; I++) {
					const WH = FWH[I][1];
					try {
						WH.delete();
						TEXT += "[ OK ]" + sanitize(WH.name) + "(" + WH.id + ")" + "\n";
						this.E.editReply("このチャンネルのWebHookを削除しています。。。\n```" + TEXT.toString() + "```");
					} catch (EX) {
						TEXT += "[ ERR ]" + sanitize(WH.name) + "(" + WH.id + ")" + "\n→" + EX + "\n";
						this.E.editReply("このチャンネルのWebHookを削除しています。。。\n```" + TEXT.toString() + "```");
					}
				}
			} else {
				this.E.editReply("このチャンネルにはWebHookがないです");
			}
		} else {
			this.E.editReply("お前には権限がありません、出直せ\nMANAGE_MESSAGES");
		}
	}
}
