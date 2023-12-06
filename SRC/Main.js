import * as FS from "node:fs";
import * as net from "node:net";
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
import { rumiserver, rumi, hakurei_win, p_nsk, rumisub, makeitaquote, general_channel, exiter_channel } from "./MODULES/SYNTAX_SUGER.js";
import { LOCK_NICK_NAME } from "./MODULES/LOCK_NICK_NAME.js";
import { calc } from "./MODULES/calc.js";
import { search } from "./MODULES/search.js";
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
import { getMcInfo, getServerInfo, getUserInfo } from "./COMMAND/INFO.js";
import { REGIST_SLASH_COMMAND } from "./REGIST_SL_COMMAND.js";
import { GET_ALL_MEMBERS_COUNT } from "./MODULES/GET_ALL_GUILD_MEMBERS_COUNT.js";

//ここに、オブジェクトとして置いておくべき、クラスを、置くよ。
// ↑インスタンスのことですか？←るみさん用語でオブジェクトです
let HTTP_SERVER_OBJ = new HTTP_SERVER();
const WS_SERVER_OBJ = new WS_SERVER();
export const LOCK_NICK_NAME_OBJ = new LOCK_NICK_NAME();
export const SQL_OBJ = new SQL();
// 何も存在しないなら
if (!(CONFIG.ADMIN_ID || CONFIG.ADMIN_PREFIX || CONFIG.ID || CONFIG.TOKEN)) {
	throw new Error("深刻なエラー:設定が初期化されていないので、実行できません");
}
export const SNS_CONNECTION = new SNS();

