/**
 * まちカドまぞくのあれをあれするあれ
 */
import { SlashCommandBuilder } from "@discordjs/builders";
import * as FS from "node:fs";

export class machikado_mazoku_pic{
	static command = new SlashCommandBuilder().setName("machikado_mazoku_pic").setDescription("まちカドまぞくのイラストをPixivからランダムにだしますのだのあのだのあのんだ");

	/**
	 * @param {import("discord.js").CommandInteraction<import("discord.js").CacheType> | import("discord.js").ButtonInteraction<import("discord.js").CacheType>} INTERACTION
	 */
	constructor(INTERACTION) {
		this.E = INTERACTION;
	}

	async main(){
		console.log(await this.CATCHE());

		await this.E.editReply("作ってる");
	}

	//キャッシュのDATAはbody内を保存してます、errorを消してます注意しろよ未来の私
	async CATCHE(){
		try{
			if(FS.existsSync("./DOWNLOAD/MACHIKADO_MAZOKU_PIXIV_CACHE.json")){
				let F =FS.readFileSync("./DOWNLOAD/MACHIKADO_MAZOKU_PIXIV_CACHE.json", "UTF-8");
				F = JSON.parse(F.toString());
				let NOW_DATE = new Date();
				let CACHE_DATE = new Date(F.DATE);
				if(CACHE_DATE.getDate() !== NOW_DATE.getDate()){
					let RESULT = await this.GET_PIC();
					FS.writeFileSync("./DOWNLOAD/MACHIKADO_MAZOKU_PIXIV_CACHE.json", JSON.stringify({
						"DATE":new Date(),
						"DATA":RESULT["body"]
					}));

					return RESULT["body"];
				}else{
					return F["DATA"];
				}
			}else{
				let RESULT = await this.GET_PIC();
				FS.writeFileSync("./DOWNLOAD/MACHIKADO_MAZOKU_PIXIV_CACHE.json", JSON.stringify({
					"DATE":new Date(),
					"DATA":RESULT["body"]
				}));
				return RESULT["body"];
			}
		}catch(EX){
			console.error("[ ERR ][ MAZOKU_PIC.CATCHE ]" + EX);
		}
	}

	async GET_PIC(){
		console.log("[ *** ][ MAZOKU.AJAX ]PixivAPIに問い合わせています。。。");
		let AJAX = await fetch("https://www.pixiv.net/ajax/search/illustrations/%E3%81%BE%E3%81%A1%E3%82%AB%E3%83%89%E3%81%BE%E3%81%9E%E3%81%8F?word=%E3%81%BE%E3%81%A1%E3%82%AB%E3%83%89%E3%81%BE%E3%81%9E%E3%81%8F&order=date_d&mode=all&p=1&csw=0&s_mode=s_tag_full&type=illust_and_ugoira&lang=ja&version=6c38cc7c723c6ae8b0dc7022d497a1ee751824c0",{
			method:"GET",
			headers:{
				"Referer": "https://www.pixiv.net/tags/%E3%81%BE%E3%81%A1%E3%82%AB%E3%83%89%E3%81%BE%E3%81%9E%E3%81%8F/illustrations"
			}
		});

		if(AJAX.ok){
			let RESULT = await AJAX.json();

			console.log("[ OK ][ MAZOKU.AJAX ]PixivAPIが応答しました");

			return RESULT;
		}else{
			console.error("[ ERR ][ MAZOKU.AJAX ]PixivAPI、もしくはAJAXがエラーを吐きました；；");
			return undefined;
		}
	}
}