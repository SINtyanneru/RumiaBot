import { REST } from "@discordjs/rest";
import { Routes } from "discord-api-types/v10";
import * as FS from "node:fs";
import { RUMI_HAPPY_BIRTHDAY } from "./MODULES/RUMI_HAPPY_BIRTHDAY.js";
import { client } from "./MODULES/loadClient.js";
import { BOT_ADMIN } from "./BOT_ADMIN.js";
import { NULLCHECK } from "./MODULES/NULLCHECK.js";
import { CONFIG } from "./MODULES/CONFIG.js";
let ACTIVE = true;
import * as command from "./COMMAND/index.js";
import { MessageEmbed } from "discord.js";
import { RND_COLOR } from "./MODULES/RND_COLOR.js";
import { MSG_SEND } from "./MODULES/MSG_SEND.js";
import {
	rumiserver,
	rumi,
	hakurei_win,
	p_nsk,
	rumisub,
	makeitaquote,
	general_channel,
	exiter_channel
} from "./MODULES/SYNTAX_SUGER.js";
import { LOCK_NICK_NAME } from "./MODULES/LOCK_NICK_NAME.js";
import { calc } from "./FUNCTION/calc.js";
import { search } from "./FUNCTION/search.js";
import { convert_vxtwitter } from "./FUNCTION/VXTWITTER_CONVERT.js";
import { SQL } from "./SQL.js";
import { sanitize } from "./MODULES/sanitize.js";
import { SNS } from "./FUNCTION/SNS.js";
import { HTTP_STATUS_CODE } from "./MODULES/HTTP_STATUS_CODE.js";
import { FUNCTION_SETTING } from "./FUNCTION/FUNCTION_SETTING.js";
import * as PATH from "node:path";
import fetch from "node-fetch";
import { HTTP_SERVER } from "./HTTP/HTTP_SERVER.js";
import { WS_SERVER } from "./HTTP/WS_SERVER.js";
import { mcInfo, userInfo, serverInfo } from "./COMMAND/infocommand/index.js";
import { REGIST_SLASH_COMMAND } from "./REGIST_SL_COMMAND.js";
import { SHIOLI } from "./FUNCTION/SHIOLI.js";
import { GET_ALL_MEMBERS_COUNT } from "./MODULES/GET_ALL_GUILD_MEMBERS_COUNT.js";
import { URI_PARAM_DECODE } from "./MODULES/URI_PARAM_DECODE.js";
import { pws_main } from "./PROCESS_WS.js";
import { MSG_COMMAND } from "./MODULES/MSG_COMMAND.js";
import { GLOBAL_CHAT } from "./FUNCTION/GLOBAL_CHAT.js";

//Tef el Obzhekt iel zef klas lö peif ere;
//↑インスタンスのことですか？←Rumisan xēlp zo Obzhekt za;
const rest = new REST({ version: "10" }).setToken(CONFIG.DISCORD.TOKEN);
let HTTP_SERVER_OBJ = new HTTP_SERVER();
const WS_SERVER_OBJ = new WS_SERVER();
export const LOCK_NICK_NAME_OBJ = new LOCK_NICK_NAME();
export const SQL_OBJ = new SQL();
//何も存在しないなら
if (!(CONFIG.ADMIN.ADMIN_ID || CONFIG.ADMIN.ADMIN_PREFIX || CONFIG.DISCORD.TOKEN)) {
	throw new Error("深刻なエラー:設定が初期化されていないので、実行できません");
}
export const SNS_CONNECTION = new SNS();
export let FUNCTION_SETTING_OBJ = new FUNCTION_SETTING();

pws_main();

