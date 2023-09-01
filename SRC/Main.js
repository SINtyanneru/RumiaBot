const FS = require('fs');
const { Client, Intents, MessageEmbed, WebhookClient} = require('discord.js');
const { ApplicationCommandType } = require('discord-api-types/v9');
const { ContextMenuCommandBuilder } = require('@discordjs/builders');
const { exec } = require('child_process');
const net = require('net');
const WebSocket = require('ws');
const http = require('http');
const https = require('https');
const { Builder, By, Key, until } = require('selenium-webdriver');
const chrome = require('selenium-webdriver/chrome');
const PATH = require('path');

let CONFIG = {};
let ACTIVE = true;

try {
	const DATA = FS.readFileSync('./Config.json', 'utf8');
	CONFIG = JSON.parse(DATA);
}catch(EX){
	console.error("[ ERR ]Config file load ERR");
	return;
}


const client = new Client({
	intents: [
		Intents.FLAGS.GUILDS,
		Intents.FLAGS.GUILD_MESSAGES,
		Intents.FLAGS.MESSAGE_CONTENT,
		Intents.FLAGS.DIRECT_MESSAGES,
		Intents.FLAGS.DIRECT_MESSAGE_REACTIONS,
		Intents.FLAGS.DIRECT_MESSAGE_TYPING,
		Intents.FLAGS.GUILD_MEMBERS,
		Intents.FLAGS.GUILD_BANS,
		Intents.FLAGS.GUILD_WEBHOOKS,
	],
});

client.once('ready',async ()=>{
console.log("    ____                  _       ____  ____  ______");
console.log("   / __ \__  ______ ___  (_)___ _/ __ )/ __ \/_  __/");
console.log("  / /_/ / / / / __ `__ \/ / __ `/ __  / / / / / /   ");
console.log(" / _, _/ /_/ / / / / / / / /_/ / /_/ / /_/ / / /    ");
console.log("/_/ |_|\__,_/_/ /_/ /_/_/\__,_/_____/\____/ /_/     ");
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
			description: "テストコマンド",
		},{
			name: "ping",
			description: "pingします",
			options: [
				{
					name: 'host',
					description: 'ホスト名',
					type: 'STRING',
					required: true
				}
			]
		},{
			name: "ferris",
			description: "ウニ？カニ？ヤドカリ？",
			options: [
				{
					name: 'type',
					description: 'タイプ',
					type: 'STRING',
					required: true,
					choices: [
						{
							"name": "コンパイルできません",
							"value": "not_compile"
						},
						{
							"name": "パニックします！",
							"value": "panic"
						},
						{
							"name": "アンセーフなコードを含みます",
							"value": "un_safe"
						},
						{
							"name": "求められた振る舞いをしません",
							"value": "not_desired_behavior"
						}
					]
				}
			]
		},{
			name: "ws",
			description: "ウェブサイトをスクショします",
			options: [
				{
					name: 'url',
					description: 'ウェブサイトのURLです',
					type: 'STRING',
					required: true
				},{
					name: 'browser_name',
					description: 'ブラウザを指定(UAのみ)',
					type: 'STRING',
					required: false,
					choices: [
						{
							"name": "FireFox",
							"value": "firefox"
						},
						{
							"name": "Floorp",
							"value": "floorp"
						},
						{
							"name": "るみさん",
							"value": "rumisan"
						}
					]
				}
			]
		},{
			name: "info_server",
			description: "鯖の情報を取得"
		},{
			name: "info_user",
			description: "ユーザーの情報を取得",
			options: [
				{
					name: 'user',
					description: 'ユーザーを指定しろ',
					type: 'MENTIONABLE',
					required: true
				}
			]
		},{
			//本当はコンテキストメニューを実装する予定だったんだ、
			//だけど、DiscordJSのローカライズ機能がローカライズを履き違えてるから無理だったよ
			//ExpectedConstraintError: Invalid string format
			//意味がわからないね、ローカライズとは
			name: "kanji",
			description: "漢字を変換します",
			options: [
				{
					name: 'text',
					description: '文字列',
					type: 'STRING',
					required: true
				},{
					name: 'mode',
					description: 'モード',
					type: 'STRING',
					required: true,
					choices: [
						{
							"name": "新 => 旧",
							"value": "n_o"
						},
						{
							"name": "旧 => 新",
							"value": "o_n"
						}
					]
				}
			]
		},{
			name: "letter",
			description: "文字を色々変換してくれます、たぶん",
			options: [
				{
					name: 'text',
					description: '文字列',
					type: 'STRING',
					required: true
				},{
					name: 'old',
					description: '変換前',
					type: 'STRING',
					required: true,
					choices: [
						{
							"name": "ひらがな",
							"value": "hilagana"
						},
						{
							"name": "ラテン文字",
							"value": "latin"
						}
					]
				},{
					name: 'new',
					description: '変換後',
					type: 'STRING',
					required: true,
					choices: [
						{
							"name": "ひらがな",
							"value": "hilagana"
						},
						{
							"name": "ラテン文字",
							"value": "latin"
						}
					]
				}
			]
		},{
			name: "help",
			description: "ヘルプコマンド、作るのめんどいやつ",
			options: [
				{
					name: 'mode',
					description: 'どれを見るか',
					type: 'STRING',
					required: true,
					choices: [
						{
							"name": "スラッシュコマンド",
							"value": "slash"
						},
						{
							"name": "メッセージコマンド",
							"value": "message"
						}
					]
				}
			]
		},
	];

	try{
		//グローバルスラッシュコマンドを登録
		await client.application.commands.set(commandData);
		console.log('Global slash commands registered!');
	}catch(EX){
		console.error('Error registering global slash commands:', EX);
	}

	new MISSKEY().main();

	//活動期間か？
	setInterval(() => {
		//現在の時刻を取得
		const currentTime = new Date();

		//時刻の取得
		const currentHour = currentTime.getHours();

		//判定
		if(currentHour >= 6 && currentHour < 21){
			ACTIVE = true;
		}else{
			ACTIVE = false;
		}
	}, 1000);

	let TEMP_ACTIVE = undefined;
	setInterval(() => {
		if(TEMP_ACTIVE !== ACTIVE){
			TEMP_ACTIVE = ACTIVE;
			if(ACTIVE){
				client.user.setPresence({
					status: "online",
					activities:[
						{
							name: "貴様",
							type: "WATCHING",
						},
					],
				});
			}else{
				client.user.setPresence({
					status: "idle",
					activities:[
						{
							name: "睡眠",
							type: "PLAYING",
						},
					],
				});
			}
		}
	}, 1000);
});