client.once("ready", async () => {
	console.log("    ____                  _       ____  ____  ______");
	console.log("   / __ \\__  ______ ___  (_)___ _/ __ )/ __ \\/_  __/");
	console.log("  / /_/ / / / / __ `__ \\/ / __ `/ __  / / / / / /   ");
	console.log(" / _, _/ /_/ / / / / / / / /_/ / /_/ / /_/ / / /    ");
	console.log("/_/ |_|\\__,_/_/ /_/ /_/_/\\__,_/_____/\\____/ /_/     ");
	console.log("V1.1");

	//SNSのインスタンスに接続
	SNS_CONNECTION.main();

	//HTTP鯖を起動
	HTTP_SERVER_OBJ.main();
	//WS鯖を起動

	LOCK_NICK_NAME_OBJ.INIT();

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


	/**@type {import("discord.js").ApplicationCommandData[]} */
	const COMMAND_DATA = REGIST_SLASH_COMMAND();//スラッシュコマンドのデータを取得する
	//取得したやつを登録する
	try {
		//グローバルスラッシュコマンドを登録
		await client.application.commands.set(COMMAND_DATA);
		console.log("[ OK ][ SLASH_COMMAND ]Al Komand wu registera!");
	} catch (EX) {
		console.log("[ ERR ][ SLASH_COMMAND ]" + EX);
	}

	// 活動期間か？
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
	const ALL_MEMBERS_COUNT = GET_ALL_MEMBERS_COUNT(client);

	//プレセンツェを設定するやつ
	let TEMP_ACTIVE = undefined;
	setInterval(() => {
		if (TEMP_ACTIVE !== ACTIVE) {
			TEMP_ACTIVE = ACTIVE;
			if (ACTIVE) {
				//活動期間である
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
	//ブロック
	if (CONFIG.BLOCK) {
		if (CONFIG.BLOCK.includes(message.author.id)) {
			return;
		}
	}

	//ログを出す
	try {
		let LOG_TEXT = "┌[" + message.author.username + "@" + message.guild.name + "/" + message.channel.name + "]\n";
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

	/*
	//BOTの場合は処理しない
	if(message.author.bot){
		return;
	}
	*/

	//BOT所有者専用のコマンド
	if (CONFIG.ADMIN_ID.find(ROW => ROW === message.author.id)) {
		await BOT_ADMIN(message);
	}

	//誕生月取得
	if (message.content === "誕生日") {
		//実験用
		message.react("✅");

		message.reply("るみさんの年齢は" + RUMI_HAPPY_BIRTHDAY());
	}

	//メンションされたユーザーのコレクションを取得
	const MENTION_USERS = message.mentions.users;

	if (!message.content.includes("@everyone") && !message.content.includes("@here")) {
		//メンションされたユーザーがいるかチェック
		if (MENTION_USERS.size > 0) {
			MENTION_USERS.forEach(async USER => {
				if (USER.id === client.user.id) {
					//自分に対するメッセージなら
					if (message.reference) {
						//リプライである
						//メッセージインフォ
						if (message.content.includes("taktud")) {
							let REPLY_P = await message.fetchReference();
							let FWH = await message.channel.fetchWebhooks();
							let WH = FWH.find(webhook => webhook.id === REPLY_P.author.id);
							message.reply(
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
									})() +
									"\n"
							);
							return;
						}
						//しもねた系
						if (message.content.includes("まんこ") || message.content.includes("生理") || message.content.includes("ちんこ")) {
							message.reply("きもい");
							return;
						}
						//特定の人なら
						if (message.author.id === rumi || message.author.id === hakurei_win || message.author.id === p_nsk || message.author.id === rumisub) {
							message.reply("そーなのかー");
							return;
						}
						message.reply("そうですか。");
					} else {
						//メッセージである
						//しもねた系
						if (message.content.includes("まんこ") || message.content.includes("生理") || message.content.includes("ちんこ")) {
							message.reply("きっしょ死ね");
							return;
						}
						//お → なに？
						if (message.content.replace("<@" + client.user.id + ">", "").endsWith("お")) {
							message.reply("...");
							return;
						}
						//特定の人なら
						if (message.author.id === rumi || message.author.id === hakurei_win || message.author.id === p_nsk || message.author.id === rumisub) {
							message.reply("なんなのだー？");
							return;
						}
						message.reply("なに？");
					}
				}
			});
		}
	}

	//検索
	if (message.content.startsWith("検索 ") || message.content.startsWith("検索　")) {
		if (!CONFIG.DISABLE?.includes("search")) {
			search(message);
		}
	}

	//その鯖で有効化されているか
	let FUNCTION_SETTING_OBJ = await new FUNCTION_SETTING().LOAD();
	if (FUNCTION_SETTING_OBJ.some(ROW => ROW.GID === message.guild.id && ROW.FUNC_ID === "vxtwitter")) {
		if (!CONFIG.DISABLE?.includes("vxtwitter")) {
			// vxtwitterのリンクに自動で置換する機能
			// もし実行しないと設定してるなら動かさない
			await convert_vxtwitter(message);
		}
	}
	//計算
	if (message.content.startsWith("計算 ") || message.content.startsWith("計算　")) {
		if (!CONFIG.DISABLE?.includes("calc")) {
			// もし実行しないと設定してるなら動かさない
			calc(message);
		}
	}

	/*
	//猫モード(無かったことにする)
	if(message.author.id === rumi){
		let TEXT = message.content;
		TEXT = TEXT.replace("な", "にゃ");
		TEXT = TEXT.replace("ぬ", "にゅ");
		TEXT = TEXT.replace("ね", "にぇ");
		TEXT = TEXT.replace("の", "にょ");

		//元メッセージを消す
		message.delete();

		let WEB_HOOK = await WebHook_FIND(message.channel);

		//WHでめっせーじを送る
		WEB_HOOK.send({
			username: message.author.username,
			avatarURL: "https://cdn.discordapp.com/avatars/" + message.author.id + "/" + message.author.avatar + ".png",
			content:TEXT
		});
	}
	*/

	if (message.content === "今日は何の日？") {
		message.react("✅");
		new command.WHAT_NOW_DAY().main(message);
	}

	LOCK_NICK_NAME_OBJ.main(message.member);
	/*
	if (!CONFIG.DISABLE?.includes("automod")) {
		DENIED_WORD_OBJ.main(message);
	}
	*/

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
		const DATE_TEXT = DATE.getFullYear() + "年" + (DATE.getMonth() + 1) + "月" + DATE.getDate() + "日" + DAY_FORMAT[DATE.getDay()] + "曜日" + "\n" + DATE.getHours() + "時" + DATE.getMinutes() + "分" + DATE.getSeconds() + "秒" + DATE.getMilliseconds() + "ミリ秒";

		message.reply(DATE_TEXT);
	}

	if (message.guild.id === rumiserver) {
		if (!CONFIG.DISABLE?.includes("httpcat")) {
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
				// ディレクトリが存在しない場合、作成
				FS.mkdirSync(targetPath, { recursive: true });
			}
			const FileStream = FS.createWriteStream(PATH.join(targetPath, message.id + "_" + key + fileExtension));
			console.info("[ *** ][ MSG_FILES ]Downloading…");
			fetch(targetUrl)
				.then(res => res.body.pipe(FileStream))
				.then(() => console.info("[ OK ][ MSG_FILES ]Downloaded"))
				.catch(error => console.error("[ ERR ][ MSG_FILES ]" + error));
		});
});

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
		if (CONFIG.BLOCK) {
			if (CONFIG.BLOCK.includes(INTERACTION.user.id)) {
				return;
			}
		}

		if (!INTERACTION.isCommand()) {
			//コマンドが送信されたか確認
			return;
		}
		try {
			console.log("[ INFO ][CMD]┌Interaction create:" + INTERACTION.commandName + "\n             ├in " + INTERACTION.guild.name + "\n             ├in " + INTERACTION.channel.name + INTERACTION.channelId + "\n             └in " + INTERACTION.member.user.username + "(" + INTERACTION.member.id + ")");
		} catch (EX) {
			console.error("[ ERR ][ LOG ]", EX);
			INTERACTION.reply("エラー");
			return;
		}

		//ユーザーに待ってもらう
		await INTERACTION.deferReply();

		const CMD = INTERACTION.commandName;

		switch (CMD) {
			case "test":
				new command.test(INTERACTION).main();
				break;
			case "help":
				new command.HELP(INTERACTION).main();
				break;
			case "ping":
				new command.PING(INTERACTION).main();
				break;
			case "ferris":
				new command.FERRIS(INTERACTION).main();
				break;
			case "ws":
				new command.WS(INTERACTION).main();
				break;
			case "info_server":
				getServerInfo(INTERACTION);
				break;
			case "info_user":
				getUserInfo(INTERACTION);
				break;
			case "info_mine":
				getMcInfo(INTERACTION);
				break;
			case "kanji":
				new command.KANJI(INTERACTION).main();
				break;
			case "letter":
				new command.LETTER(INTERACTION).main();
				break;
			case "sns_set":
				new command.SNS(INTERACTION).main();
				break;
			case "ip":
				new command.IP(INTERACTION).main();
				break;
			case "setting":
				new command.SETTING(INTERACTION).SET();
				break;
			case "num":
				new command.NUM(INTERACTION).main();
				break;
			case "wh_clear":
				new command.WH_CLEAR(INTERACTION).main();
				break;
			case "vc_music":
				new command.VC_MUSIC(INTERACTION).main();
				break;
		}
	} catch (EX) {
		console.error("[ ERR ][ DJS ]" + EX);
		return;
	}
});

//ボタン
client.on("interactionCreate", async INTERACTION => {
	if (!INTERACTION.isButton()) {
		return;
	}

	switch (INTERACTION.customId) {
		case "test":
			new command.test(INTERACTION).button();
			break;
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

		// Send a DM to the guild owner
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

			LOG_CH.send(SERVERS.size + 1 + " са¯ва¯ вэдэне тащ ду¯ма;\n" + "Иф" + SERVERS.size + " са¯ва¯ вэдэне зад〜! Бля¯д!");
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

client.on("guildMemberUpdate", (oldMember, newMember) => {
	LOCK_NICK_NAME_OBJ.main(newMember);
});

//ロール変更を検知
client.on("roleUpdate", (oldRole, newRole) => {
	if (newRole.guild.id === "836142496563068929") {
		const OLD_ROLE_PM = oldRole.permissions.toArray();
		const NEW_ROLE_PM = newRole.permissions.toArray();

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
				CH.send("「" + sanitize(oldRole.name) + "」→「" + sanitize(newRole.name) + "」ロールの権限が変更されました\n" + sanitize(PM_UPDATE_TEXT));
			}
		}
	}
});

// ログインする
client.login(CONFIG.TOKEN);
