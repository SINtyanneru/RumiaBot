import { CONFIG } from "../MODULES/CONFIG.js";
export class IP {
	constructor(INTERACTION) {
		this.E = INTERACTION;
	}

	async main() {
		let E = this.E;
		if (CONFIG.ADMIN.DISABLE?.includes("ip")) {
			return E.editReply("運営者の意向により、開示できません！");
		}
		const RES = await fetch("https://ifconfig.me/ip", {
			method: "GET",
			headers: {
				"Content-Type": "application/json"
			}
		});

		if (RES.ok) {
			const RESULT = await RES.text();
			await E.editReply("私のIPは" + RESULT + "です！");
		} else {
			await E.editReply("取得失敗");
		}
	}
}
