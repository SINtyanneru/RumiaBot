/*
import { MessageEmbed } from "discord.js";
import { RND_COLOR } from "../MODULES/RND_COLOR.js";
*/
import { MessageActionRow, MessageButton } from "discord.js";
import { WebHook_FIND } from "../MODULES/WebHook_FIND.js";
import { SQL_OBJ } from "../Main.js";

export class test {
	constructor(INTERACTION) {
		this.E = INTERACTION;
	}

	async main() {
		try {
			let E = this.E;

			const row = new MessageActionRow().addComponents(new MessageButton().setCustomId("test").setLabel("こゃーん").setStyle("PRIMARY"));

			let TEXT = "テストを実行します\n";

			await E.editReply({
				content: TEXT,
				components: [row]
			});

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
		} catch (EX) {
			console.log("[ ERR ][ TEST ]" + EX);
		}
	}

	async button() {
		try {
			let WEB_HOOK = await WebHook_FIND(this.E.channel);

			WEB_HOOK.send({
				username: this.E.user.username,
				avatarURL: "https://cdn.discordapp.com/avatars/" + this.E.user.id + "/" + this.E.user.avatar + ".png",
				content: "こゃーん"
			});

			await this.E.reply({
				content: this.E.user.username + "は言った「こゃゃーん」",
				ephemeral: true // このオプションを true にすると他のユーザーには見えません
			});
		} catch (EX) {
			console.log("[ ERR ][ TEST ]" + EX);
		}
	}
}
