const FS = require('fs');
const {Client, GatewayIntentBits } = require('discord.js');

let CONFIG = {};

try {
	const DATA = FS.readFileSync('./Config.json', 'utf8');
	CONFIG = JSON.parse(DATA);
}catch(EX){
	console.error("[ ERR ]Config file load ERR");
	return;
}


const client = new Client({
	intents:[
		GatewayIntentBits.Guilds,
		GatewayIntentBits.GuildMessages,
		GatewayIntentBits.MessageContent
	],
});

client.once('ready',async ()=>{
	console.log("BOT is online!");

	const commandData = [
		{
			name: "test",
			description: "テストコマンド",
		},
	];
	
	try{
		// 全てのサーバーに対してスラッシュコマンドを登録
		await client.application.commands.set(commandData);
		console.log('Global slash commands registered!');
	}catch(EX){
		console.error('Error registering global slash commands:', EX);
	}
});

client.on('messageCreate', (message) => {
	if(message.author.bot){
		return;
	}
	
	
	
});

client.on('interactionCreate', async (interaction) => {
	if(!interaction.isCommand()){
		//コマンドが送信されたか確認
		return;
	};

	console.log(interaction);

	const command = interaction.commandName;

	switch (command) {
		case 'test':
			interaction.deferReply();
			interaction.reply("はい");
			break;
	}
});


client.login(CONFIG.TOKEN);

