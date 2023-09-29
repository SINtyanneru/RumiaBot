import FS from "fs";
import net from "net";
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
import { DENIED_WORD } from "./DENIED_WORD.js";
import { LOCK_NICK_NAME } from "./MODULES/LOCK_NICK_NAME.js";
import { calc } from "./MODULES/calc.js";
import { search } from "./MODULES/search.js";
import { convert_vxtwitter } from "./convert_vxtwitter.js";
import { SQL } from "./SQL.js";
import { sanitize } from "./MODULES/sanitize.js";
import { SNS } from "./SNS.js";
import { includesAll } from "./MODULES/includesAll.js";
import { HTTP_STATUS_CODE } from "./MODULES/HTTP_STATUS_CODE.js";

//ここに、オブジェクトとして置いておくべき、クラスを、置くよ。
// ↑インスタンスのことですか？←るみさん用語でオブジェクトです
let DENIED_WORD_OBJ = new DENIED_WORD();
export let SQL_OBJ = new SQL();
// 何も存在しないなら
if (!(CONFIG.ADMIN_ID || CONFIG.ADMIN_PREFIX || CONFIG.ID || CONFIG.TOKEN)) {
	throw new Error("深刻なエラー:設定が初期化されていないので、実行できません");
}
export let SNS_CONNECTION = new SNS();