//メッセージを受信
client.on('messageCreate', async (message) => {
	//ログを出す
	let LOG_TEXT = "┌[" + message.author.username + "@" + message.guild.name + "/" + message.channel.name + "]\n";
	const LOG_TEXT_SPLIT = message.content.split("\n");
	for (let I = 0; I < LOG_TEXT_SPLIT.length; I++) {
		const TEXT = LOG_TEXT_SPLIT[I];
		if(LOG_TEXT_SPLIT[I + 1] !== undefined){
			LOG_TEXT += "├" + TEXT + "\n";
		}else{
			LOG_TEXT += "└" + TEXT + "\n";
		}
	}
	console.log(LOG_TEXT);

	/*
	//BOTの場合は処理しない
	if(message.author.bot){
		return;
	}
	*/
	
	//BOT所有者専用のコマンド
	if(message.author.id === CONFIG.ADMIN_ID){
		await BOT_ADMIN(message);
	}

	//てすとこまんど
	if(message.content.startsWith(CONFIG.ADMIN_PREFIX + "HB/.")){//実験用
		message.react("✅");

		message.reply("るみさんの年齢は" + RUMI_HAPPY_BIRTHDAY());
	}

	//テストコマンド
	if(message.content.startsWith(CONFIG.ADMIN_PREFIX + "IT/.")){
		message.reply(
			"ping -c5 \"" + message.content.replace(CONFIG.ADMIN_PREFIX + "IT/.", "").replace(/[^A-Za-z0-9\-\.]/g, '') + "\""+
			"\nIP?" + net.isIP(CONFIG.ADMIN_PREFIX + "IT/.")
		);
	}
	
	//メンションされたユーザーのコレクションを取得
	const MENTION_USERS = message.mentions.users;

	if(!message.content.includes('@everyone') && !message.content.includes('@here')){
		//メンションされたユーザーがいるかチェック
		if(MENTION_USERS.size > 0){
			MENTION_USERS.forEach((USER) => {
				if(USER.id === CONFIG.ID){
					if(message.reference){
						message.reply("そうですか。");
					}else{
						message.reply("なに？");
					}
				}
			});
		}
	}

	//検索
	if(message.content.startsWith("検索 ")){
		const SEARCH_WORD = message.content.replace("検索 ", "")

		message.react("✅");

		new SEARCH(message, SEARCH_WORD).main();
	}

	//VX
	if(message.content.includes("https://twitter.com/")){
		let WEB_HOOK = await WebHook_FIND(message.channel);
		const TEXT = message.content.replaceAll("https://twitter.com/", "https://vxtwitter.com/");

		//WHでめっせーじを送る
		WEB_HOOK.send({
			username: message.author.username,
			avatarURL: "https://cdn.discordapp.com/avatars/" + message.author.id + "/" + message.author.avatar + ".png",
			content:TEXT
		});

		//元メッセ時を削除
		message.delete();
	}

	//計算
	if(message.content.startsWith("計算 ")){
		const MATH_TEXT = message.content.replace("計算 ", "").replace("×", "*").replace("÷", "/").replace(/[^0-9\-\+\*\/\(\)]/g, "");

		message.react("✅");

		let RESULT = await new MATH(message.content).main();

		//結果を吐き出す
		message.reply("多分結果は：「" + RESULT.toString() + "」です");
	}

	//MIQ
	if(message.content.startsWith("MIQ")){
		try{
			const MSG_ID = message.content.replace("MIQ ", "").replace(/[^0-9]/g, "");
			const DWN_PATH = PATH.join("DOWNLOAD", "MIQ", MSG_ID + ".png");

			message.react("✅");
	
			if (FS.existsSync(DWN_PATH)) {
				message.reply({files:[DWN_PATH]})
			} else {
				message.reply("そのQuoteは保存されていません");
			}
		}catch(EX){
			console.log(EX);
			message.reply("エラー");
			return;
		}
	}

});

