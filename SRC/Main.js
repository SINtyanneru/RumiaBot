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

client.once("ready", async () => {
	console.log(String.raw`    ____                  _       ____  ____  ______`);
	console.log(String.raw`   / __ \__  ______ ___  (_)___ _/ __ )/ __ \/_  __/`);
	console.log(String.raw`  / /_/ / / / / __ \`__ \/ / __ \`/ __  / / / / / /   `);
	console.log(String.raw` / _, _/ /_/ / / / / / / / /_/ / /_/ / /_/ / / /    `);
	console.log(String.raw`/_/ |_|\__,_/_/ /_/ /_/_/\__,_/_____/\____/ /_/     `);
	console.log("V1.0");

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
			// NOTE 本当はコンテキストメニューを実装する予定だったんだ、
			//だけど、DiscordJSのローカライズ機能がローカライズを履き違えてるから無理だったよ
			//ExpectedConstraintError: Invalid string format
			//意味がわからないね、ローカライズとは
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

	//new MISSKEY().main();

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
	if (CONFIG.ADMIN_ID.includes(message.author.id)) {
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
						}
						if (message.author.id === "867187372026232833") {
							message.reply("そうですか(笑)");
							return;
						}
						if (
							message.author.id === "564772363950882816" ||
							message.author.id === "811143522212118528" ||
							message.author.id === "980604083851390976"
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
						}
						if (message.content.replace("<@" + client.user.id + ">", "").endsWith("お")) {
							message.reply("...");
							return;
						}
						if (
							message.author.id === "564772363950882816" ||
							message.author.id === "811143522212118528" ||
							message.author.id === "980604083851390976"
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
		const SEARCH_WORD = message.content.replace("検索 ", "");

		message.react("✅");

		new command.SEARCH(message, SEARCH_WORD).main();
	}

	//VX
	const VX_REGEX = /https:\/\/twitter\.com\/[a-zA-Z0-9_]+\/status\/[0-9]+/g;
	if (message.content.match(VX_REGEX)) {
		let WEB_HOOK = await WebHook_FIND(message.channel);
		const TEXT = message.content
			.replaceAll("https://twitter.com/", "https://vxtwitter.com/")
			.replaceAll("@everyone", "[全体メンション]")
			.replaceAll("@here", "[全体メンション]")
			.replaceAll(/<@&[^>]*>/g, "[ロールメンション]");

		//WHでめっせーじを送る
		WEB_HOOK.send({
			username: message.author.username,
			avatarURL: "https://cdn.discordapp.com/avatars/" + message.author.id + "/" + message.author.avatar + ".png",
			content: TEXT
		});

		//元メッセージを削除
		if(message.content){
			message.delete();
		}
	}

	//計算
	if (message.content.startsWith("計算 ")) {
		const MATH_TEXT = message.content
			.replace("計算 ", "")
			.replace("×", "*")
			.replace("÷", "/")
			.replace(/[^0-9\-+*/().]/g, "");

		message.react("✅");
		console.log(MATH_TEXT);

		let RESULT = new command.MATH(message.content).main();

		//結果を吐き出す
		message.reply(RESULT);
	}

	/*
	//猫モード(無かったことにする)
	if(message.author.id === "564772363950882816"){
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

	//あずさ
	if (message.author.id === "867187372026232833") {
		if (
			message.content.includes("きしょ") ||
			message.content.includes("死ね") ||
			message.content.includes("kisyo") ||
			message.content.includes("やめろ") ||
			message.content.includes("死ね")
		) {
			message.reply("黙れ");
		}

		if (message.content.includes("おい")) {
			message.reply("あ？");
		}

		if (message.content.includes("天安門")) {
			message.reply("気をつけな、あんた、中華人民共和国当局に、見られてるぜ");
		}
	}

	if (message.content === "今日は何の日？") {
		message.react("✅");
		new command.WHAT_NOW_DAY().main(message);
	}

	LOCK_NICK_NAME(message.member);
});

client.on("messageUpdate", (oldMessage, newMessage) => {
	//Make it a Quote を ダウンロード
	if (newMessage.author.id === "949479338275913799") {
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
		}
	} catch (EX) {
		console.log("[ ERR ][ DJS ]" + EX);
		return;
	}
});

//鯖に参加した
client.on("guildCreate", async GUILD => {
	try {
		const LOG_CH = client.guilds.cache.get("836142496563068929").channels.cache.get("1128742498194444298");

		if (LOG_CH !== undefined) {
			LOG_CH.send(GUILD.name + "(" + GUILD.id + ")に参加しました");
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
		const LOG_CH = client.guilds.cache.get("836142496563068929").channels.cache.get("1128742498194444298");

		if (LOG_CH !== undefined) {
			LOG_CH.send(GUILD.name + "(" + GUILD.id + ")から叩き出されました；；");

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
		if (member.guild.id === "836142496563068929") {
			const EB = new MessageEmbed();
			EB.setTitle(NULLCHECK(member.displayName) + "が鯖から抜けたわ");
			EB.setDescription("彼は自分に私生活が有ることを証明してしまった");
			EB.setColor(RND_COLOR());
			MSG_SEND(client, "836142496563068929", "894185240728322058", { embeds: [EB] });

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
		if (member.guild.id === "836142496563068929") {
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
										"認証をされるには、<@564772363950882816>にDMで以下のことを教えてください。\n" +
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

async function WebHook_FIND(CHANNEL) {
	let FWH = await CHANNEL.fetchWebhooks();
	let WH = FWH.find(webhook => webhook.owner.id === CONFIG.ID);
	if (WH) {
		return WH;
	} else {
		let NEW_WH = CHANNEL.createWebhook("るみBOT");
		return NEW_WH;
	}
}

async function LOCK_NICK_NAME(MEMBER){
	try{
		//るみ鯖無いでの出来事に適応
		if(MEMBER.guild.id === "836142496563068929"){
			const NICK_LOCK_USER = {
				"759410422591389736":{
					"NAME":"緑霊夢"
				},
				"828569154167767061":{
					"NAME":"BaGuAr二世"
				},
				"997588139235360958":{
					"NAME":"猫川風緑"
				},
				"612479046919520275":{
					"NAME":"ミント㌨Да！！"
				},
				"1059267736049557635":{
					"NAME":"まっさんご\"う\""
				}
			};

			const NLU = NICK_LOCK_USER[MEMBER.id.toString()];
			if(NLU){
				if(NLU.NAME !== MEMBER.nickname){
					console.log("[ INFO ][ LOCK NICKNAME ]" + MEMBER.user.name + "がニックネームを変えました");
					if(MEMBER.manageable){
						MEMBER.setNickname(NLU.NAME);
					}else{
						console.log("[ ERR ][ LOCK NICKNAME ]権限不足により変更できませんでした");
						return;
					}
				}
			}
		}
	}catch(EX){
		console.log("[ ERR ][ LOCK NICKNAME ]" + EX);
		return;
	}
}

client.login(CONFIG.TOKEN);
