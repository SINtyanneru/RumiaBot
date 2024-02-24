import { sanitize } from "./sanitize.js";
import * as command from "../OLD/COMMAND/index.js";
import { client } from "./loadClient.js";

export class MSG_COMMAND{
	constructor(message){
		this.message = message;

		this.MSG_COMMAND_LIST = {
			"test":{
				"OPTION":[
					/*
					{
						"NAME":"text",
						"REQ":true,
						"TYPE":"STRING"
					}*/
				],
				"FUNCTION":"test"
			},
			"mazokupic":{
				"OPTION":[],
				"FUNCTION":"mazokupic"
			}
		};

		if(this.message.content.startsWith("r.")){
			let PARSE_RESULT = this.PARSE();

			if(PARSE_RESULT != null){
				let OPTION_CHECK_RESULT = this.OPTION_CHECK(PARSE_RESULT);
				if(OPTION_CHECK_RESULT){
					if(command[this.MSG_COMMAND_LIST[PARSE_RESULT["NAME"]]["FUNCTION"]]){
						this.RUN_FUNC(PARSE_RESULT);
					}else{
						message.reply(sanitize(this.MSG_COMMAND_LIST[PARSE_RESULT["NAME"]]["FUNCTION"]) +  "というクラスがありません");
					}
				}else{
					//解析結果を吐く
					this.message.reply("オプションが足りません、[公式サイト](https://rumiserver.com/rumiabot/site/function)を確認してください");
				}
			}else{
				this.message.reply(sanitize(PARSE_RESULT["NAME"]) + "というコマンドはありません");
			}
		}
	}

	//スラッシュコマンドを解析
	PARSE(){
		try{
			const CMD_TEXT = this.message.content.replace("r.", "");              //コマンドのテキスト(当然r.は消している)
			const CMD_NAME = this.message.content.replace("r.", "").split(" ")[0];//コマンドの名前(CMD_TEXTの0番目)
			let CMD_OPTION = {};                                             //コマンドのオプションの配列
		
			//コマンドがあるか
			if(this.MSG_COMMAND_LIST[CMD_NAME]){
				const MSG_COMMAND_DATA = this.MSG_COMMAND_LIST[CMD_NAME];
		
				//メッセージコマンドを解析する
				(function(){
					let CMD_SPLIT = CMD_TEXT.split(" ");//コマンドを分解したもの(0番目はコマンドの名前)
		
					//分解したものを回す
					for (let I = 0; I < CMD_SPLIT.length; I++) {
						const ROW = CMD_SPLIT[I];
		
						//その行に設定されたオプションのデータはあるか
						if(MSG_COMMAND_DATA["OPTION"][I]){
							let OPTION_DATA = MSG_COMMAND_DATA["OPTION"][I];//オプションの設定されたデータ
		
							//オプションの名前で配列に追加する
							CMD_OPTION[OPTION_DATA["NAME"]] = ROW;
						}
					}
				})();
			}else{//コマンドがない
				return null;
			}

			return {
				"NAME":CMD_NAME,
				"OPTION":CMD_OPTION
			};
		}catch(EX){
			console.log(EX);
		}
	}

	//オプションが揃ってるかのチェック
	OPTION_CHECK(PARSE_RESULT){
		const OPTION_DATA = this.MSG_COMMAND_LIST[PARSE_RESULT["NAME"]];
		if(OPTION_DATA){
			for (let I = 0; I < OPTION_DATA.length; I++) {
				const DATA = OPTION_DATA[I];
				if(!PARSE_RESULT["OPTION"][DATA["NAME"]]){
					return false;
				}
			}

			return true;
		}else{
			return false;
		}
	}

	async RUN_FUNC(PARSE_RESULT){
		const REPLY_MSG = await this.message.reply("るみさんBOTが考えています");

		//インテラクションを作成
		const INTERACTION = {
			editReply:async function(SETTING){
				const GUILD = await client.guilds.fetch(REPLY_MSG.guild.id);
				/** @type { import("discord.js").TextChannel } */
				const CHANNEL = await GUILD.channels.fetch(REPLY_MSG.channel.id);
				const MESSAGE = await CHANNEL.messages.fetch(REPLY_MSG.id);
				MESSAGE.edit(SETTING);
			}
		};

		//設定された関数を実行する
		new command[this.MSG_COMMAND_LIST[PARSE_RESULT["NAME"]]["FUNCTION"]](INTERACTION).main();
	}
}