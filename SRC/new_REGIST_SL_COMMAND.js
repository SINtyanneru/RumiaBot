/**
 * スラッシュコマンドの登録
 */
import { SlashCommandBuilder } from "@discordjs/builders";
import * as FS from "node:fs";
import { CONFIG } from "./MODULES/CONFIG.js";

export async function REGIST_SLASH_COMMAND() {
	/**@type {SlashCommandBuilder[]} */
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
					.setChoices(
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
			),
		new SlashCommandBuilder()
			.setName("ws")
			.setDescription("ウェブサイトをスクショします")
			.addStringOption(o => o.setName("url").setDescription("ウェブサイトのURLです").setRequired(true))
			.addStringOption(o =>
				o.setName("browser_name").setDescription("ブラウザを指定(UAのみ)".setRequired(false)).setChoices(
					{
						name: "FireFox",
						value: "firefox"
					},
					{
						name: "Floorp",
						value: "floorp"
					},
					{
						name: "るみさん",
						value: "rumisan"
					},
					{
						name: "Chrome",
						value: "chrome"
					}
				)
			),
		new SlashCommandBuilder().setName("info_server").setDescription("鯖の情報を取得"),
		new SlashCommandBuilder()
			.setName("info_user")
			.setDescription("ユーザーの情報を取得")
			.addMentionableOption(o => o.setName("user").setDescription("ユーザーを指定しろ").setRequired(true)),
		new SlashCommandBuilder()
			.setName("info_mine")
			.setDescription("マイクラのユーザーの情報を盗みます")
			.addStringOption(o => o.setName("mcid").setDescription("マイクラのID").setRequired(true)),
		new SlashCommandBuilder()
			.setName("kanji")
			.setDescription("漢字を変換します")
			.addStringOption(o => o.setName("text").setDescription("文字列").setRequired(true))
			.addStringOption(o =>
				o
					.setName("mode")
					.setDescription("モード")
					.setChoices(
						{
							name: "新 => 旧",
							value: "n_o"
						},
						{
							name: "旧 => 新",
							value: "o_n"
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
