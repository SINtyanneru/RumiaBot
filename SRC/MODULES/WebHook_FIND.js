import { CONFIG } from "./CONFIG.js";

export async function WebHook_FIND(CHANNEL) {
	let FWH = await CHANNEL.fetchWebhooks();
	let WH = FWH.find(webhook => webhook.owner.id === CONFIG.ID);
	if (WH) {
		return WH;
	} else {
		let NEW_WH = CHANNEL.createWebhook("るみBOT");
		return NEW_WH;
	}
}
