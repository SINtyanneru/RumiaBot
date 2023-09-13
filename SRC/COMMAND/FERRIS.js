export class FERRIS {
	constructor(INTERACTION) {
		this.E = INTERACTION;
	}

	async main() {
		let E = this.E;
		switch (E.options.getString("type")) {
			case "not_compile":
				await E.editReply("https://rumiserver.com/Asset/RUMI_BOT/RES/does_not_compile.png");
				break;
			case "panic":
				await E.editReply("https://rumiserver.com/Asset/RUMI_BOT/RES/panics.png");
				break;
			case "un_safe":
				await E.editReply("https://rumiserver.com/Asset/RUMI_BOT/RES/unsafe.png");
				break;
			case "not_desired_behavior":
				await E.editReply("https://rumiserver.com/Asset/RUMI_BOT/RES/not_desired_behavior.png");
				break;
		}
	}
}
//DiscordJSの所為でファイルアップロードができんかった、まじでふざけんな