client.once("ready", async () => {
	//SNSのインスタンスに接続
	SNS_CONNECTION.main();

	//HTTP鯖を起動
	//HTTP_SERVER_OBJ.main();
	//WS鯖を起動
	//WS_SERVER_OBJ.main();

	LOCK_NICK_NAME_OBJ.INIT();

	//機能設定をロード
	FUNCTION_SETTING_OBJ.LOAD();

	/*
		console.log("⠀⠀⠀⠀⠀⠀⢀⣤⣀⣀⣀⠀⠻⣷⣄");
		console.log("⠀⠀⠀⠀⢀⣴⣿⣿⣿⡿⠋⠀⠀⠀⠹⣿⣦⡀");
		console.log("⠀⠀⢀⣴⣿⣿⣿⣿⣏⠀⠀⠀⠀⠀⠀⢹⣿⣧");
		console.log("⠀⠀⠙⢿⣿⡿⠋⠻⣿⣿⣦⡀⠀⠀⠀⢸⣿⣿⡆");
		console.log("⠀⠀⠀⠀⠉⠀⠀⠀⠈⠻⣿⣿⣦⡀⠀⢸⣿⣿⡇");
		console.log("⠀⠀⠀⠀⢀⣀⣄⡀⠀⠀⠈⠻⣿⣿⣶⣿⣿⣿⠁");
		console.log("⠀⠀⠀⣠⣿⣿⢿⣿⣶⣶⣶⣶⣾⣿⣿⣿⣿⡁");
		console.log("⢠⣶⣿⣿⠋⠀⠀⠉⠛⠿⠿⠿⠿⠿⠛⠻⣿⣿⣦⡀");
		console.log("⣿⣿⠟⠁⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠈⠻⣿⡿");
	*/

	const COMMAND_DATA = (await REGIST_SLASH_COMMAND()).map(c => c.toJSON()); //スラッシュコマンドのデータを取得する toJSONしないと送れないっぽい
	//取得したやつを登録する
	try {
		//グローバルスラッシュコマンドを登録
		await rest.put(Routes.applicationCommands(client.user.id), {
			body: COMMAND_DATA
		});
		console.log("[ OK ][ SLASH_COMMAND ]Al Komand wu registera!");
	} catch (EX) {
		console.error("[ ERR ][ SLASH_COMMAND ]" + EX);
	}

	//活動期間か？
	setInterval(() => {
		//現在の時刻を取得
		const currentTime = new Date();

		//時刻の取得
		const currentHour = currentTime.getHours();

		//判定
		if (currentHour >= 6 && currentHour < 21) {
			ACTIVE = true;
		} else {
			ACTIVE = false;
		}
	}, 1000);

	//参加しているすべてのサーバーのメンバー数
	const ALL_MEMBERS_COUNT = await GET_ALL_MEMBERS_COUNT(client);

	//プレセンツェを設定するやつ
	let TEMP_ACTIVE = undefined;
	setInterval(() => {
		if (TEMP_ACTIVE !== ACTIVE) {
			TEMP_ACTIVE = ACTIVE;
			if (ACTIVE) {
				//活動期間である				//活動期間である
				client.user.setPresence({
					status: "online",
					activities: [
						{
							name: ALL_MEMBERS_COUNT + "人",
							type: "WATCHING"
						}
					]
				});
			} else {
				//寝てる
				client.user.setPresence({
					status: "idle",
					activities: [
						{
							name: "睡眠",
							type: "PLAYING"
						}
					]
				});
			}
		}
	}, 1000);
});

