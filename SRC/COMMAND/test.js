import {MessageEmbed} from 'discord.js';
import { RND_COLOR } from '../MODULES/RND_COLOR.js';
export class test{
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