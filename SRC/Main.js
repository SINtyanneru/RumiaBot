const FS = require('fs');
const { Client, Intents, MessageEmbed } = require('discord.js');
const { exec } = require('child_process');
const net = require('net');
const WebSocket = require('ws');
const http = require('http');
const https = require('https');
const { Builder, By, Key, until } = require('selenium-webdriver');
const chrome = require('selenium-webdriver/chrome');

let CONFIG = {};

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
	],
});

client.once('ready',async ()=>{
	console.log("BOT is online!");

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
});

client.on('messageCreate', async (message) => {
	if(message.author.bot){
		return;
	}
	
	if(message.author.id === CONFIG.ADMIN_ID){
		if(message.content === CONFIG.ADMIN_PREFIX + "sls"){
			console.log(client.guilds.cache.size);
			message.reply("サーバー参加数：「" + client.guilds.cache.size + "」");
		}

		if(message.content === CONFIG.ADMIN_PREFIX + "sl"){
			const SERVERS = client.guilds.cache;
			console.log(SERVERS.size);

			let EB = new MessageEmbed()
				.setTitle("参加済み鯖")
				.setDescription("合計" + SERVERS.size)
				.setColor(RND_COLOR());

			SERVERS.forEach(SERVER => {
				EB.addFields({
					name: SERVER.name,
					value: SERVER.id,
					inline: true
				});
			});

			message.reply({embeds:[EB]})
		}
		if(message.content.startsWith(CONFIG.ADMIN_PREFIX + "SHELL/.")){
			try{
				const CMD = message.content.replace(CONFIG.ADMIN_PREFIX + "SHELL/.", "");
				exec("sh -c \"" + CMD + "\"", (error, stdout, stderr) => {
					if(error){
						message.reply("EXECでエラーが発生");
						return;
					}
					if(stderr){
						message.reply("```sh\n" + stderr + "```\nEXIT CODE:NOT 0");
						return;
					}
					message.reply("```sh\n" + stdout + "```\nEXIT CODE:0");
				});
			}catch(EX){
				message.reply(EX);
			}
		}

		if(message.content.startsWith(CONFIG.ADMIN_PREFIX + "EXEC/.")){
			try{
				const CMD = message.content.replace(CONFIG.ADMIN_PREFIX + "EXEC/.", "");
				eval(CMD);
			}catch(EX){
				console.log(EX);
			}
			
		}

		if(message.content.startsWith(CONFIG.ADMIN_PREFIX + "RV/.")){
			try{
				const USER_ID = message.content.replace(CONFIG.ADMIN_PREFIX + "RV/.", "");
				const ROLE_1 = message.guild.roles.cache.get("965185875778609184");//性別
				const ROLE_2 = message.guild.roles.cache.get("1005417446171222047");//国
				const ROLE_3 = message.guild.roles.cache.get("847108353691746354");//一般
				if(ROLE_1 && ROLE_2 && ROLE_3){
					try{
						const MEMBER = message.guild.members.cache.get(USER_ID);
						MEMBER.roles.add(ROLE_1);
						MEMBER.roles.add(ROLE_2);
						MEMBER.roles.add(ROLE_3);
	
						message.reply("付与");
					}catch(EX){
						console.log(EX);
						message.reply("失敗");
					}
				}else{
					message.reply("ロールが見つかりません");
				}
			}catch(EX){
				console.log(EX);
				message.reply("失敗");
			}
		}
	}

	if(message.content.startsWith(CONFIG.ADMIN_PREFIX + "HB/.")){//実験用
		message.reply("るみさんの年齢は" + RUMI_HAPPY_BIRTHDAY());
	}


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
		new SEARCH(message, SEARCH_WORD).main();
	}
});

client.on('interactionCreate', async (INTERACTION) => {
	try{
		if(!INTERACTION.isCommand()){
			//コマンドが送信されたか確認
			return;
		};
	
		console.log("[ INFO ][CMD]┌Interaction create:" + INTERACTION.commandName+
					"\n             ├in" + INTERACTION.guild.name+
					"\n             └in" + INTERACTION.member.nickname + "(" + INTERACTION.member.id + ")");
	
		//ユーザーに待ってもらう
		await INTERACTION.deferReply();
	
		const CMD = INTERACTION.commandName;
		
		switch (CMD) {
			case 'test':
				new test(INTERACTION).main();
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
		}
	}catch(EX){
		console.log("[ ERR ][ DJS ]" + EX);
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

client.login(CONFIG.TOKEN);