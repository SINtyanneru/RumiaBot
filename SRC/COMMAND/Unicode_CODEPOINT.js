/**
 * Unicodeコードポイントを返すやつ
 */
import { MessageEmbed } from "discord.js";
import { RND_COLOR } from "../MODULES/RND_COLOR.js";
import { SlashCommandBuilder } from "@discordjs/builders";

export class Unicode_CODEPOINT{
	static command = new SlashCommandBuilder()
		.setName("cp")
		.setDescription("Unicodeコードポイントを出します")
		.addStringOption(o => o.setName("letter").setDescription("文字").setRequired(true));

	constructor(INTERACTION) {
		this.E = INTERACTION;

		this.LETTER_INFO = {
			"A":{
				DESC:"ラテン文字のA",
				ASCII:true,
				CATEGORY:"ラテン文字"
			},
			"a":{
				DESC:"ラテン文字のAの小文字",
				ASCII:true,
				CATEGORY:"ラテン文字"
			}
		};
	}

	async main() {
		const LETTER = this.E.options.getString("letter");
		if(LETTER.length === 1){
			//文字のUnicodeコードポイントを取得数r
			let Unioode_CODEPOINT = LETTER.charCodeAt(0);
			//それを16進法にする
			let Unioode_CODEPOINT_HEX = Unioode_CODEPOINT.toString(16).toUpperCase();

			const EB = new MessageEmbed();
			EB.setTitle(LETTER);
			if(this.LETTER_INFO[LETTER]){
				EB.setDescription(this.LETTER_INFO[LETTER].DESC);
				EB.addFields({
					name: "文字種",
					value: this.LETTER_INFO[LETTER].CATEGORY,
					inline: false
				});
			}
			EB.setColor(RND_COLOR());
			EB.addFields({
				name: "コードポイント",
				value: "U+" + Unioode_CODEPOINT_HEX.padStart(5, "0"),
				inline: false
			});
			
			await this.E.editReply({ embeds: [EB] });
		}else{
			await this.E.editReply("1文字のみです");
		}
	}
}