import { SlashCommandBuilder } from "@discordjs/builders";
import { CONFIG } from "../MODULES/CONFIG.js";
import { CommandInteraction, CacheType } from "discord.js";
export class IP {
	static command = new SlashCommandBuilder().setName("ip").setDescription("るみBOTのIPを表示します");
	E: CommandInteraction<CacheType>;
	constructor(INTERACTION: CommandInteraction<CacheType>) {
		this.E = INTERACTION;
	}

	async main() {
		const E = this.E;
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
