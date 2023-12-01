import { CONFIG } from "./MODULES/CONFIG.js";
import { client } from "./MODULES/loadClient.js";
// eslint-disable-next-line no-unused-vars
import { MessageEmbed, Message } from "discord.js";
import { RND_COLOR } from "./MODULES/RND_COLOR.js";
import { exec } from "child_process";
import { NULLCHECK } from "./MODULES/NULLCHECK.js";
import { SQL_OBJ, LOCK_NICK_NAME_OBJ } from "./Main.js";

/**
 * BOT管理者が使う奴
 * @param {Message} message メッセージ
 */
export async function BOT_ADMIN(message) {
	//参加済みサーバーの数を表示
	if (message.content === CONFIG.ADMIN_PREFIX + "SLS") {
		console.log("接続済みサーバーの数:", client.guilds.cache.size);
		message.reply("サーバー参加数：「" + client.guilds.cache.size + "」");
	}

	//参加済みサーバーを表示
	if (message.content === CONFIG.ADMIN_PREFIX + "SL") {
		const SERVERS = client.guilds.cache;
		console.log("接続済みサーバーの数:", SERVERS.size);

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
			exec(CMD, (error, stdout, stderr) => {
				if (error) {
					console.error(error)
					message.reply("EXECでエラーが発生");
					return;
				}
				if (stderr) {
					message.reply("```ansi\n" + stderr + "```\nEXIT CODE:NOT 0");
					return;
				}

				let TEXT = stdout;
				TEXT = TEXT.replace(/\x1b\[[\d;]*(:[0-9;]*[Hf])?[A-GSTJK]/g, "");

				message.reply("```ansi\n" + TEXT + "```\nEXIT CODE:0");
			});
		} catch (EX) {
			message.reply(EX);
		}
	}

	//任意コード実行
	if (message.content.startsWith(CONFIG.ADMIN_PREFIX + "EXEC/.")) {
		console.log("任意コードを実行する");
		try {
			const CMD = message.content.replace(CONFIG.ADMIN_PREFIX + "EXEC/.", "");
			console.log(CMD);
			const result = eval(CMD);
			console.log("[ EVAL_RESULT ] ", result);
			message.reply(JSON.stringify(result)?.toString() || "内容が返されませんでした！！");
		} catch (EX) {
			console.error(EX);
			message.reply("<:blod_sad:1155039115709005885> エラー: ```js\n" + EX.stack + "```");
		}
	}

	//招待コード作成
	if (message.content.startsWith(CONFIG.ADMIN_PREFIX + "INV/.")) {
		try {
			const GID = message.content.replace(CONFIG.ADMIN_PREFIX + "INV/.", "");
			let INV_CODE = await client.guilds.cache
				.get(GID)
				.channels.cache.find(ROW => ROW.type === "GUILD_TEXT")
				.createInvite();

			message.reply("https://discord.gg/" + NULLCHECK(INV_CODE.code));
		} catch (EX) {
			console.error(EX);

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
			console.error(EX);

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
			console.error(EX);

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
			console.error(EX);

			message.reply("エラー");
		}
	}

	//ブロックリスト
	if (message.content.startsWith(CONFIG.ADMIN_PREFIX + "BL/.")) {
		try {
			message.reply("以下の人がブロックされてます\n" + JSON.stringify(CONFIG.BLOCK_LIST));
		} catch (EX) {
			console.error(EX);

			message.reply("エラー");
		}
	}

	//好感度
	if (message.content.startsWith(CONFIG.ADMIN_PREFIX + "LIKE/.")) {
		try {
			const UID = message.content.replace(CONFIG.ADMIN_PREFIX + "LIKE/.", "");
			SQL_OBJ.SCRIPT_RUN("SELECT * FROM `LIKABILITY` WHERE `UID` = ?;", [UID])
				.then(RESULT => {
					if (RESULT.length !== 0) {
						let RESULT_ODIN = RESULT[0];

						message.reply("ID:" + RESULT_ODIN.ID + "\n" + "DiscordID" + RESULT_ODIN.UID + "\n" + "好感度:" + RESULT_ODIN.LIKABILITY);
					} else {
						message.reply("誰ですかそれ");
					}
				})
				.catch(EX => {
					console.error(EX);
					message.reply(JSON.stringify(EX));
				});
		} catch (EX) {
			console.error(EX);
		}
	}

	//私のお金
	if (message.content.startsWith(CONFIG.ADMIN_PREFIX + "MONEY/.")) {
		try {
			let RES = await fetch("https://rumiserver.com/API/Rumisan/money.php", {
				method: "GET",
				headers: {
					"Content-Type": "application/json"
				}
			});
			if (RES.ok) {
				const RESULT = await RES.json();
				if (RESULT.STATUS) {
					let REGEX = new RegExp('\\B(?=(\\d{' + 4 + '})+(?!\\d))', 'g');
					message.reply("るみさんのお金は" + RESULT.MONEY.replace(REGEX, ",") + "円だよ");
				} else {
					message.reply("エラー" + RESULT.ERR);
				}
			} else {
				message.reply("取得に失敗" + RES.status);
			}
		} catch (EX) {
			console.error(EX);

			message.reply("エラー");
		}
	}

	//再読込
	if (message.content === CONFIG.ADMIN_PREFIX + "RELOAD") {
		console.log("[ *** ][ BOT ADMIN ]管理者が設定の再読込を要請しました、再読込を開始します...");

		//ニックネーム強制固定
		LOCK_NICK_NAME_OBJ.INIT();

		message.reply("[ OK ]ｼｽﾃﾑの設定を再読込しました");
		console.log("[ *** ][ BOT ADMIN ]ｼｽﾃﾑの設定を再読込しました");
	}
}
