/**
 * Misskey絵文字検索
 */
import { MessageEmbed } from "discord.js";
import { RND_COLOR } from "../MODULES/RND_COLOR.js";

let EMOJI_CACHE = undefined;

export class MISSKEY_EMOJI_SEARCH{
	constructor(INTERACTION) {
		this.E = INTERACTION;
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

			//キャッシュがなければつめる
			if(!EMOJI_CACHE){
				EMOJI_CACHE = {};
				EMOJI_CACHE["DATE"] = new Date();
				EMOJI_CACHE["ussr_rumiserver_com"] = RESULT.emojis;
			}

			//検索結果
			let EMOJI_SEARCH_RESULT = [];

			//検索する
			for (let I = 0; I < EMOJI_CACHE["ussr_rumiserver_com"].length; I++) {
				const EMOJI_DATA = EMOJI_CACHE["ussr_rumiserver_com"][I];
				if(EMOJI_DATA.name === EMOJI_NAME || EMOJI_DATA.aliases === EMOJI_NAME){
					EMOJI_SEARCH_RESULT.push(EMOJI_DATA);
				}
			}

			if(EMOJI_SEARCH_RESULT.length !== 0){
				let EMOJI_SEARCH_RESULT_EB = [];

				for (let I = 0; I < EMOJI_SEARCH_RESULT.length; I++) {
					const ROW = EMOJI_SEARCH_RESULT[I];

					const EB = new MessageEmbed();
					EB.setColor(RND_COLOR());
					EB.setTitle(":" + ROW.name + ":");
					EB.setDescription("るみすきー");
					EB.setImage(ROW.url);

					EMOJI_SEARCH_RESULT_EB.push(EB);
				}

				await this.E.editReply({ embeds: EMOJI_SEARCH_RESULT_EB });
			}else{
				await this.E.editReply("そんな絵文字は無い！");
			}
		}else{
			await this.E.editReply("MisskeyのAPIがエラーを吐きやがった！Бля！");
		}

	}
}