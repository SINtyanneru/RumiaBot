// @ts-check
import { exec } from "child_process";
import { CONFIG } from "../MODULES/CONFIG.js";
import { SlashCommandBuilder } from "@discordjs/builders";
export class PING {
	static command = new SlashCommandBuilder()
		.setName("ping")
		.setDescription("pingします")
		.addStringOption(o => o.setName("host").setDescription("ホスト名").setRequired(true));
	/**
	 *
	 * @param {import("discord.js").CommandInteraction<import("discord.js").CacheType>} INTERACTION
	 */
	constructor(INTERACTION) {
		this.E = INTERACTION;
	}

	async main() {
		try {
			let E = this.E;
			if (CONFIG.DISABLE.includes("ping")) {
				E.editReply("botの管理者がこれを無効化しています");
				return;
			}

			const CMD = E.options.getString("host").replace(/[^A-Za-z0-9\-.]/g, "");
			if (CMD != undefined) {
				//コマンドを実行し、リアルタイムに出力を取得します
				const EXEC = exec('ping -c5 "' + CMD + '"');

				let OUTPUT = ""; //出力を記録
				let COUNT = 0; //出力した回数を記録

				EXEC.stdout.on("data", data => {
					OUTPUT = OUTPUT + data + "\n";
					if (COUNT <= 5) {
						//出力が5以下なら更新する(望んだ動作にするため)
						//編集
						E.editReply(OUTPUT);
					}
					COUNT++;
				});

				EXEC.stderr.on("data", () => {
					//エラーを出す
					OUTPUT = OUTPUT + "PINGがエラーを吐きやがりました\n";
					E.editReply("PINGがエラーを吐きやがりました");
				});

				EXEC.on("close", code => {
					if (code === 0) {
						E.editReply(OUTPUT + "\n" + "返り値が0だから成功したんじゃないかな");
					} else {
						E.editReply(OUTPUT + "\n" + "返り値が「" + code + "」だから失敗したんじゃないかな");
					}
				});
			} else {
				E.editReply("ホストの指定がおかしいね！");
			}
		} catch (EX) {
			console.error("[ ERR ][ PING ]", EX);
			this.E.editReply("エラーだあああ:" + EX);
		}
	}
}