//メッセージを受信
client.on("messageCreate", async message => {
	try {
		//ブロック
		if (CONFIG.ADMIN.BLOCK) {
			if (CONFIG.ADMIN.BLOCK.includes(message.author.id)) {
				return;
			}
		}

		//ログを出す
		try {
			let LOG_TEXT =
				"┌[" + message.author.username + "@" + message.guild.name + "/" + message.channel.name + "]\n";
			const LOG_TEXT_SPLIT = message.content.split("\n");
			for (let I = 0; I < LOG_TEXT_SPLIT.length; I++) {
				const TEXT = LOG_TEXT_SPLIT[I];
				if (LOG_TEXT_SPLIT[I + 1] !== undefined) {
					LOG_TEXT += "├" + TEXT + "\n";
				} else {
					LOG_TEXT += "└" + TEXT + "\n";
				}
			}
			console.log(LOG_TEXT);
		} catch (EX) {
			console.error("[ ERR ][ LOG ]Send LOG ERR" + EX);
			return;
		}

		new GLOBAL_CHAT(message);

		new MSG_COMMAND(message);

		//WSに流す
		for (let I = 0; I < WS_SERVER_OBJ.SOCKETS.length; I++) {
			const SOCKET = WS_SERVER_OBJ.SOCKETS[I];
			SOCKET.send(
				JSON.stringify({
					"TYPE": "MSG_RESOVE",
					"MSG": {
						"ID": message.id,
						"TEXT": message.content
					},
					"GUILD": {
						"ID": message.guild.id
					},
					"CHANNEL": {
						"ID": message.channel.id
					},
					"AUTHOR": {
						"ID": message.author.id,
						"NAME": message.author.username,
						"ICON": message.author.avatarURL(),
						"DEF_ICON": message.author.defaultAvatarURL
					}
				})
			);
		}

		//ニックネーム固定
		LOCK_NICK_NAME_OBJ.main(message.member);

		//BOT所有者専用のコマンド
		if (CONFIG.ADMIN.ADMIN_ID.find(ROW => ROW === message.author.id)) {
			await BOT_ADMIN(message);

			if(message.content === "お金"){
				try {
					let RES = await fetch("https://rumiserver.com/API/Rumisan/money.php", {
						method: "GET",
						headers: {
							"Content-Type": "application/json"
						}
					});
					if (RES.ok) {
						const RESULT = await RES.json();
						if (RESULT.STATUS) {
							let REGEX = new RegExp("\\B(?=(\\d{" + 4 + "})+(?!\\d))", "g");
							let TN_LIST_TEXT = "```diff\n";
		
							for (let I = 0; I < RESULT.TN_LIST.length; I++) {
								const TN = RESULT.TN_LIST[I];
								if(TN.V){
									TN_LIST_TEXT += "+ " + TN.DATE + "に" + TN.SITE + "から" + TN.MONEY + "円入金されました\n";
								}else{
									TN_LIST_TEXT += "- " + TN.DATE + "に" + TN.SITE + "で" + TN.MONEY + "円使いました\n";
								}
							}
		
							TN_LIST_TEXT += "```";
		
							message.reply("るみさんのお金は" + RESULT.MONEY.replace(REGEX, ",") + "円だよ\n" + TN_LIST_TEXT);
						} else {
							message.reply("エラー" + RESULT.ERR);
						}
					} else {
						message.reply("取得に失敗" + RES.status);
					}
				} catch (EX) {
					console.error(EX);
		
					message.reply("エラー");
				}
			}
		}

		//誕生月取得
		if (message.content === "誕生日") {
			//実験用
			await message.react("✅");

			await message.reply("るみさんの年齢は" + RUMI_HAPPY_BIRTHDAY());
		}

		//メンションされたユーザーのコレクションを取得
		const MENTION_USERS = message.mentions.users;

		if (!message.content.includes("@everyone") && !message.content.includes("@here")) {
			//メンションされたユーザーがいるかチェック
			if (MENTION_USERS.size > 0) {
				MENTION_USERS.forEach(async USER => {
					try{
						if (USER.id === client.user.id) {
							//自分に対するメッセージなら
							if (message.reference) {
								//リプライである
								//メッセージインフォ
								if (message.content.includes("info")) {
									let REPLY_P = await message.fetchReference();
									let FWH = await message.channel.fetchWebhooks();
									let WH = FWH.find(webhook => webhook.id === REPLY_P.author.id);
									await message.reply(
										"BOT:" +
											REPLY_P.author.bot +
											"\n" +
											"ID:" +
											REPLY_P.author.id +
											"\n" +
											"WH:" +
											(function () {
												if (WH) {
													return "NAME-" + WH.name + "/OWNER-" + WH.owner.username;
												} else {
													return "NONE";
												}
											})() + "\n" +
											"REMJA:" + 
												message.createdAt.getFullYear() + "G " +
												message.createdAt.getMonth() + "Y " +
												message.createdAt.getDate() + "S " +

												message.createdAt.getHours() + "Č " +
												message.createdAt.getMinutes() + "M " +
												message.createdAt.getSeconds() + "Sî " +
											"\n"
									);
									return;
								}
								//しもねた系
								if (
									message.content.includes("まんこ") ||
									message.content.includes("生理") ||
									message.content.includes("ちんこ")
								) {
									await message.reply("きもい");
									return;
								}
								//特定の人なら
								if (
									message.author.id === rumi ||
									message.author.id === hakurei_win ||
									message.author.id === p_nsk ||
									message.author.id === rumisub
								) {
									await message.reply("そーなのかー");
									return;
								}
								message.reply("おんこ");
							} else {
								//メッセージである
								//しもねた系
								if (
									message.content.includes("まんこ") ||
									message.content.includes("生理") ||
									message.content.includes("ちんこ")
								) {
									message.reply("きっしょ死ね");
									return;
								}
								//お → なに？
								if (message.content.replace("<@" + client.user.id + ">", "").endsWith("お")) {
									await message.reply("...");
									return;
								}
								//特定の人なら
								if (
									message.author.id === rumi ||
									message.author.id === hakurei_win ||
									message.author.id === p_nsk ||
									message.author.id === rumisub
								) {
									await message.reply("なんなのだー？");
									return;
								}
								await message.reply("なに？");
							}
						}
					}catch(EX){
						console.log("[ ERR ][ DJS ]" + EX);
					}
				});
			}
		}

		//検索
		if (message.content.startsWith("検索 ") || message.content.startsWith("検索　")) {
			if (!CONFIG.ADMIN.DISABLE?.includes("search")) {
				search(message);
			}
		}

		if (FUNCTION_SETTING_OBJ.SETTING.some(ROW => ROW.GID === message.guild.id && ROW.FUNC_ID === "vxtwitter")) {
			if (!CONFIG.ADMIN.DISABLE?.includes("vxtwitter")) {
				//vxtwitterのリンクに自動で置換する機能
				//もし実行しないと設定してるなら動かさない
				await convert_vxtwitter(message);
			}
		}
		//計算
		if (
			message.content.startsWith("計算 ") ||
			message.content.startsWith("計算　") ||
			message.content.startsWith("計算　")
		) {
			if (!CONFIG.ADMIN.DISABLE?.includes("calc")) {
				//もし実行しないと設定してるなら動かさない
				calc(message);
			}
		}

		if (message.content === "今日は何の日？") {
			message.react("✅");
			new command.WHAT_NOW_DAY().main(message);
		}

		if (message.content.startsWith("ルーレット")) {
			const CHOISE_LIST = message.content.replace("ルーレット ", "").split(",");
			const RANDOM = Math.floor(Math.random() * CHOISE_LIST.length);
			if (CHOISE_LIST[RANDOM]) {
				message.reply("結果：" + sanitize(CHOISE_LIST[RANDOM].toString()));
			}
		}

		if (message.content === "時間") {
			const DATE = new Date();
			const DAY_FORMAT = ["日", "月", "火", "水", "木", "金", "土"];
			const DATE_TEXT =
				DATE.getFullYear() +
				"年" +
				(DATE.getMonth() + 1) +
				"月" +
				DATE.getDate() +
				"日" +
				DAY_FORMAT[DATE.getDay()] +
				"曜日" +
				"\n" +
				DATE.getHours() +
				"時" +
				DATE.getMinutes() +
				"分" +
				DATE.getSeconds() +
				"秒" +
				DATE.getMilliseconds() +
				"ミリ秒";

			message.reply(DATE_TEXT);
		}

		if (message.content === "しおり登録") {
			new SHIOLI(message.guild.id, message.channel.id, message.id, message.author.id, message).SET();
		}

		if (message.content === "しおり") {
			new SHIOLI(message.guild.id, message.channel.id, message.id, message.author.id, message).LOAD();
		}

		if (message.guild.id === rumiserver) {
			if (!CONFIG.ADMIN.DISABLE?.includes("httpcat")) {
				const MATCH = message.content.match(/(?<!\d)\d{3}(?!\d)/);
				if (MATCH) {
					if (HTTP_STATUS_CODE.some(CODE => MATCH[0] === CODE)) {
						if (!message.author.bot) {
							message.channel.send({
								content: `http://http.cat/${MATCH[0]}`,
								flags: ["SUPPRESS_NOTIFICATIONS"]
							});
						}
					}
				}
			}
		}

		message.attachments
			.map(a => a)
			.forEach((attachment, key) => {
				const targetUrl = attachment.url;
				const fileExtension = attachment.name.match(/.[^.]+$/)?.[0];
				const targetPath = PATH.join("DOWNLOAD", "MSG_FILES", message.guildId);
				if (!FS.existsSync(targetPath)) {
					//ディレクトリが存在しない場合、作成
					FS.mkdirSync(targetPath, { recursive: true });
				}
				const FileStream = FS.createWriteStream(PATH.join(targetPath, message.id + "_" + key + fileExtension));
				console.info("[ *** ][ MSG_FILES ]Downloading…");
				fetch(targetUrl)
					.then(res => res.body.pipe(FileStream))
					.then(() => console.info("[ OK ][ MSG_FILES ]Downloaded"))
					.catch(error => console.error("[ ERR ][ MSG_FILES ]" + error));
			});
	} catch (EX) {
		console.error("[ ERR ][ DJS ]Error", EX);
	}
});

