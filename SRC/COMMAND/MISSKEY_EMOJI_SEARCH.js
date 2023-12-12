/**
 * Misskey絵文字検索
 */
export class MISSKEY_EMOJI_SEARCH{
	constructor(INTERACTION) {
		this.E = INTERACTION;

		this.CACHE = {};
	}

	async main() {
		const EMOJI_NAME = this.E.options.getString("name");

		const RES = await fetch("https://ussr.rumiserver.com/api/emojis", {
			method: "GET",
			headers: {
				"Content-Type": "application/json"
			}
		});

		if (RES.ok) {
			const RESULT = await RES.json();
			console.log(RESULT);
		}else{
			await this.E.editReply("MisskeyのAPIがエラーを吐きやがった！Бля！");
		}

	}
}