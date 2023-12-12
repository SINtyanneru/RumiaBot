export class MISSKEY_EMOJI_SEARCH{
	constructor(INTERACTION) {
		this.E = INTERACTION;
	}

	async main() {
		const EMOJI_NAME = this.E.options.getString("name");

		await this.E.editReply(EMOJI_NAME);
	}
}