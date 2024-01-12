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
		let RESULT = await this.CATCHE();

		//Nullチェック
		if(RESULT){
			//乱数を生成する
			let RND = Math.floor(Math.random() * RESULT["illust"]["data"].length);

			//乱数の先にデータが有るか
			if(RESULT["illust"]["data"][RND]){
				//あるので返す
				await this.E.editReply("https://www.pixiv.net/artworks/" + RESULT["illust"]["data"][RND]["id"]);
			}else{//無いのでエラーを変えす
				await this.E.editReply("乱数生成中にエラーが発生");
			}
		}else{
			await this.E.editReply("キャッシュもしくはAJAXでエラーが発生しました");
		}
	}

	//キャッシュのDATAはbody内を保存してます、errorを消してます注意しろよ未来の私
	async CATCHE(){
		try{
			//キャッシュはあるか
			if(FS.existsSync("./DOWNLOAD/MACHIKADO_MAZOKU_PIXIV_CACHE.json")){
				let F =FS.readFileSync("./DOWNLOAD/MACHIKADO_MAZOKU_PIXIV_CACHE.json", "UTF-8");
				F = JSON.parse(F.toString());                   //ファイルの内容
				let NOW_DATE = new Date();                      //今日の日付
				let CACHE_DATE = new Date(F.DATE);              //キャッシュの日付
				//キャッシュの日付と今日の日付は違うか
				if(CACHE_DATE.getDate() !== NOW_DATE.getDate()){
					//違うので再取得して(ry
					let RESULT = await this.GET_PIC();
					FS.writeFileSync("./DOWNLOAD/MACHIKADO_MAZOKU_PIXIV_CACHE.json", JSON.stringify({
						"DATE":new Date(),
						"DATA":RESULT["body"]
					}));
					//取得した内容を返答する
					return RESULT["body"];
				}else{//同じなのでキャッシュの内容を返す
					return F["DATA"];
				}
			}else{//ないのでAPIで情報を取得する
				let RESULT = await this.GET_PIC();
				//取得した内容をキャッシュに書き込む
				FS.writeFileSync("./DOWNLOAD/MACHIKADO_MAZOKU_PIXIV_CACHE.json", JSON.stringify({
					"DATE":new Date(),
					"DATA":RESULT["body"]
				}));
				//取得した内容を返答する
				return RESULT["body"];
			}
		}catch(EX){
			console.error("[ ERR ][ MAZOKU_PIC.CATCHE ]" + EX);
			return undefined;
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