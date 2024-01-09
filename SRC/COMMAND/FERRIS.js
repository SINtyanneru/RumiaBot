// @ts-check
import { SlashCommandBuilder } from "@discordjs/builders";
export class FERRIS {
	static command = new SlashCommandBuilder()
		.setName("ferris")
		.setDescription("ウニ？カニ？ヤドカリ？")
		.addStringOption(o =>
			o
				.setName("type")
				.setDescription("タイプ")
				.setChoices(
					{
						name: "コンパイルできません",
						value: "not_compile"
					},
					{
						name: "パニックします！",
						value: "panic"
					},
					{
						name: "アンセーフなコードを含みます",
						value: "un_safe"
					},
					{
						name: "求められた振る舞いをしません",
						value: "not_desired_behavior"
					}
				)
				.setRequired(true)
		);

	/**
	 * @param {import("discord.js").CommandInteraction<import("discord.js").CacheType>} INTERACTION
	 */
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
