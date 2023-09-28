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

		SQL_OBJ.SCRIPT_RUN("SHOW TABLES LIKE 'USER';", []).then(async RESULT => {
			console.log(RESULT);
			TEXT += "[OK]SQL TABLE USER\n";
			await E.editReply(TEXT);
		});
	}
}
