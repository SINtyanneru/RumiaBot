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
		try{
			const EMOJI_NAME = this.E.options.getString("name");

			let EMOJI_CACHE = await this.CACHE();

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
		}catch(EX){
			await this.E.editReply("DiscordAPIがエラーを吐きやがった！Бля！");
		}
	}

	async API_RUN(DOMAIN){
		const RES = await fetch("https://" + DOMAIN + "/api/emojis", {
			method: "GET",
			headers: {
				"Content-Type": "application/json"
			}
		});

		if (RES.ok) {
			const RESULT = await RES.json();
			return RESULT;
		}else{
			return null;
		}
	}

	async CACHE(){
		//キャッシュがなければつめる
		if(!EMOJI_CACHE){
			EMOJI_CACHE = {};
			EMOJI_CACHE["DATE"] = new Date();

			let RESULT = await this.API_RUN("ussr.rumiserver.com");
			if(RESULT){//取得成功
				EMOJI_CACHE["ussr_rumiserver_com"] = RESULT.emojis;
			}

			//返す
			return EMOJI_CACHE;
		}else{
			//5分経ったか
			if(EMOJI_CACHE["DATE"] - new Date() >= 5 * 60 * 1000){
				EMOJI_CACHE["DATE"] = new Date();

				let RESULT = await this.API_RUN("ussr.rumiserver.com");
				if(RESULT){//取得成功
					EMOJI_CACHE["ussr_rumiserver_com"] = RESULT.emojis;
				}

				//返す
				return EMOJI_CACHE;
			}else{//経ってない
				return EMOJI_CACHE;
			}
		}

	}
}