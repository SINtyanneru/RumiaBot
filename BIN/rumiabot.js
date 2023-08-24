class FERRIS{
	constructor(INTERACTION) {
		this.E = INTERACTION;
	}

	async main(){
		let E = this.E;
		switch(E.options.getString("type")){
			case "not_compile":
				await E.editReply("https://rumiserver.com/Asset/RUMI_BOT/RES/does_not_compile.png");
				break;
			case "panic":
				await E.editReply("https://rumiserver.com/Asset/RUMI_BOT/RES/panics.png");
				break;
			case "un_safe":
				await E.editReply("https://rumiserver.com/Asset/RUMI_BOT/RES/unsafe.png");
				break;
			case "not_desired_behavior":
				await E.editReply("https://rumiserver.com/Asset/RUMI_BOT/RES/not_desired_behavior.png");
				break;
		}
	}
}
//DiscordJSの所為でファイルアップロードができんかった、まじでふざけんな
class PING{
	constructor(INTERACTION) {
		this.E = INTERACTION;
	}
	
	async main(){
		let E = this.E;
		const CMD = E.options.getString("host").replace(/[^A-Za-z0-9\-\.]/g, "");
		if(CMD != undefined){
			//コマンドを実行し、リアルタイムに出力を取得します
			const EXEC = exec("ping -c5 \"" + CMD + "\"");

			let OUTPUT = "";//出力を記録
			let COUNT = 0;//出力した回数を記録

			EXEC.stdout.on('data', (data) => {
				OUTPUT = OUTPUT + data + "\n";
				if(COUNT <= 5){//出力が5以下なら更新する(望んだ動作にするため)
					//編集
					E.editReply(OUTPUT);
				}
				COUNT++;
			});

			EXEC.stderr.on('data', (data) => {
				//エラーを出す
				OUTPUT = OUTPUT + "PINGがエラーを吐きやがりました\n";
				E.editReply("PINGがエラーを吐きやがりました");
				
			});

			EXEC.on('close', (code) => {
				if(code === 0){
					E.editReply(OUTPUT + "\n"+
								"返り値が0だから成功したんじゃないかな");
				}else{
					E.editReply(OUTPUT + "\n"+
								"返り値が「" + code + "」だから失敗したんじゃないかな");
				}

			});
		}else{
			E.editReply("ホストの指定がおかしいね！");
		}
	}
}
class SEARCH{
	constructor(MSG, SEARCH_WORD){
		this.E = MSG;
		this.SEARCH_WORD = SEARCH_WORD;

		this.DENIED_URL = [
			"pornhub.com",
			"xvideos.com",
			"eroterest.net"
		]
	}

	async main(){
		// リクエストのオプションを設定
		const OPTION = {
			hostname: "www.googleapis.com",
			path: "/customsearch/v1" +
			"?key=" + encodeURIComponent(CONFIG.GOOGLE_API_KEY)+
			"&cx=" + encodeURIComponent(CONFIG.GOOGLE_API_ENGINE_ID)+
			"&q=" + encodeURIComponent(this.SEARCH_WORD),
			method: "GET",
		};

		// リクエストを作成
		const REQ = https.request(OPTION, (RES) => {
			//レスポンスを受け取るためのコールバック

			let DATA = "";

			//レスポンスデータを受信したときのイベントハンドラ
			RES.on("data", (CHUNK) => {
				DATA += CHUNK;
			});

			//レスポンスデータをすべて受信したときのイベントハンドラ
			RES.on("end", () => {
				try{
					const RESULT = JSON.parse(DATA);
					if(RESULT.error != null){
						console.log("[ ERR ][ SEARCH ]", RESULT.error);
						this.E.reply("検索中にエラー:" + RESULT.error.code + "\n" + RESULT.error.message);
						return;
					}

					//埋め込み生成くん
					let EB = new MessageEmbed();
					EB.setTitle("検索結果");
					EB.setDescription(this.SEARCH_WORD);
					EB.setColor(RND_COLOR());
	
					for(let I = 0; I < RESULT.items.length; I++){
						if(I > 5){
							const SEARCH_DATA = RESULT.items[I];
							const SEARCH_RESULT_URL = new URL(SEARCH_DATA.link);
	
							let DENIED = false;//禁止URLか
	
							//禁止されているURLを回す
							this.DENIED_URL.forEach(ROW=>{
								//禁止されているか？
								if(SEARCH_RESULT_URL.hostname.endsWith(ROW)){
									//禁止されていることを伝える
									DENIED = true;
								}
							});
	
							if(!DENIED){//禁止されていなければ
								//追加
								let TITLE = SEARCH_DATA.title;
								if(TITLE.length > 253){
									TITLE = TITLE.slice(0, 256) + "...";
								}
	
								let DESC = SEARCH_DATA.htmlSnippet;
								if(DESC.length > 253){
									DESC = DESC.slice(0, 256) + "...";
								}
								
								EB.addFields({
									name: TITLE,
									value: DESC + "\n[見に行く](" + SEARCH_DATA.link + ")",
									inline: false
								});
							}
						}
					}
	
					//返答
					this.E.reply({embeds:[EB]});
				}catch(EX){
					console.log("[ ERR ][ SEARCH ]" + EX);
				}
			});
		});

		//エラーハンドリング
		REQ.on("error", (ERR) => {
			console.error('エラー:', ERR);
		});

		//リクエストを送信
		REQ.end();
	}
}
class WS{
	constructor(INTERACTION) {
		this.E = INTERACTION;
	}

