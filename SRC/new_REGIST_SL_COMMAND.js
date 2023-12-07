// @ts-check
/**
 * スラッシュコマンドの登録
 */
import { SlashCommandBuilder } from "@discordjs/builders";
import * as FS from "node:fs";
import { CONFIG } from "./MODULES/CONFIG.js";

export async function REGIST_SLASH_COMMAND() {
	const CMD_DATA = [
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
				o.setName("browser_name").setDescription("ブラウザを指定(UAのみ)").setRequired(false).setChoices(
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
			),
		new SlashCommandBuilder()
			.setName("letter")
			.setDescription("文字を色々変換してくれます、たぶん")
			.addStringOption(o =>
				o
					.setName("old")
					.setDescription("変換前")
					.setChoices(
						{
							name: "ひらがな",
							value: "hilagana"
						},
						{
							name: "ラテン文字",
							value: "latin"
						}
					)
					.setRequired(true)
			)
			.addStringOption(o =>
				o
					.setName("new")
					.setDescription("変換後")
					.setChoices(
						{
							name: "ひらがな",
							value: "hilagana"
						},
						{
							name: "ラテン文字",
							value: "latin"
						}
					)
					.setRequired(true)
			),
		new SlashCommandBuilder()
			.setName("help")
			.setDescription("ヘルプコマンド、作るのめんどいやつ")
			.addStringOption(o =>
				o
					.setName("mode")
					.setDescription("どれを見るか")
					.setChoices(
						{
							name: "スラッシュコマンド",
							value: "slash"
						},
						{
							name: "メッセージコマンド",
							value: "message"
						}
					)
					.setRequired(true)
			),
		new SlashCommandBuilder().setName("ip").setDescription("るみBOTのIPを表示します"),
		new SlashCommandBuilder().setName("wh_clear").setDescription("WHをすべてクリアします"),
		new SlashCommandBuilder()
			.setName("setting")
			.setDescription("設定します")
			.addStringOption(o =>
				o
					.setName("設定をどうするか")
					.setChoices(
						{
							name: "有効化",
							value: "true"
						},
						{
							name: "無効化",
							value: "false"
						}
					)
					.setRequired(true)
			)
			.addStringOption(o =>
				o
					.setName("function")
					.setDescription("どの機能を？")
					.setChoices({
						name: "VXTwitterに置き換え機能",
						value: "vxtwitter"
					})
					.setRequired(true)
			),
		new SlashCommandBuilder()
			.setName("num")
			.setDescription("数字を変換します")
			.addStringOption(o => o.setName("num").setDescription("数字").setRequired(true))
			.addStringOption(o =>
				o
					.setName("input")
					.setDescription("なに数字？")
					.setChoices(
						{
							name: "アラビア数字",
							value: "national_arabic"
						},
						{
							name: "アラビア数字 日本式区切り",
							value: "national_arabic"
						},
						{
							name: "アラビア数字 アメリカ式区切り",
							value: "national_arabic_usa"
						}
					)
					.setRequired(true)
			)
			.addStringOption(o =>
				o
					.setName("output")
					.setDescription("変換先")
					.setChoices(
						{
							name: "アラビア数字",
							value: "national_arabic"
						},
						{
							name: "アラビア数字 日本式区切り",
							value: "national_arabic_jp"
						},
						{
							name: "アラビア数字 アメリカ式区切り",
							value: "national_arabic_usa"
						},
						{
							name: "ローマ数字",
							value: "roma"
						}
					)
					.setRequired(true)
			)
	];

	//VC-music
	CMD_DATA.push(
		await (async () => {
			const FILES = await FS.promises.readdir("./DATA/MUSIC");
			const SC_VC_MUSIC = [];

			FILES.forEach(FILE => {
				SC_VC_MUSIC.push({
					name: FILE,
					value: FILE
				});
			});

			return new SlashCommandBuilder()
				.setName("vc_music")
				.setDescription("VCに曲を垂れ流します")
				.addStringOption(o =>
					o
						.setName("file")
						.setDescription("どの曲")
						.addChoices(...SC_VC_MUSIC)
				);
		})()
	);

	//ActivityPub
	/** @type {{name:string,value:string}[]} */
	const SC_ActivityPub_CHOICES = [];
	CONFIG.SNS.forEach(DATA => {
		SC_ActivityPub_CHOICES.push({
			name: DATA.NAME,
			value: DATA.ID
		});
	});
	CMD_DATA.push(
		new SlashCommandBuilder()
			.setName("sns_set")
			.setDescription("SNSを")
			.addStringOption(o =>
				o
					.setName("type")
					.setDescription("どのインスタンスを？")
					.setChoices(...SC_ActivityPub_CHOICES)
					.setRequired(true)
			)
			.addStringOption(o => o.setName("username").setDescription("誰を？").setRequired(true))
	);
	return CMD_DATA;
}