//メッセージが更新された
client.on("messageUpdate", (oldMessage, newMessage) => {
	//Make it a Quote を ダウンロード
	if (newMessage.author.id === makeitaquote) {
		if (newMessage.attachments.map(attachment => attachment.url).length > 0) {
			new command.MIQ().save_miq(newMessage);
		} else {
			new command.MIQ().load_miq(newMessage);
		}
	}
});

//スラッシュコマンド
client.on("interactionCreate", async INTERACTION => {
	try {
		//ブロック
		if (CONFIG.ADMIN.BLOCK) {
			if (CONFIG.ADMIN.BLOCK.includes(INTERACTION.user.id)) {
				//ブロックしてるので実行しない
				INTERACTION.reply({
					content:"お前嫌いだから実行しない",
					ephemeral: true
				});
				return;
			}
		}

		//インテラクションはスラッシュコマンドか
		if (!INTERACTION.isCommand()) {
			return;
		}

		console.log(
			"[ INFO ][ SL ]┌Interaction create:" +
				INTERACTION.commandName +
				"\n             ├in " +
				INTERACTION.guild.name +
				"\n             ├in " +
				INTERACTION.channel.name +
				INTERACTION.channelId +
				"\n             └in " +
				INTERACTION.member.user.username +
				"(" +
				INTERACTION.member.id +
				")"
		);
		const CMD = INTERACTION.commandName;

		/**
		 * でふぁーりぷらいしないやつら
		 */
		switch (CMD) {
			case "sns_login":
				await new command.sns_login(INTERACTION).main();
				return;
		}

		/**
		 * ユーザーに待ってもらうやつの処理
		 */
		await INTERACTION.deferReply();
		
		switch (CMD) {
			case "test":
				await new command.test(INTERACTION).main();
				return;
			case "help":
				await new command.HELP(INTERACTION).main();
				return;
			case "ping":
				await new command.PING(INTERACTION).main();
				return;
			case "ferris":
				await new command.FERRIS(INTERACTION).main();
				return;
			case "ws":
				await new command.WS(INTERACTION).main();
				return;
			case "info_server":
				serverInfo.getServerInfo(INTERACTION);
				return;
			case "info_user":
				userInfo.getUserInfo(INTERACTION);
				return;
			case "info_mine":
				mcInfo.getMcInfo(INTERACTION);
				return;
			case "kanji":
				await new command.KANJI(INTERACTION).main();
				return;
			case "letter":
				await new command.LETTER(INTERACTION).main();
				return;
			case "sns_set":
				await new command.SNS(INTERACTION).main();
				return;
			case "sns_remove":
				await new command.sns_remove(INTERACTION).main();
				return;
			case "ip":
				await new command.IP(INTERACTION).main();
				return;
			case "setting":
				await new command.SETTING(INTERACTION).SET();
				return;
			case "num":
				await new command.NUM(INTERACTION).main();
				return;
			case "wh_clear":
				await new command.WH_CLEAR(INTERACTION).main();
				return;
			case "vc_music":
				await new command.VC_MUSIC(INTERACTION).main();
				return;
			case "misskey_emoji_search":
				await new command.MISSKEY_EMOJI_SEARCH(INTERACTION).main();
				return;
			case "cp":
				await new command.Unicode_CODEPOINT(INTERACTION).main();
				return;
			case "mazokupic":
				await new command.mazokupic(INTERACTION).main();
				return;
			case "voicevox":
				await new command.voicevox(INTERACTION).main();
				return;
			case "global_chat_join":
				await new command.GLOBAL_CHAT_CMD(INTERACTION).JOIN();
				return;
			case "global_chat_left":
				await new command.GLOBAL_CHAT_CMD(INTERACTION).LEFT();
				return;
		}
	} catch (EX) {
		console.error("[ ERR ][ DJS ]スラッシュコマンドのインテラツティオンエラー");
		console.error(EX);
	}
});