	async main(){
		let E = this.E;

		let URL = E.options.getString("url");
		let BROWSER_NAME = E.options.getString("browser_name");
		let BROWSER_NAME_TEXT = "Chrome";

		if(URL !== undefined && URL !== null){
			//URLの整形
			if(!URL.startsWith("http")){
				URL = "http://" + URL;
			}

			//Chromeのオプションを設定
			const chromeOptions = new chrome.Options();
			chromeOptions.addArguments('--headless'); // ヘッドレスモードで実行
			chromeOptions.addArguments('--window-size=1980,1080'); // ウィンドウのサイズを設定

			if(BROWSER_NAME !== undefined && BROWSER_NAME !== null){
				switch(BROWSER_NAME){
					case"firefox":
						chromeOptions.addArguments("--user-agent=Mozilla/5.0 (X11; Linux x86_64; rv:102.0) Gecko/20100101 Firefox/102.0");
						BROWSER_NAME_TEXT = "FireFox";
						break;
					case"floorp":
						chromeOptions.addArguments("--user-agent=Mozilla/5.0 (X11; Linux x86_64; rv:102.0) Gecko/20100101 Floorp/10.13.0");
						BROWSER_NAME_TEXT = "Floorp";
						break;
					case"rumisan":
						chromeOptions.addArguments("--user-agent=Mozilla/5.0 (X11; Linux x86_64; rv:102.0) Gecko/20100101 Firefox/102.0 Rumisan/" + RUMI_HAPPY_BIRTHDAY() + ".0");
						BROWSER_NAME_TEXT = "るみさん";
						break;
					default:
						E.editReply("ブラウザ名が無効です");
						break;
				}
			}

			try{
				//WebDriverのインスタンスを作成
				const driver = new Builder()
					.forBrowser('chrome')
					.setChromeOptions(chromeOptions)
					.build();

				//ウェブサイトにアクセス
				driver.get(URL).then(() => {
					//スクリーンショットを撮影
					return driver.takeScreenshot();
				}).then(screenshotData => {
					try{
						FS.writeFileSync("./TEMP/" + E.member.id + ".png", screenshotData, "base64");
						
						E.editReply({
							content: "おｋ：" + BROWSER_NAME_TEXT + "で撮影",
							files: ["./TEMP/" + E.member.id + ".png"]
						});
					}catch(EX){
						E.editReply("接続できませんでした！");
					}
				}).catch(() => {
					E.editReply("接続できませんでした！");
					//WebDriverを終了
					driver.quit();
				}).finally(() => {
					//WebDriverを終了
					driver.quit();
				});
			}catch(EX){
				E.editReply("接続できませんでした！");
			}
		}else{
			E.editReply("URLが指定されていません");
		}
	}
}
class test{
	constructor(INTERACTION) {
		this.E = INTERACTION;
	}
	
