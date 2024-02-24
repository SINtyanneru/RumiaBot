/*
   グロチャ
 */
import { client } from "../MODULES/loadClient.js";
import { sanitize } from "../MODULES/sanitize.js";
import { WebHook_FIND } from "../MODULES/WebHook_FIND.js";

export class GLOBAL_CHAT{
	/**
	 * @param {import("discord.js").Message<import("discord.js").CacheType>} message
	 */
	constructor(message){
		this.CHANNEL_LIST = [
			{
				CID:"1204015173380083732",
				ROOM:"test"
			},
			{
				CID:"1204043526455693312",
				ROOM:"test"
			}
		];

		this.SEND(message);
	}

	/**
	 * @param {import("discord.js").Message<import("discord.js").CacheType>} message
	 * @param {import("discord.js").TextChannel<import("discord.js").CacheType>} G_CHANNEL
	 */
	async SEND(message){
		try{
			const G_CHANNEL = this.CHANNEL_LIST.find((ROW) => ROW.CID === message.channel.id);

			if(G_CHANNEL){
				let FWH = await message.channel.fetchWebhooks();
				let WH = FWH.find(webhook => webhook.id === message.author.id);

				if(!WH){
					console.log("ルーム" + G_CHANNEL.ROOM + "に送信したよ");
	
					for (let I = 0; I < this.CHANNEL_LIST.length; I++) {
						const CHANNEL_DATA = this.CHANNEL_LIST[I];
						if(CHANNEL_DATA.ROOM === G_CHANNEL.ROOM && CHANNEL_DATA.CID !== message.channel.id){
							const CHANNEL = await client.channels.fetch(CHANNEL_DATA.CID);
							if(CHANNEL){
								let WEB_HOOK = await WebHook_FIND(CHANNEL);
			
								await WEB_HOOK.send({
									username: message.author.username + "@" + message.guild.name,
									avatarURL: "https://cdn.discordapp.com/avatars/" + message.author.id + "/" + message.author.avatar + ".png",
									content: sanitize(message.content)
								});
							}
						}
					}
				}
			}
		}catch(EX){
			console.error(EX);
		}
	}
}