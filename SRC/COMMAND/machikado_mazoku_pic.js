/**
 * まちカドまぞくのあれをあれするあれ
 */
import { SlashCommandBuilder } from "@discordjs/builders";

export class machikado_mazoku_pic{
	static command = new SlashCommandBuilder().setName("machikado_mazoku_pic").setDescription("まちカドまぞくのイラストをPixivからランダムにだしますのだのあのだのあのんだ");

	/**
	 * @param {import("discord.js").CommandInteraction<import("discord.js").CacheType> | import("discord.js").ButtonInteraction<import("discord.js").CacheType>} INTERACTION
	 */
	constructor(INTERACTION) {
		this.E = INTERACTION;
	}

	async main(){
		let AJAX = await fetch("https://www.pixiv.net/ajax/search/illustrations/%E3%81%BE%E3%81%A1%E3%82%AB%E3%83%89%E3%81%BE%E3%81%9E%E3%81%8F?word=%E3%81%BE%E3%81%A1%E3%82%AB%E3%83%89%E3%81%BE%E3%81%9E%E3%81%8F&order=date_d&mode=all&p=1&csw=0&s_mode=s_tag_full&type=illust_and_ugoira&lang=ja&version=6c38cc7c723c6ae8b0dc7022d497a1ee751824c0",{
			method:"GET",
			headers:{
				"Referer": "https://www.pixiv.net/tags/%E3%81%BE%E3%81%A1%E3%82%AB%E3%83%89%E3%81%BE%E3%81%9E%E3%81%8F/illustrations"
			}
		});

		if(AJAX.ok){
			let RESULT = await AJAX.json();

			let RND = Math.floor(Math.random() * RESULT["body"]["illust"]["data"].length);

			if(RESULT["body"]["illust"]["data"][RND]){
				await this.E.editReply("https://www.pixiv.net/artworks/" + RESULT["body"]["illust"]["data"][RND]["id"]);
			}else{
				await this.E.editReply({
					content: "乱数生成中にエラーが発生"
				});
			}
		}else{
			await this.E.editReply({
				content: "AJAXエラー"
			});
		}
	}
}