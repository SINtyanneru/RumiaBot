/**
 * スラッシュコマンドの登録
 */
import { SlashCommandBuilder } from "@discordjs/builders";
import * as FS from "node:fs";
import { CONFIG } from "./MODULES/CONFIG.js";

export async function REGIST_SLASH_COMMAND() {
	const CMD_DATA = [];
	CMD_DATA.push(
		new SlashCommandBuilder().setName("test").setDescription("テストコマンド"),
		new SlashCommandBuilder()
			.setName("ping")
			.setDescription("pingします")
			.addStringOption(o => o.setName("host").setDescription("ホスト名").setRequired(true)),
		new SlashCommandBuilder()
			.setName("ferris")
			.setDescription("ウニ？カニ？ヤドカリ？")
			.addStringOption(o =>
				o
					.setName("type")
					.setDescription("タイプ")
					.addChoices(
						{
							name: "コンパイルできません",
							value: "not_compile"
						},
						{
							name: "パニックします！",
							value: "panic"
						},
						{
							name: "アンセーフなコードを含みます",
							value: "un_safe"
						},
						{
							name: "求められた振る舞いをしません",
							value: "not_desired_behavior"
						}
					)
					.setRequired(true)
			)
	);

	//VC-music
	CMD_DATA.push(
		await (function () {
			return new Promise((resolve, reject) => {
				FS.readdir("./DATA/MUSIC", (ERR, FILES) => {
					if (ERR) {
						console.error("[ EER ][ FS ]ファイル一覧取得に失敗しました\n", ERR);
						reject();
					}

					let SC_VC_MUSIC = [];

					FILES.forEach(FILE => {
						SC_VC_MUSIC.push({
							name: FILE,
							value: FILE
						});
					});

					resolve({
						name: "vc_music",
						description: "VCに曲を垂れ流します",
						options: [
							{
								name: "file",
								description: "どの曲",
								type: "STRING",
								required: true,
								choices: SC_VC_MUSIC
							}
						]
					});
				});
			});
		})()
	);

	//ActivityPub
	let SC_ActivityPub_CHOICES = [];
	CONFIG.SNS.forEach(DATA => {
		SC_ActivityPub_CHOICES.push({
			name: DATA.NAME,
			value: DATA.ID
		});
	});

	CMD_DATA.push({
		name: "sns_set",
		description: "SNSを",
		options: [
			{
				name: "type",
				description: "どのインスタンスを？",
				type: "STRING",
				required: true,
				choices: SC_ActivityPub_CHOICES
			},
			{
				name: "username",
				description: "誰を？",
				type: "STRING",
				required: true
			}
		]
	});

	return CMD_DATA;
}