client.once("ready", async () => {
	console.log("    ____                  _       ____  ____  ______");
	console.log("   / __ \\__  ______ ___  (_)___ _/ __ )/ __ \\/_  __/");
	console.log("  / /_/ / / / / __ `__ \\/ / __ `/ __  / / / / / /   ");
	console.log(" / _, _/ /_/ / / / / / / / /_/ / /_/ / /_/ / / /    ");
	console.log("/_/ |_|\\__,_/_/ /_/ /_/_/\\__,_/_____/\\____/ /_/     ");
	console.log("V1.1");

	SNS_CONNECTION.main();

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

	const commandData = [
		{
			name: "test",
			description: "テストコマンド"
		},
		{
			name: "ping",
			description: "pingします",
			options: [
				{
					name: "host",
					description: "ホスト名",
					type: "STRING",
					required: true
				}
			]
		},
		{
			name: "ferris",
			description: "ウニ？カニ？ヤドカリ？",
			options: [
				{
					name: "type",
					description: "タイプ",
					type: "STRING",
					required: true,
					choices: [
						{
							name: "コンパイルできません",
							value: "not_compile"
						},
						{
							name: "パニックします！",
							value: "panic"
						},
						{
							name: "アンセーフなコードを含みます",
							value: "un_safe"
						},
						{
							name: "求められた振る舞いをしません",
							value: "not_desired_behavior"
						}
					]
				}
			]
		},
		{
			name: "ws",
			description: "ウェブサイトをスクショします",
			options: [
				{
					name: "url",
					description: "ウェブサイトのURLです",
					type: "STRING",
					required: true
				},
				{
					name: "browser_name",
					description: "ブラウザを指定(UAのみ)",
					type: "STRING",
					required: false,
					choices: [
						{
							name: "FireFox",
							value: "firefox"
						},
						{
							name: "Floorp",
							value: "floorp"
						},
						{
							name: "るみさん",
							value: "rumisan"
						}
					]
				}
			]
		},
		{
			name: "info_server",
			description: "鯖の情報を取得"
		},
		{
			name: "info_user",
			description: "ユーザーの情報を取得",
			options: [
				{
					name: "user",
					description: "ユーザーを指定しろ",
					type: "MENTIONABLE",
					required: true
				}
			]
		},
		{
			name: "info_mine",
			description: "マイクラのユーザーの情報を盗みます",
			options: [
				{
					name: "mcid",
					description: "マイクラのID",
					type: "STRING",
					required: true
				}
			]
		},
		{
			// NOTE 本当はコンテキストメニューを実装する予定だったんだ、
			// だけど、DiscordJSのローカライズ機能がローカライズを履き違えてるから無理だったよ
			// ExpectedConstraintError: Invalid string format
			// 意味がわからないね、ローカライズとは
			name: "kanji",
			description: "漢字を変換します",
			options: [
				{
					name: "text",
					description: "文字列",
					type: "STRING",
					required: true
				},
				{
					name: "mode",
					description: "モード",
					type: "STRING",
					required: true,
					choices: [
						{
							name: "新 => 旧",
							value: "n_o"
						},
						{
							name: "旧 => 新",
							value: "o_n"
						}
					]
				}
			]
		},
		{
			name: "letter",
			description: "文字を色々変換してくれます、たぶん",
			options: [
				{
					name: "text",
					description: "文字列",
					type: "STRING",
					required: true
				},
				{
					name: "old",
					description: "変換前",
					type: "STRING",
					required: true,
					choices: [
						{
							name: "ひらがな",
							value: "hilagana"
						},
						{
							name: "ラテン文字",
							value: "latin"
						}
					]
				},
				{
					name: "new",
					description: "変換後",
					type: "STRING",
					required: true,
					choices: [
						{
							name: "ひらがな",
							value: "hilagana"
						},
						{
							name: "ラテン文字",
							value: "latin"
						}
					]
				}
			]
		},
		{
			name: "help",
			description: "ヘルプコマンド、作るのめんどいやつ",
			options: [
				{
					name: "mode",
					description: "どれを見るか",
					type: "STRING",
					required: true,
					choices: [
						{
							name: "スラッシュコマンド",
							value: "slash"
						},
						{
							name: "メッセージコマンド",
							value: "message"
						}
					]
				}
			]
		},
		{
			name: "ip",
			description: "るみBOTのIPを表示します"
		}
	];

	//ActivityPub
	let SC_ActivityPub_CHOICES = [];
	CONFIG.SNS.forEach(DATA => {
		SC_ActivityPub_CHOICES.push({
			name: DATA.NAME,
			value: DATA.ID
		});
	});

	commandData.push({
		name: "sns_set",
		description: "SNSを",
		options: [
			{
				name: "type",
				description: "どのインスタンスを？",
				type: "STRING",
				required: true,
				choices: SC_ActivityPub_CHOICES
			},
			{
				name: "username",
				description: "誰を？",
				type: "STRING",
				required: true
			}
		]
	});

	//コマンドを登録する
	try {
		//グローバルスラッシュコマンドを登録
		await client.application.commands.set(commandData);
		console.log("Global slash commands registered!");
	} catch (EX) {
		console.error("Error registering global slash commands:", EX);
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

	let TEMP_ACTIVE = undefined;
	setInterval(() => {
		if (TEMP_ACTIVE !== ACTIVE) {
			TEMP_ACTIVE = ACTIVE;
			if (ACTIVE) {
				client.user.setPresence({
					status: "online",
					activities: [
						{
							name: "貴様",
							type: "WATCHING"
						}
					]
				});
			} else {
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
		console.log("[ ERR ][ LOG ]Send LOG ERR" + EX);
		return;
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

	//てすとこまんど
	if (message.content.startsWith(CONFIG.ADMIN_PREFIX + "HB/.")) {
		//実験用
		message.react("✅");

		message.reply("るみさんの年齢は" + RUMI_HAPPY_BIRTHDAY());
	}

	//テストコマンド
	if (message.content.startsWith(CONFIG.ADMIN_PREFIX + "IT/.")) {
		message.reply(
			'ping -c5 "' +
				message.content.replace(CONFIG.ADMIN_PREFIX + "IT/.", "").replace(/[^A-Za-z0-9\-.]/g, "") +
				'"' +
				"\nIP?" +
				net.isIP(CONFIG.ADMIN_PREFIX + "IT/.")
		);
	}

	//メンションされたユーザーのコレクションを取得
	const MENTION_USERS = message.mentions.users;

	if (!message.content.includes("@everyone") && !message.content.includes("@here")) {
		//メンションされたユーザーがいるかチェック
		if (MENTION_USERS.size > 0) {
			MENTION_USERS.forEach(USER => {
				if (USER.id === CONFIG.ID) {
					if (message.reference) {
						if (
							message.content.includes("まんこ") ||
							message.content.includes("生理") ||
							message.content.includes("ちんこ")
						) {
							message.reply("きもい");
							return;
						}
						if (
							message.author.id === rumi ||
							message.author.id === hakurei_win ||
							message.author.id === p_nsk ||
							message.author.id === rumisub
						) {
							message.reply("そーなのかー");
							return;
						}
						message.reply("そうですか。");
					} else {
						if (
							message.content.includes("まんこ") ||
							message.content.includes("生理") ||
							message.content.includes("ちんこ")
						) {
							message.reply("きっしょ死ね");
							return;
						}
						if (message.content.replace("<@" + client.user.id + ">", "").endsWith("お")) {
							message.reply("...");
							return;
						}
						if (
							message.author.id === rumi ||
							message.author.id === hakurei_win ||
							message.author.id === p_nsk ||
							message.author.id === rumisub
						) {
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
	if (message.content.startsWith("検索 ")) {
		if (!CONFIG.DISABLE?.includes("search")) {
			search(message);
		}
	}

	// vxtwitterのリンクに自動で置換する機能
	if (!CONFIG.DISABLE?.includes("vxtwitter")) {
		// もし実行しないと設定してるなら動かさない
		await convert_vxtwitter(message);
	}
	//計算
	if (message.content.startsWith("計算 ")) {
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

	LOCK_NICK_NAME(message.member);
	if (!CONFIG.DISABLE?.includes("automod")) {
		DENIED_WORD_OBJ.main(message);
	}

	if (message.content.startsWith("ルーレット")) {
		const CHOISE_LIST = message.content.replace("ルーレット ").split(",");
		const RANDOM = Math.floor(Math.random() * CHOISE_LIST.length);
		if (CHOISE_LIST[RANDOM]) {
			message.reply("結果：" + sanitize(CHOISE_LIST[RANDOM]));
		}
	}
	if (!CONFIG.DISABLE?.includes("httpcat")) {
		const { detected, value } = includesAll(
			message.content
				.replace(/<@[0-9&#]+>/g, "") // ユーザーとロールのメンションを削除 あとチャンネルも削除しています
				.replace(/<:.+?:[0-9]+>/g, "") // 絵文字idも削除
				.replace(/<\/.+?:[0-9]+>/g, ""), // コマンドidも削除
			...HTTP_STATUS_CODE
		);
		if (detected) {
			if (!message.author.bot) {
				message.channel.send({ content: `http://http.cat/${value}`, flags: ["SUPPRESS_NOTIFICATIONS"] });
			}
		}
	}
});

client.on("messageUpdate", (oldMessage, newMessage) => {
	//Make it a Quote を ダウンロード
	if (newMessage.author.id === makeitaquote) {
		console.log(newMessage.attachments.map(attachment => attachment.url).length);
		if (newMessage.attachments.map(attachment => attachment.url).length > 0) {
			new command.MIQ().save_miq(newMessage);
		} else {
			new command.MIQ().load_miq(newMessage);
		}
	}
});

//イントラクション
client.on("interactionCreate", async INTERACTION => {
	try {
		if (!INTERACTION.isCommand()) {
			//コマンドが送信されたか確認
			return;
		}
		try {
			console.log(
				"[ INFO ][CMD]┌Interaction create:" +
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
		} catch (EX) {
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
				new command.INFO(INTERACTION).sv_main();
				break;
			case "info_user":
				new command.INFO(INTERACTION).usr_main();
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
			case "info_mine":
				new command.INFO(INTERACTION).MINECRAFT();
				break;
			case "ip":
				new command.IP(INTERACTION).main();
				break;
		}
	} catch (EX) {
		console.log("[ ERR ][ DJS ]" + EX);
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

		// Send a DM to the guild owner
		const dmChannel = await guildOwner.createDM();
		await dmChannel.send("導入ありがと！よろしくね！");
		console.log("[ INFO ][ GUILD ]Send DM:" + guildOwner.nickname);
	} catch (EX) {
		const guildOwner = await GUILD.fetchOwner();
		console.log("[ ERR ][ GUILD ]Send DM:" + guildOwner.nickname);
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
		console.log("[ ERR ][ GUILD ]Send MSG:" + EX);
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
		console.log(member);
		if (member.guild.id === rumiserver) {
			const EB = new MessageEmbed();
			EB.setTitle(NULLCHECK(member.displayName) + "が鯖から抜けたわ");
			EB.setDescription("彼は自分に私生活が有ることを証明してしまった");
			EB.setColor(RND_COLOR());
			MSG_SEND(client, rumiserver, exiter_channel, { embeds: [EB] });

			//独自のBANリスト
			const fileName = "./TEMP/RS_LEAVE.json";
			FS.access(fileName, FS.constants.F_OK, ERR => {
				if (ERR) {
					FS.writeFile(
						fileName,
						JSON.stringify([
							{
								ID: member.id,
								DATE: new Date().toDateString()
							}
						]),
						ERR => {
							if (ERR) {
								console.error("[ ERR ][ AUTO BAN ]JSONファイルを作成できませんでした:" + ERR);
							} else {
								console.error("[ OK ][ AUTO BAN ]JSONファイルを作成しました");
							}
						}
					);
				} else {
					FS.readFile(fileName, "utf8", (ERR, DATA) => {
						if (ERR) {
							console.error("[ ERR ][ AUTO BAN ]JSONファイルを読み込めませんでした:" + ERR);
						} else {
							console.error("[ OK ][ AUTO BAN ]JSONファイルを読み込みました");
							const RESULT = JSON.parse(DATA);
							if (!RESULT.some(ROW => ROW.ID === member.id)) {
								RESULT.push({
									ID: member.id,
									DATE: new Date().toISOString()
								});
								FS.writeFile(fileName, JSON.stringify(RESULT), ERR => {
									if (ERR) {
										console.error("[ ERR ][ AUTO BAN ]JSONファイルを作成できませんでした:" + ERR);
									} else {
										console.error("[ OK ][ AUTO BAN ]JSONファイルを作成しました");
									}
								});
							}
						}
					});
				}
			});
		}
	} catch (EX) {
		console.log("[ ERR ][ AUTO BAN ]" + EX);
		return;
	}
});

//メンバーが入った
client.on("guildMemberAdd", member => {
	try {
		if (member.guild.id === rumiserver) {
			//独自のBANリストでチェックする
			const fileName = "./TEMP/RS_LEAVE.json";
			FS.access(fileName, FS.constants.F_OK, ERR => {
				if (!ERR) {
					FS.readFile(fileName, "utf8", (ERR, DATA) => {
						if (ERR) {
							console.error("[ ERR ][ AUTO BAN ]JSONファイルを読み込めませんでした:" + ERR);
						} else {
							console.error("[ OK ][ AUTO BAN ]JSONファイルを読み込みました");
							const RESULT = JSON.parse(DATA);
							const BAN_INFO = RESULT.find(ROW => ROW.ID === member.id);
							if (BAN_INFO !== undefined && BAN_INFO !== null) {
								const USER = client.users.cache.get(member.id);

								//地獄計算
								const DAY_FORMAT = ["日", "月", "火", "水", "木", "金", "土"];
								const DATE = new Date(BAN_INFO.DATE);

								USER.send(
									"あなたは、" +
										DATE.getFullYear().toString() +
										"年 " +
										(DATE.getMonth() + 1).toString() +
										"月 " +
										DATE.getDate().toString() +
										"日 " +
										DAY_FORMAT[DATE.getDay()] +
										"曜日 " +
										DATE.getHours().toString() +
										"時 " +
										DATE.getMinutes().toString() +
										"分 " +
										DATE.getSeconds().toString() +
										"秒 " +
										DATE.getMilliseconds().toString() +
										"ミリ秒\n" +
										"に、るみさんの鯖から脱退しています。\n" +
										"認証をされるには、<@" +
										rumi +
										">にDMで以下のことを教えてください。\n" +
										"\n" +
										"1・なぜ抜けたのか\n" +
										"2・なぜ戻ってきたのか\n" +
										"\n" +
										"理由は、無言で戻ってこられると、「なんで抜けたのにもどってきたんだ？」と気になるからです()"
								);
							}
						}
					});
				}
			});
		}
	} catch (EX) {
		console.log("[ ERR ][ AUTO BAN ]" + EX);
		return;
	}
});

client.on("guildMemberUpdate", (oldMember, newMember) => {
	LOCK_NICK_NAME(newMember);
});

//ロール変更を検知
client.on("roleUpdate", (oldRole, newRole) => {
	//まぞくロール
	if (newRole.id === "1130723739349291089") {
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
			CH.send("まぞくロールの権限が変更されました\n" + sanitize(PM_UPDATE_TEXT));
		}
	}
});

// ログインする
client.login(CONFIG.TOKEN);
