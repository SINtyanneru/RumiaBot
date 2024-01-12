// @ts-check
/**
 * まちカドまぞくのあれをあれするあれ
 */
import { SlashCommandBuilder } from "@discordjs/builders";
import { MessageEmbed } from "discord.js";
import * as FS from "node:fs";
import { RND_COLOR } from "../MODULES/RND_COLOR.js";

export class mazokupic{
	static command = new SlashCommandBuilder().setName("mazokupic").setDescription("まちカドまぞくのイラストをPixivからランダムにだしますのだのあのだのあのんだ");

	/**
	 * @param {import("discord.js").CommandInteraction<import("discord.js").CacheType> | import("discord.js").ButtonInteraction<import("discord.js").CacheType>} INTERACTION
	 */
	constructor(INTERACTION) {
		this.E = INTERACTION;

		//いつでも使えるように
		this.API_HEADER = {//これで満足かPixivAPIめ
			"Host": "i.pximg.net",
			"User-Agent": "Mozilla/5.0 (X11; Linux x86_64; rv:109.0;rumisan:16.0) Gecko/20100101 Firefox/115.0",
			"Accept": "text/html,application/xhtml+xml,application/xml;q=0.9,image/avif,image/jxl,image/webp,*/*;q=0.8",
			"Accept-Language": "ja,en-US;q=0.7,en;q=0.3",
			"Accept-Encoding": "gzip, deflate, br",
			"Referer": "https://www.pixiv.net/",
			"Connection": "keep-alive",
			"Upgrade-Insecure-Requests": "1",
			"Sec-Fetch-Dest": "document",
			"Sec-Fetch-Mode": "navigate",
			"Sec-Fetch-Site": "cross-site",
			"Pragma": "no-cache",
			"Cache-Control": "no-cache",
		};

		this.API_VERSION = "6c38cc7c723c6ae8b0dc7022d497a1ee751824c0";
	}

	async main(){
		let RESULT = await this.CATCHE();

		//Nullチェック
		if(RESULT){
			//乱数を生成する
			let RND = Math.floor(Math.random() * RESULT["illust"]["data"].length);

			//乱数の先にデータが有るか
			if(RESULT["illust"]["data"][RND]){//ある
				const ILLUST = RESULT["illust"]["data"][RND];
				const EB = new MessageEmbed();
				EB.setTitle(ILLUST["title"]);
				EB.setDescription("https://www.pixiv.net/artworks/" + ILLUST["id"]);
				EB.setColor(RND_COLOR());

				EB.setImage(`attachment://${ILLUST["id"]}.png`);

				let ILLUST_GET = await this.GET_ILLUST(ILLUST["id"]);
				if(ILLUST_GET){
					await this.E.editReply({ embeds: [EB], files: [`./DOWNLOAD/MAZOKUPIC/${ILLUST["id"]}.png`] });
				}else{
					await this.E.editReply("画像のダウンロードに失敗しました");
				}
			}else{
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
			if(FS.existsSync("./DOWNLOAD/MAZOKUPIC/MACHIKADO_MAZOKU_PIXIV_CACHE.json")){
				const F =FS.readFileSync("./DOWNLOAD/MAZOKUPIC/MACHIKADO_MAZOKU_PIXIV_CACHE.json", "utf-8");
				const F_D = JSON.parse(F.toString());                   //ファイルの内容
				let NOW_DATE = new Date();                      //今日の日付
				let CACHE_DATE = new Date(F_D.DATE);              //キャッシュの日付
				//キャッシュの日付と今日の日付は違うか
				if(CACHE_DATE.getDate() !== NOW_DATE.getDate()){
					//違うので再取得して(ry
					let RESULT = await this.GET_PIC();
					FS.writeFileSync("./DOWNLOAD/MAZOKUPIC/MACHIKADO_MAZOKU_PIXIV_CACHE.json", JSON.stringify({
						"DATE":new Date(),
						"DATA":RESULT["body"]
					}));
					//取得した内容を返答する
					return RESULT["body"];
				}else{//同じなのでキャッシュの内容を返す
					return F_D["DATA"];
				}
			}else{//ないのでAPIで情報を取得する
				let RESULT = await this.GET_PIC();
				//取得した内容をキャッシュに書き込む
				FS.writeFileSync("./DOWNLOAD/MAZOKUPIC/MACHIKADO_MAZOKU_PIXIV_CACHE.json", JSON.stringify({
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
		let AJAX = await fetch("https://www.pixiv.net/ajax/search/illustrations/%E3%81%BE%E3%81%A1%E3%82%AB%E3%83%89%E3%81%BE%E3%81%9E%E3%81%8F?word=%E3%81%BE%E3%81%A1%E3%82%AB%E3%83%89%E3%81%BE%E3%81%9E%E3%81%8F&order=date_d&mode=all&p=1&csw=0&s_mode=s_tag_full&type=illust_and_ugoira&lang=ja&version=" + this.API_VERSION,{
			method:"GET",
			headers:{
				"Referer": "https://www.pixiv.net/tags/%E3%81%BE%E3%81%A1%E3%82%AB%E3%83%89%E3%81%BE%E3%81%9E%E3%81%8F/illustrations"
			}
		});

		if(AJAX.ok){//成功
			let RESULT = await AJAX.json();

			console.log("[ OK ][ MAZOKU.AJAX ]PixivAPIが応答しました");

			return RESULT;
		}else{//失敗
			console.error("[ ERR ][ MAZOKU.AJAX ]PixivAPI、もしくはAJAXがエラーを吐きました；；");
			return undefined;
		}
	}

	/**
	 * @param {string} ID
	 */
	async GET_ILLUST(ID){
		//キャッシュが有るか
		if(FS.existsSync("./DOWNLOAD/MAZOKUPIC/" + ID + ".png")){
			//ある
			return true;
		}else{
			console.log("[ *** ][ MAZOKU.AJAX ]PixivAPIに問い合わせています。。。");
			let API_AJAX = await fetch(`https://www.pixiv.net/ajax/illust/${ID}?lang=ja&version=${this.API_VERSION}`,{
				method:"GET",
				headers:this.API_HEADER
			});

			if(API_AJAX.ok){
				console.log("[ OK ][ MAZOKU.AJAX ]PixivAPIが応答しました");
				let API_RESULT = await API_AJAX.json();

				//イラストのURL
				const ILLUST_URL = API_RESULT["body"]["urls"]["thumb"];

				//イラストをダウンロードする
				console.log("[ *** ][ MAZOKU.GET_ILLUST ]Pixivから画像ファイルをダウンロードしています:" + ILLUST_URL);

				let ILLUST_AJAX = await fetch(ILLUST_URL,{
					method:"GET",
					headers:this.API_HEADER
				});
	
				if(ILLUST_AJAX.ok){//成功
					let ILLUST_RESULT = new DataView(await (await ILLUST_AJAX.blob()).arrayBuffer());
	
					console.log("[ OK ][ MAZOKU.GET_ILLUST ]ダウンロード完了");
	
					FS.writeFileSync("./DOWNLOAD/MAZOKUPIC/" + ID + ".png", ILLUST_RESULT, "binary");
	
					return true;
				}else{//失敗
					console.error("[ ERR ][ MAZOKU.GET_ILLUST ]むりだー！|" + ILLUST_AJAX.status);
					return false;
				}
			}else{//失敗
				console.error("[ ERR ][ MAZOKU.AJAX ]PixivAPI、もしくはAJAXがエラーを吐きました；；");
				return false;
			}
		}
	}
}