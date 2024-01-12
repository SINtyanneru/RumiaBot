/**
 * VOICEVOXの動作確認用コマンド
 */
import { SlashCommandBuilder } from "@discordjs/builders";
import * as FS from "node:fs";

export class voicevox{
	static command = new SlashCommandBuilder().setName("voicevox").setDescription("ボイボのテスト用コマンド")
		.addStringOption(o => o.setName("text").setDescription("文章").setRequired(true))
		.addStringOption(o =>
			o
				.setName("speaker")
				.setDescription("誰")
				.setChoices(
					{
						name: "ずんだもん/ノーマル",
						value: "3"
					},
					{
						name: "ずんだもん/あまあま",
						value: "1"
					},
					{
						name: "四国めたん/ノーマル",
						value: "2"
					},
					{
						name: "四国めたん/あまあま",
						value: "0"
					},
					{
						name: "春日部つむぎ/ノーマル",
						value: "8"
					}
				)
				.setRequired(false)
		);

	/**
	 * @param {import("discord.js").CommandInteraction<import("discord.js").CacheType> | import("discord.js").ButtonInteraction<import("discord.js").CacheType>} INTERACTION
	 */
	constructor(INTERACTION){
		this.E = INTERACTION;

		//ID（時刻をbase64してる）
		this.ID = btoa(encodeURIComponent(new Date().toISOString()));

		//文章
		this.TEXT = INTERACTION.options.getString("text");

		//話者を選ぶ
		if(INTERACTION.options.getString("speaker")){
			this.SPEEK_ID = INTERACTION.options.getString("speaker");
		}else{//話者が指定されてないので初期値の3(ずんだもん/ノーマル)
			this.SPEEK_ID = "3";
		}
	}

	async main(){
		try{
			const QUERY = await this.GET_QUERY(this.TEXT);

			if(QUERY){
				let GENERATE_RESULT = await this.GENERATE(QUERY);
				if(GENERATE_RESULT){
					//生成結果をファイルを保存
					FS.writeFileSync(`./DOWNLOAD/VOICEVOX/${this.ID}.wav`, (GENERATE_RESULT), "binary");
	
					//生成結果を入れる
					await this.E.editReply({ content:"生成した", files: [
						`./DOWNLOAD/VOICEVOX/${this.ID}.wav`
					] });
	
					//終了
					return;
				}
			}
			//終了しないとここにくる
			await this.E.editReply({ content:"エラー"});
		}catch(EX){
			console.error("[ ERR ][ VOICEVOX ]" + EX);
			return;
		}
	}

	/**
	 * クエリを作成します
	 * @param {string} TEXT 
	 * @returns 
	 */
	async GET_QUERY(TEXT){
		console.log("[ *** ][ VOICEVOX ]VOICEVOXに問い合わせています。。。");
		let AJAX = await fetch(`http://localhost:50021/audio_query?text=${encodeURI(TEXT)}&speaker=${this.SPEEK_ID}`,{
			method:"POST",
			headers:{
				"Content-Type": "application/json"
			}
		});

		if(AJAX.ok){
			let QUERY = await AJAX.text();

			console.log("[ OK ][ VOICEVOX ]返答がありました" + QUERY);

			return QUERY;
		}else{
			console.error("[ ERR ][ VOICEVOX ]クエリを作成できませんでした");
			return undefined;
		}
	}

	/**
	 * クエリを元に音声を生成します
	 * @param {string} QUERY 
	 * @returns 
	 */
	async GENERATE(QUERY){
		console.log("[ *** ][ VOICEVOX ]VOICEVOXに問い合わせて音声を生成しています。。。");
		let AJAX = await fetch(`http://localhost:50021/synthesis?speaker=${this.SPEEK_ID}`,{
			method:"POST",
			headers:{
				"accept": "audio/wav",
				"Content-Type": "application/json"
			},
			body:QUERY
		});

		if(AJAX.ok){
			let RESULT = new DataView(await (await AJAX.blob()).arrayBuffer());

			console.log("[ OK ][ VOICEVOX ]生成しました");

			return RESULT;
		}else{
			console.error("[ ERR ][ VOICEVOX ]音声を生成できませんでした:" + JSON.stringify(await AJAX.json()));
			return undefined;
		}
	}
}