//ボタンのインテラツティオン
client.on("interactionCreate", async INTERACTION => {
	try{
		//ブロック
		if (CONFIG.ADMIN.BLOCK) {
			if (CONFIG.ADMIN.BLOCK.includes(INTERACTION.user.id)) {
				//ブロックしてるので実行しない
				return await INTERACTION.reply({
					content:"お前嫌いだから実行しない",
					ephemeral: true
				});
			}
		}

		if (!INTERACTION.isButton()) {
			return;
		}

		const CMD = INTERACTION.customId.split("?")[0];
		let URI_PARAM = URI_PARAM_DECODE(INTERACTION.customId);

		console.log(
			"[ INFO ][ MI ]┌Interaction create:" +
				CMD +
				"\n             ├in " +
				INTERACTION.guild.name +
				"\n             ├in " +
				INTERACTION.channel.name +
				INTERACTION.channelId +
				"\n             └in " +
				INTERACTION.member.user.username +
				"(" +
				INTERACTION.member.id +
				")"
		);

		switch (CMD) {
			case "test":
				await new command.test(INTERACTION).button();
				break;
			case "sns_button_noteopen":
				await SNS_CONNECTION.note_open(INTERACTION, URI_PARAM);
				break;
		}
	}catch(EX){
		console.error("[ ERR ][ DJS ]ボタンのインテラツティオンエラー");
		console.error(EX);
		return;
	}
});

