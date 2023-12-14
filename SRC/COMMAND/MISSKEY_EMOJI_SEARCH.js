/**
 * Misskey絵文字検索
 */
import { MessageEmbed } from "discord.js";
import { RND_COLOR } from "../MODULES/RND_COLOR.js";
import { CONFIG } from "../MODULES/CONFIG.js";

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
			for (let I = 0; I < CONFIG.SNS.length; I++) {//複数のインスタンスを回る
				const SNS = CONFIG.SNS[I];
				if(SNS.TYPE === "MISSKEY"){
					for (let I = 0; I < EMOJI_CACHE.DATA[SNS.DOMAIN.replace(".", "_")].length; I++) {
						const EMOJI_DATA = EMOJI_CACHE.DATA[SNS.DOMAIN.replace(".", "_")][I];
						if(EMOJI_DATA.name === EMOJI_NAME || EMOJI_DATA.aliases === EMOJI_NAME){
							EMOJI_SEARCH_RESULT.push(EMOJI_DATA);
						}
					}
				}
			}

			//検索結果を埋め込み化
			if(EMOJI_SEARCH_RESULT.length !== 0){
				let EMOJI_SEARCH_RESULT_EB = [];

				//検索結果は複数有る場合が有るのでfor
				for (let I = 0; I < EMOJI_SEARCH_RESULT.length; I++) {
					const ROW = EMOJI_SEARCH_RESULT[I];

					const EB = new MessageEmbed();
					EB.setColor(RND_COLOR());
					EB.setTitle(":" + ROW.name + ":");
					EB.setDescription("検索結果");
					EB.setImage(ROW.url);

					EMOJI_SEARCH_RESULT_EB.push(EB);
				}

				//返す
				await this.E.editReply({ embeds: EMOJI_SEARCH_RESULT_EB });
			}else{
				await this.E.editReply("そんな絵文字は無い！");
			}
		}catch(EX){
			console.log(EX);
			await this.E.editReply("DiscordAPIがエラーを吐きやがった！Бля！");
		}
	}

	//全インスタンスのAPIを叩く
	async API_RUN(){
		let API_RESULT = {};

		for (let I = 0; I < CONFIG.SNS.length; I++) {
			const SNS = CONFIG.SNS[I];
			if(SNS.TYPE === "MISSKEY"){
				const RES = await fetch("https://" + SNS.DOMAIN + "/api/emojis", {
					method: "GET",
					headers: {
						"Content-Type": "application/json"
					}
				});
		
				if (RES.ok) {
					const RESULT = await RES.json();
					API_RESULT[SNS.DOMAIN.replace(".", "_")] = RESULT.emojis;
				}
			}
		}

		return API_RESULT;
	}

	//キャッシュ
	async CACHE(){
		//キャッシュがなければつめる
		if(!EMOJI_CACHE){
			EMOJI_CACHE = {};
			EMOJI_CACHE["DATE"] = new Date();

			EMOJI_CACHE["DATA"] = await this.API_RUN();
			//返す
			return EMOJI_CACHE;
		}else{
			//5分経ったか
			if(EMOJI_CACHE["DATE"] - new Date() >= 5 * 60 * 1000){
				EMOJI_CACHE["DATE"] = new Date();

				EMOJI_CACHE["DATA"] = await this.API_RUN();

				//返す
				return EMOJI_CACHE;
			}else{//経ってない
				return EMOJI_CACHE;
			}
		}

	}
}