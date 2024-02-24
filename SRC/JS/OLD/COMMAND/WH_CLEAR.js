//@ts-check
import { SlashCommandBuilder } from "@discordjs/builders";
import { sanitize } from "../../MODULES/sanitize.js";
import { PermissionFlagsBits } from "discord-api-types/v10";
import { ThreadChannel } from "discord.js";

export class WH_CLEAR {
	static command = new SlashCommandBuilder()
		.setName("wh_clear")
		.setDescription("WHをすべてクリアします")
		.setDefaultMemberPermissions(PermissionFlagsBits.ManageMessages);
	/**
	 * @param {import("discord.js").CommandInteraction<import("discord.js").CacheType>} INTERACTION
	 */
	constructor(INTERACTION) {
		this.E = INTERACTION;
	}

	async main() {
		if (this.E.channel instanceof ThreadChannel) return;
		const FWH = await this.E.channel.fetchWebhooks();

		if (FWH.size > 0) {
			let TEXT = "";

			await this.E.editReply("このチャンネルのWebHookを削除しています。。。\n```" + TEXT.toString() + "");
			// prettier-ignore
			for (const [/*ESLintがキレるから空白 */, wh] of FWH) {
				try {
					wh.delete();
					TEXT += "[ OK ]" + sanitize(wh.name) + "(" + wh.id + ")" + "\n";
					this.E.editReply("このチャンネルのWebHookを削除しています。。。\n```" + TEXT.toString() + "```");
				} catch (error) {
					TEXT += "[ ERR ]" + sanitize(wh.name) + "(" + wh.id + ")" + "\n→" + error + "\n";
					this.E.editReply("このチャンネルのWebHookを削除しています。。。\n```" + TEXT.toString() + "```");
				}
			}
		} else {
			this.E.editReply("このチャンネルにはWebHookがないです");
		}
	}
}
