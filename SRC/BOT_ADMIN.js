import { CONFIG } from "./MODULES/CONFIG.js";
import { client } from "./MODULES/loadClient.js";
// eslint-disable-next-line no-unused-vars
import { MessageEmbed, Message } from "discord.js";
import { RND_COLOR } from "./MODULES/RND_COLOR.js";
import { exec, spawn } from "child_process";
import { NULLCHECK } from "./MODULES/NULLCHECK.js";
import { SQL_OBJ, LOCK_NICK_NAME_OBJ, SNS_CONNECTION } from "./Main.js";
import { MAP_KILLER } from "./MODULES/MAP_KILLER.js";
import { REON4213 } from "./MODULES/REON4213.js";

/**
 * BOT管理者が使う奴
 * @param {Message} message メッセージ
 */
export async function BOT_ADMIN(message) {
	let CMD_LIST = REON4213(message.content);
	if(CMD_LIST != null){
		for (let I = 0; I < CMD_LIST.length; I++) {
			const CMD = CMD_LIST[I];
			switch(CMD.A){
				case "EXEC":{
					console.log("任意コードを実行する:" + CMD.B);
					try {
						const result = JSON.stringify(eval(CMD.B));
						console.log("[ EVAL_RESULT ] ", result);
						if(result){
							if(result !== "{}"){
								message.reply(result);
							}
						}else{
							message.reply("内容が返されませんでした！！");
						}
					} catch (EX) {
						console.error(EX);
						message.reply("<:blod_sad:1155039115709005885> エラー: ```js\n" + EX.stack + "```");
					}

					return;
				}

				case "SHELL":{
					try {
						const SHELL_CMD = "bash";
						const ARGS = ["-c", CMD.B];
			
						let MSG = await message.channel.send("tailēd...");
						let CMD_OUTPUT = "";
			
						let INTER = setInterval(() => {
							MSG.edit("```ansi\n" + CMD_OUTPUT + "```");
						}, 1000);
						
						const CHILS_PROCESS = spawn(SHELL_CMD, ARGS);
						// 標準出力のデータがあるたびに呼び出されるイベントハンドラ
						CHILS_PROCESS.stdout.on("data", (DATA) => {
							CMD_OUTPUT += DATA.toString();
							if(CMD_OUTPUT.length > 1024){
								const EXCESS_LENGTH = CMD_OUTPUT.length - 1024;
								CMD_OUTPUT = CMD_OUTPUT.substring(EXCESS_LENGTH);
							}
						});
						//外部プロセスが終了したときに呼び出されるイベントハンドラ
						CHILS_PROCESS.on("close", (CODE) => {
							MSG.edit("```ansi\n" + CMD_OUTPUT + "```\nEND CODE:" + CODE.toString());
							clearInterval(INTER);
						});
					} catch (EX) {
						message.reply(EX);
					}
					return;
				}

				case "SYS":{
					if(CMD.B === "SLS"){
						message.reply("サーバー参加数：「" + client.guilds.cache.size + "」");
					}

					if(CMD.B === "SL"){
						const GUILDS = MAP_KILLER(await client.guilds.fetch());
						const GUILDS_KEY = Object.keys(GUILDS);
						let TEXT = "参加している鯖\n```";
				
						for (let I = 0; I < GUILDS_KEY.length; I++) {
							/** @type { import("discord.js").Guild } */
							const GUILD = GUILDS[GUILDS_KEY[I]];
							TEXT += (GUILD.name + "/" + GUILD.id) + "\n";
						}
				
						TEXT += "\n```";
				
						message.reply(TEXT);
					}

					if(CMD.B.split(":")[0] === "INV"){
						try {
							const GID = CMD.B.split(":")[1];
							let INV_CODE = await client.guilds.cache
								.get(GID)
								.channels.cache.find(ROW => ROW.type === "GUILD_TEXT")
								.createInvite();
				
							message.reply("https://discord.gg/" + NULLCHECK(INV_CODE.code));
						} catch (EX) {
							console.error(EX);
				
							message.reply("エラー");
						}
					}

					if(CMD.B.split(":")[0] === "LV"){
						try {
							const GID = CMD.B.split(":")[1];
							let GUILD = client.guilds.cache.get(GID);
							await GUILD.leave();
							message.reply("たぶん脱退した");
						} catch (EX) {
							console.error(EX);
				
							message.reply("エラー");
						}
					}

					if(CMD.B.split(":")[0] === "PM"){
						try {
							const GID = CMD.B.split(":")[1];
							let GUILD = client.guilds.cache.get(GID);
							if (GUILD.members.cache.get(client.user.id).permissions.has("ADMINISTRATOR")) {
								message.reply("はい、それは管理者権限的");
							} else {
								message.reply("あーー！管理者権限がないぞおおお！！！こんな鯖抜けてやる！");
							}
						} catch (EX) {
							console.error(EX);
				
							message.reply("エラー");
						}
					}

					if(CMD.B === "RELOAD"){
						console.log("[ *** ][ BOT ADMIN ]管理者が設定の再読込を要請しました、再読込を開始します...");

						//ニックネーム強制固定
						LOCK_NICK_NAME_OBJ.INIT();
						//SNS
						SNS_CONNECTION.SQL_RELOAD();
				
						message.reply("[ OK ]ｼｽﾃﾑの設定を再読込しました");
						console.log("[ *** ][ BOT ADMIN ]ｼｽﾃﾑの設定を再読込しました");
					}
					return;
				}
			}
		}
	}
}