client.on('messageUpdate', (oldMessage, newMessage) => {
	//Make it a Quote を ダウンロード
	if(newMessage.author.id === "949479338275913799"){
		console.log(newMessage.attachments.map(attachment => attachment.url).length);
		if(newMessage.attachments.map(attachment => attachment.url).length > 0){
			//newMessage.channel.sendTyping();

			//ダウンロード先
			const DOWNLOAD_URL = newMessage.attachments.map(attachment => attachment.url)[0];
			//保存先
			const DWN_PATH = PATH.join("DOWNLOAD", "MIQ", newMessage.id + ".png");
			
			//ファイルを作るやつ
			const FILE_STREAM = FS.createWriteStream(DWN_PATH);
			
			//ダウンロード開始
			console.error("[ *** ][ MIQDL ]Downloading...");
			https.get(DOWNLOAD_URL, RES => {
				RES.pipe(FILE_STREAM);
			
				RES.on('end', () => {//完了
					console.error("[ OK ][ MIQDL ]Donwloaded");
					//newMessage.reply("保存しました〜"); うるさい
				});
			}).on('error', EX => {
				console.error("[ ERR ][ MIQDL ]" + EX);
			});
		}else{
			try{
				const MSG_ID = newMessage.id;
				const DWN_PATH = PATH.join("DOWNLOAD", "MIQ", MSG_ID + ".png");
	
				newMessage.react("✅");
		
				if (FS.existsSync(DWN_PATH)) {
					newMessage.reply({
						content: "🇨🇳🇨🇳🇨🇳削除を検知！！！！🇨🇳🇨🇳🇨🇳",
						files:[DWN_PATH]
					})
				}
			}catch(EX){
				console.log("[ ERR ][ MIQ ]" + EX);
				return;
			}
		}
	}
});

//イントラクション
client.on('interactionCreate', async (INTERACTION) => {
	try{
		if(!INTERACTION.isCommand()){
			//コマンドが送信されたか確認
			return;
		};
	
		console.log("[ INFO ][CMD]┌Interaction create:" + INTERACTION.commandName+
					"\n             ├in " + INTERACTION.guild.name+
					"\n             ├in " + INTERACTION.channel.name + INTERACTION.channelId+
					"\n             └in " + INTERACTION.member.user.username + "(" + INTERACTION.member.id + ")");
	
		//ユーザーに待ってもらう
		await INTERACTION.deferReply();
	
		const CMD = INTERACTION.commandName;
		
		switch (CMD) {
			case 'test':
				new test(INTERACTION).main();
				break;
			case 'help':
				new HELP(INTERACTION).main();
				break;
			case 'ping':
				new PING(INTERACTION).main();
				break;
			case"ferris":
				new FERRIS(INTERACTION).main();
				break;
			case"ws":
				new WS(INTERACTION).main();
				break;
			case"info_server":
				new INFO(INTERACTION).sv_main();
				break;
			case"info_user":
				new INFO(INTERACTION).usr_main();
				break;
			case "kanji":
				new KANJI(INTERACTION).main();
				break;
			case "letter":
				new LETTER(INTERACTION).main();
				break;
		}
	}catch(EX){
		console.log("[ ERR ][ DJS ]" + EX);
		return;
	}
});