//鯖に参加した
client.on("guildCreate", async GUILD => {
	try {
		const LOG_CH = client.guilds.cache.get(rumiserver).channels.cache.get(general_channel);

		if (LOG_CH !== undefined) {
			LOG_CH.send(sanitize(GUILD.name) + "(" + GUILD.id + ")に参加しました");
		}

		const guildOwner = await GUILD.fetchOwner();

		//Send a DM to the guild owner
		const dmChannel = await guildOwner.createDM();
		await dmChannel.send("導入ありがと！よろしくね！");
		console.log("[ INFO ][ GUILD ]Send DM:" + guildOwner.nickname);
	} catch (EX) {
		const guildOwner = await GUILD.fetchOwner();
		console.error("[ ERR ][ GUILD ]Send DM:" + guildOwner.nickname, "[STACK]", EX.stack);
		return;
	}
});

//鯖からキックされた
client.on("guildDelete", GUILD => {
	try {
		const LOG_CH = client.guilds.cache.get(rumiserver).channels.cache.get(general_channel);

		if (LOG_CH !== undefined) {
			LOG_CH.send(sanitize(GUILD.name) + "(" + GUILD.id + ")から叩き出されました；；");

			const SERVERS = client.guilds.cache;

			LOG_CH.send(
				SERVERS.size + 1 + " са¯ва¯ вэдэне тащ ду¯ма;\n" + "Иф" + SERVERS.size + " са¯ва¯ вэдэне зад〜! Бля¯д!"
			);
		}
	} catch (EX) {
		console.error("[ ERR ][ GUILD ]Send MSG:" + EX);
		return;
	}
});

//メッセージが消された
client.on("messageDelete", async deletedMessage => {
	if (deletedMessage.author.bot && deletedMessage.webhookId !== null) {
		new command.MIQ().load_miq(deletedMessage);
	}
});

