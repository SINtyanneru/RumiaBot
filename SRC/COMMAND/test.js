/*
import { MessageEmbed } from "discord.js";
import { RND_COLOR } from "../MODULES/RND_COLOR.js";
*/

import { SQL_OBJ } from "../Main.js";

export class test {
	constructor(INTERACTION) {
		this.E = INTERACTION;
	}

	async main() {
		let E = this.E;

		let TEXT = "テストを実行します\n";

		await E.editReply(TEXT);

		SQL_OBJ.SCRIPT_RUN("SHOW TABLES LIKE 'USER';", [])
			.then(async () => {
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
}
