import { CONFIG } from "./MODULES/CONFIG.js";
import { client } from "./MODULES/loadClient.js";
import { MessageEmbed } from "discord.js";
import { RND_COLOR } from "./MODULES/RND_COLOR.js";
import { exec } from "child_process";
import { NULLCHECK } from "./MODULES/NULLCHECK.js";

/**
 * BOT管理者が使う奴
 * @param {*} message メッセージ
 */
export async function BOT_ADMIN(message) {
	//参加済みサーバーの数を表示
	if (message.content === CONFIG.ADMIN_PREFIX + "SLS") {
		console.log(client.guilds.cache.size);
		message.reply("サーバー参加数：「" + client.guilds.cache.size + "」");
	}

	//参加済みサーバーを表示
	if (message.content === CONFIG.ADMIN_PREFIX + "SL") {
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

		message.reply({ embeds: [EB] });
	}

	//シェルコマンド実行
	if (message.content.startsWith(CONFIG.ADMIN_PREFIX + "SHELL/.")) {
		try {
			const CMD = message.content.replace(CONFIG.ADMIN_PREFIX + "SHELL/.", "");
			exec('sh -c "' + CMD + '"', (error, stdout, stderr) => {
				if (error) {
					message.reply("EXECでエラーが発生");
					return;
				}
				if (stderr) {
					message.reply("```sh\n" + stderr + "```\nEXIT CODE:NOT 0");
					return;
				}
				message.reply("```sh\n" + stdout + "```\nEXIT CODE:0");
			});
		} catch (EX) {
			message.reply(EX);
		}
	}

	//任意コード実行
	if (message.content.startsWith(CONFIG.ADMIN_PREFIX + "EXEC/.")) {
		try {
			const CMD = message.content.replace(CONFIG.ADMIN_PREFIX + "EXEC/.", "");
			eval(CMD);
		} catch (EX) {
			console.log(EX);
		}
	}

	//招待コード作成
	if (message.content.startsWith(CONFIG.ADMIN_PREFIX + "INV/.")) {
		try {
			const GID = message.content.replace(CONFIG.ADMIN_PREFIX + "INV/.", "");
			let INV_CODE = await client.guilds.cache.get(GID).channels.cache.first().createInvite();

			message.reply("https://discord.gg/" + NULLCHECK(INV_CODE.code));
		} catch (EX) {
			console.log(EX);

			message.reply("エラー");
		}
	}

	//鯖から抜ける
	if (message.content.startsWith(CONFIG.ADMIN_PREFIX + "LV/.")) {
		try {
			const GID = message.content.replace(CONFIG.ADMIN_PREFIX + "LV/.", "");
			let GUILD = client.guilds.cache.get(GID);
			GUILD.leave();
		} catch (EX) {
			console.log(EX);

			message.reply("エラー");
		}
	}

	//チャンネル一覧取得
	if (message.content.startsWith(CONFIG.ADMIN_PREFIX + "CH_L/.")) {
		try {
			const GID = message.content.replace(CONFIG.ADMIN_PREFIX + "CH_L/.", "");
			let GUILD = client.guilds.cache.get(GID);

			if (GUILD !== undefined) {
				let CH = GUILD.channels.cache;

				let EB = new MessageEmbed()
					.setTitle(GUILD.name)
					.setDescription("合計" + CH.size)
					.setColor(RND_COLOR());

				CH.forEach(CH_INFO => {
					EB.addFields({
						name: CH_INFO.name,
						value: CH_INFO.id,
						inline: true
					});
				});

				message.reply({ embeds: [EB] });
			} else {
				message.reply("鯖がありません");
			}
		} catch (EX) {
			console.log(EX);

			message.reply("エラー");
		}
	}

	//管理者チェック
	if (message.content.startsWith(CONFIG.ADMIN_PREFIX + "PM/.")) {
		//実験用
		try {
			const GID = message.content.replace(CONFIG.ADMIN_PREFIX + "PM/.", "");
			let GUILD = client.guilds.cache.get(GID);
			if (GUILD.members.cache.get(client.user.id).permissions.has("ADMINISTRATOR")) {
				message.reply("はい、それは管理者権限的");
			} else {
				message.reply("あーー！管理者権限がないぞおおお！！！こんな鯖抜けてやる！");
			}
		} catch (EX) {
			console.log(EX);

			message.reply("エラー");
		}
	}
}
