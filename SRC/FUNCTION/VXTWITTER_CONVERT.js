import { WebHook_FIND } from "../MODULES/WebHook_FIND.js";
import { sanitize } from "../MODULES/sanitize.js";

export async function convert_vxtwitter(message) {
	const VX_REGEX = /https:\/\/(?:twitter|x)\.com\/([a-zA-Z0-9_]+\/status\/[0-9]+)(\?s=[0-9]*)?/g;
	let result = message.content.match(VX_REGEX);
	if (result) {
		result = result[0];
		let WEB_HOOK = await WebHook_FIND(message.channel);
		const TEXT = sanitize(message.content).replace(VX_REGEX, "https://vxtwitter.com/$1" /* [mobile](https://twitter.com/$1)"*/);

		//WHでめっせーじを送る
		WEB_HOOK.send({
			username: message.author.username,
			avatarURL: "https://cdn.discordapp.com/avatars/" + message.author.id + "/" + message.author.avatar + ".png",
			content: TEXT
		});

		//元メッセージを削除
		if (message.content) {
			message.delete();
		}
	}
}
