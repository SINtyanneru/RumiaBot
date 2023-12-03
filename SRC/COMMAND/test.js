/*
import { MessageEmbed } from "discord.js";
import { RND_COLOR } from "../MODULES/RND_COLOR.js";
*/
import { MessageActionRow, MessageButton } from "discord.js";

import { SQL_OBJ } from "../Main.js";

export class test {
	constructor(INTERACTION) {
		this.E = INTERACTION;
	}

	async main() {
		let E = this.E;

		const row = new MessageActionRow().addComponents(
			new MessageButton()
				.setCustomId('test')
				.setLabel('こゃーん')
				.setStyle('PRIMARY')
		);

		let TEXT = "テストを実行します\n";

		await E.editReply(
			{
				content: TEXT,
				components: [row],
			}
		);

		SQL_OBJ.SCRIPT_RUN("SHOW TABLES LIKE 'USER';", [])
			.then(async (ROW) => {
				TEXT += "[ OK ]SQL TABLE USER\n";
				await E.editReply(TEXT);
			})
			.catch(async EX => {
				console.error("[ ERR ][ TEST ][ TABLE USER ]", EX);
				TEXT += "[ ERR ]SQL TABLE USER\n";
				await E.editReply(TEXT);
			});

		SQL_OBJ.SCRIPT_RUN("SHOW TABLES LIKE 'CONFIG';", [])
			.then(async () => {
				TEXT += "[ OK ]SQL TABLE CONFIG\n";
				await E.editReply(TEXT);
			})
			.catch(async EX => {
				console.error("[ ERR ][ TEST ][ TABLE CONFIG ]", EX);

				TEXT += "[ ERR ]SQL TABLE CONFIG\n";
				await E.editReply(TEXT);
			});

		SQL_OBJ.SCRIPT_RUN("SHOW TABLES LIKE 'SNS';", [])
			.then(async () => {
				TEXT += "[ OK ]SQL TABLE SNS\n";
				await E.editReply(TEXT);
			})
			.catch(async EX => {
				console.error("[ ERR ][ TEST ][ TABLE SNS ]", EX);
				TEXT += "[ ERR ]SQL TABLE SNS\n";
				await E.editReply(TEXT);
			});
	}

	async button() {
		await this.E.reply({
			content: "こゃゃーん",
			ephemeral: true, // このオプションを true にすると他のユーザーには見えません
		});
	}
}