//メンバーが抜けた
client.on("guildMemberRemove", async member => {
	try {
		if (member.guild.id === rumiserver) {
			const EB = new MessageEmbed();
			EB.setTitle(NULLCHECK(member.displayName) + "が鯖から抜けたわ");
			EB.setDescription("彼は自分に私生活が有ることを証明してしまった");
			EB.setColor(RND_COLOR());
			MSG_SEND(client, rumiserver, exiter_channel, { embeds: [EB] });
		}
	} catch (EX) {
		console.error("[ ERR ][ AUTO BAN ]" + EX);
		return;
	}
});

//メンバーが入った
client.on("guildMemberAdd", member => {
	try {
		if (member.guild.id === rumiserver) {
			const EB = new MessageEmbed();
			EB.setTitle(NULLCHECK(member.displayName) + "さんようこそ地獄へ");
			EB.setDescription("だれだろう？");
			EB.setColor(RND_COLOR());
			MSG_SEND(client, rumiserver, "837971226222657536", { embeds: [EB] });
		}
	} catch (EX) {
		console.error("[ ERR ][ MEM ADD]" + EX);
		return;
	}
});

//メンバーの情報が更新された
client.on("guildMemberUpdate", (oldMember, newMember) => {
	//ニックネーム固定
	LOCK_NICK_NAME_OBJ.main(newMember);
});

//ロール変更を検知
client.on("roleUpdate", (oldRole, newRole) => {
	//るみさん鯖での活動ならうごかす
	if (newRole.guild.id === "836142496563068929") {
		const OLD_ROLE_PM = oldRole.permissions.toArray();
		const NEW_ROLE_PM = newRole.permissions.toArray();

		if (oldRole.name === newRole.name) {
			for (let I = 0; I < NEW_ROLE_PM.length; I++) {
				if(OLD_ROLE_PM[I] === NEW_ROLE_PM[I]){
					return;
				}
			}
		}


		const CH = client.guilds.cache.get(rumiserver).channels.cache.get(general_channel);
		if (CH) {
			let PM_UPDATE_LIST = [];
			//旧ロール設定からの比較
			OLD_ROLE_PM.forEach(OLD_PM => {
				if (NEW_ROLE_PM.find(PM => PM === OLD_PM)) {
					PM_UPDATE_LIST.push({
						NAME: OLD_PM,
						TYPE: "EX"
					});
				} else {
					PM_UPDATE_LIST.push({
						NAME: OLD_PM,
						TYPE: "LOST"
					});
				}
			});
			//新ロール設定からの比較
			NEW_ROLE_PM.forEach(OLD_PM => {
				if (!OLD_ROLE_PM.find(PM => PM === OLD_PM)) {
					PM_UPDATE_LIST.push({
						NAME: OLD_PM,
						TYPE: "NEW"
					});
				}
			});

			//diffは神です
			let PM_UPDATE_TEXT = "```diff\n";
			//文字化する
			for (let I = 0; I < PM_UPDATE_LIST.length; I++) {
				const PM_UPDATE = PM_UPDATE_LIST[I];
				if (PM_UPDATE.TYPE === "EX") {
					PM_UPDATE_TEXT += PM_UPDATE.NAME + "\n";
				} else if (PM_UPDATE.TYPE === "LOST") {
					PM_UPDATE_TEXT += "- " + PM_UPDATE.NAME + "\n";
				} else if (PM_UPDATE.TYPE === "NEW") {
					PM_UPDATE_TEXT += "+ " + PM_UPDATE.NAME + "\n";
				}
			}
			//THE END
			PM_UPDATE_TEXT += "```";

			//作った文字列をおくりつける
			if (oldRole.name === newRole.name) {
				//名前が同じ
				CH.send("「" + sanitize(newRole.name) + "」ロールの権限が変更されました\n" + sanitize(PM_UPDATE_TEXT));
			} else {
				CH.send(
					"「" +
						sanitize(oldRole.name) +
						"」→「" +
						sanitize(newRole.name) +
						"」ロールの権限が変更されました\n" +
						sanitize(PM_UPDATE_TEXT)
				);
			}
		}
	}
});

//BOTにログインする
client.login(CONFIG.DISCORD.TOKEN);