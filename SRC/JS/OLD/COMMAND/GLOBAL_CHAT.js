/*
   グローバルチャット
 */
import { SlashCommandBuilder } from "@discordjs/builders";

export class GLOBAL_CHAT_CMD{
	static command = {
		JOIN:new SlashCommandBuilder().setName("global_chat_join").setDescription("グロチャの参加"),
		LEFT:new SlashCommandBuilder().setName("global_chat_left").setDescription("グロチャから脱退")
	};

	/**
	 * @param {import("discord.js").CommandInteraction<import("discord.js").CacheType> | import("discord.js").ButtonInteraction<import("discord.js").CacheType>} INTERACTION
	 */
	constructor(INTERACTION){
		this.E = INTERACTION;
	}

	async JOIN(){
		await this.E.editReply("test");
	}

	async LEFT(){
		await this.E.editReply("出ていけ！");
	}
}