//鯖に参加した
client.on('guildCreate', async (GUILD) => {
	try{
		const LOG_CH = client.guilds.cache.get("836142496563068929").channels.cache.get("1128742498194444298");

		if(LOG_CH !== undefined){
			LOG_CH.send(GUILD.name + "(" + GUILD.id + ")に参加しました");
		}
	
		const guildOwner = await GUILD.fetchOwner();
	
		// Send a DM to the guild owner
		const dmChannel = await guildOwner.createDM();
		await dmChannel.send("導入ありがと！よろしくね！");
		console.log("[ INFO ][ GUILD ]Send DM:" + guildOwner.nickname);
	}catch(EX){
		console.log("[ ERR ][ GUILD ]Send DM:" + guildOwner.nickname);
		return;
	}
});


//鯖からキックされた
client.on('guildDelete', (GUILD) => {
	try{
		const LOG_CH = client.guilds.cache.get("836142496563068929").channels.cache.get("1128742498194444298");

		if(LOG_CH !== undefined){
			LOG_CH.send(GUILD.name + "(" + GUILD.id + ")から叩き出されました；；");

			const SERVERS = client.guilds.cache;

			LOG_CH.send((SERVERS.size + 1) + " са¯ва¯ вэдэне тащ ду¯ма;\n"+
						"Иф" + SERVERS.size + " са¯ва¯ вэдэне зад〜! Бля¯д!");
		}
	}catch(EX){
		console.log("[ ERR ][ GUILD ]Send MSG:" + EX);
		return;
	}
});

//メッセージが消された
client.on('messageDelete', async (deletedMessage) => {
	try{
		const EB = new MessageEmbed();
		EB.setTitle("メッセージが消されました");
		EB.setDescription(NULLCHECK(deletedMessage.author.username));
		EB.setColor(RND_COLOR());
	
		EB.addFields({
			name: "ないよう",
			value: NULLCHECK(deletedMessage.content),
			inline: false
		})
	
		MSG_SEND("836142496563068929", "1140511350620168192", {embeds:[EB]});
	}catch(EX){
		console.log("[ ERR ][ DELMSG ]Send MSG:" + EX);
		return;
	}
});

//メンバーが抜けた
client.on('guildMemberRemove', async (member) => {
	try{
		console.log(member);
		if(member.guild.id === "836142496563068929"){
			const EB = new MessageEmbed();
			EB.setTitle(NULLCHECK(member.displayName) + "が鯖から抜けたわ");
			EB.setDescription("彼は自分に私生活が有ることを証明してしまった");
			EB.setColor(RND_COLOR());
			MSG_SEND("836142496563068929", "894185240728322058", {embeds:[EB]})
		}
	}catch(EX){
		console.log("[ ERR ][ DELMSG ]Send MSG:" + EX);
		return;
	}
});


function RND_COLOR(){
	return "#00ff00";
}

function MSG_SEND(GID, CID, TEXT){
	client.guilds.cache.get(GID).channels.cache.get(CID).send(TEXT);
}

function RUMI_HAPPY_BIRTHDAY(){
	//2007年10月29日の日付を作成
	const targetDate = new Date(2007, 9, 29); //月は0から始まるため、9は10月を表す
		
	//今日の日付を取得
	const today = new Date();
		
	//年数の差を計算
	let yearDifference = today.getFullYear() - targetDate.getFullYear();
		
	//10月29日以前の場合、1年引く
	if(
		today.getMonth() < targetDate.getMonth() ||
		(today.getMonth() === targetDate.getMonth() &&
		today.getDate() < targetDate.getDate())
	){
		yearDifference--;
	}
	return yearDifference;
}

function NULLCHECK(VAR){
	if(VAR !== undefined && VAR !== null){
		return VAR;
	}else{
		return "ぬるぽ";
	}
}

async function WebHook_FIND(CHANNEL){
	let FWH = await CHANNEL.fetchWebhooks();
	let WH = FWH.find((webhook) => webhook.owner.id === CONFIG.ID);
	if(WH){
		return WH;
	}else{
		let NEW_WH = CHANNEL.createWebhook("るみBOT");
		return NEW_WH;
	}
}

client.login(CONFIG.TOKEN);