	async main(){
		let E = this.E;
		const embed = new MessageEmbed()
				.setTitle("テスト")
				.setDescription("これが見れてるってことは、成功したってことです()")
				.setColor(RND_COLOR());
		await E.editReply({ embeds: [embed] });
	}
}
class MISSKEY{
	constructor(){
		this.USER = {
			"9i642yz0h7":{
				"GID":"836142496563068929",
				"CID":"1128742498194444298"
			}
		};
	}

	main(){
		let USER = this.USER;//ユーザー

		//WebSocketサーバーのURL
		const serverURL = "wss://ussr.rumiserver.com/streaming?i=" + CONFIG.MISSKEY_API_KEY;

		//WebSocket接続を作成
		const socket = new WebSocket(serverURL);

		//接続が確立された際のイベントハンドラ
		socket.on('open', () => {
			console.log('WebSocket接続が確立されました。');
		
			//メッセージをサーバーに送信
			socket.send('{"type":"connect","body":{"channel":"localTimeline","id":"1","params":{"withReplies":false}}}');
		});

		//サーバーからメッセージを受信した際のイベントハンドラ
		socket.on('message', (DATA) => {
			const RESULT = JSON.parse(DATA);
			if(RESULT.body.type === "note"){
				let IT_MIS_USER = RESULT.body.body.user;
				let IT_DIS_USER = USER[IT_MIS_USER.id];
				let NOTE_TEXT = RESULT.body.body.text;
				let NOTE_FILES = RESULT.body.body.files;
				let NOTE_ID = RESULT.body.body.id;
				let RENOTE_ID = RESULT.body.body.renoteId;
				let RENOTE_NOTE = RESULT.body.body.renote;
				
				console.log("[ INFO ][ MISSKEY ]Note res:" +NOTE_ID);

				if(IT_DIS_USER !== undefined){
					const EB = new MessageEmbed();
					//ユーザー名
					EB.setTitle(IT_MIS_USER.name);

					//本文
					if(NOTE_TEXT !== undefined && NOTE_TEXT !== null){//本文が有るか
						//ある
						EB.setDescription(NOTE_TEXT);
					}

					//色
					EB.setColor(RND_COLOR());

					//URL
					EB.setURL("https://ussr.rumiserver.com/@" + IT_MIS_USER.id);

					if(NOTE_FILES[0] !== undefined && NOTE_FILES[0] !== null){
						EB.setImage(NOTE_FILES[0].thumbnailUrl);
					}

					//リノート関連
					if(RENOTE_ID !== null && RENOTE_ID !== undefined){//リノートはあるか
						//あるのでリノート元を貼る
						if(RENOTE_NOTE.text !== undefined && RENOTE_NOTE.text !== null){
							EB.addFields({
								name: "リノート元\n" + RENOTE_NOTE.user.name,
								value: RENOTE_NOTE.text,
								inline: false
							});
						}else{
							EB.addFields({
								name: "リノート元\n" + RENOTE_NOTE.user.name,
								value: "[テキストナシ]",
								inline: false
							});
						}
						
						//リノートじの画像
						if(NOTE_FILES[0] === undefined){//既に画像が有るか
							//リノート元に画像は有るか
							if(RENOTE_NOTE.files[0] !== undefined && RENOTE_NOTE.files[0] !== null){
								EB.setImage(RENOTE_NOTE.files[0].thumbnailUrl);
							}
						}
					}

					//アクション
					EB.addFields({
						name: "ｱクション",
						value: "[見に行く](https://ussr.rumiserver.com/notes/" + NOTE_ID +")|"+
								"[何もしない](https://google.com)",
						inline: false
					});

					//そのまま送りつける
					MSG_SEND(IT_DIS_USER.GID, IT_DIS_USER.CID, { embeds: [EB] });
				}
			}
		});

		//エラー発生時のイベントハンドラ
		socket.on('error', (ERR) => {
			console.error('エラーが発生しました:', ERR);
		});

		//接続が閉じられた際のイベントハンドラ
		socket.on('close', (CODE, REASON) => {
			console.log("[ INFO ][ MISSKEY ]Disconnected!" + CODE);
			console.log("[ *** ][ MISSKEY ]Re Connecting...");
			clearInterval(SEND_H);
			main();//再接続する
		});

		let SEND_H =  setInterval(() => {
			socket.send("h");
		}, 60000);
	}
}
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

client.login(CONFIG.TOKEN);
