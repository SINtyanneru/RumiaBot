/**
 * スラッシュコマンドの登録
 */
import * as FS from "node:fs";
import { CONFIG } from "./MODULES/CONFIG.js";

export async function REGIST_SLASH_COMMAND(){
	let CMD_DATA = [
		{
			name: "test",
			description: "テストコマンド"
		},
		{
			name: "ping",
			description: "pingします",
			options: [
				{
					name: "host",
					description: "ホスト名",
					type: "STRING",
					required: true
				}
			]
		},
		{
			name: "ferris",
			description: "ウニ？カニ？ヤドカリ？",
			options: [
				{
					name: "type",
					description: "タイプ",
					type: "STRING",
					required: true,
					choices: [
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
					]
				}
			]
		},
		{
			name: "ws",
			description: "ウェブサイトをスクショします",
			options: [
				{
					name: "url",
					description: "ウェブサイトのURLです",
					type: "STRING",
					required: true
				},
				{
					name: "browser_name",
					description: "ブラウザを指定(UAのみ)",
					type: "STRING",
					required: false,
					choices: [
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
					]
				}
			]
		},
		{
			name: "info_server",
			description: "鯖の情報を取得"
		},
		{
			name: "info_user",
			description: "ユーザーの情報を取得",
			options: [
				{
					name: "user",
					description: "ユーザーを指定しろ",
					type: "MENTIONABLE",
					required: true
				}
			]
		},
		{
			name: "info_mine",
			description: "マイクラのユーザーの情報を盗みます",
			options: [
				{
					name: "mcid",
					description: "マイクラのID",
					type: "STRING",
					required: true
				}
			]
		},
		{
			// NOTE 本当はコンテキストメニューを実装する予定だったんだ、
			// だけど、DiscordJSのローカライズ機能がローカライズを履き違えてるから無理だったよ
			// ExpectedConstraintError: Invalid string format
			// 意味がわからないね、ローカライズとは
			name: "kanji",
			description: "漢字を変換します",
			options: [
				{
					name: "text",
					description: "文字列",
					type: "STRING",
					required: true
				},
				{
					name: "mode",
					description: "モード",
					type: "STRING",
					required: true,
					choices: [
						{
							name: "新 => 旧",
							value: "n_o"
						},
						{
							name: "旧 => 新",
							value: "o_n"
						}
					]
				}
			]
		},
		{
			name: "letter",
			description: "文字を色々変換してくれます、たぶん",
			options: [
				{
					name: "text",
					description: "文字列",
					type: "STRING",
					required: true
				},
				{
					name: "old",
					description: "変換前",
					type: "STRING",
					required: true,
					choices: [
						{
							name: "ひらがな",
							value: "hilagana"
						},
						{
							name: "ラテン文字",
							value: "latin"
						}
					]
				},
				{
					name: "new",
					description: "変換後",
					type: "STRING",
					required: true,
					choices: [
						{
							name: "ひらがな",
							value: "hilagana"
						},
						{
							name: "ラテン文字",
							value: "latin"
						}
					]
				}
			]
		},
		{
			name: "help",
			description: "ヘルプコマンド、作るのめんどいやつ",
			options: [
				{
					name: "mode",
					description: "どれを見るか",
					type: "STRING",
					required: true,
					choices: [
						{
							name: "スラッシュコマンド",
							value: "slash"
						},
						{
							name: "メッセージコマンド",
							value: "message"
						}
					]
				}
			]
		},
		{
			name: "ip",
			description: "るみBOTのIPを表示します"
		},
		{
			name: "wh_clear",
			description: "WHをすべてクリアします"
		},
		{
			name: "setting",
			description: "設定します",
			options: [
				{
					name: "mode",
					description: "設定をどうするか",
					type: "STRING",
					required: true,
					choices: [
						{
							name: "有効化",
							value: "true"
						},
						{
							name: "無効化",
							value: "false"
						}
					]
				},
				{
					name: "function",
					description: "どの機能を？",
					type: "STRING",
					required: true,
					choices: [
						{
							name: "VXTwitterに置き換え機能",
							value: "vxtwitter"
						}
					]
				}
			]
		},
		{
			name: "num",
			description: "数字を変換します",
			options: [
				{
					name: "num",
					description: "数字",
					type: "STRING",
					required: true
				},
				{
					name: "input",
					description: "なに数字？",
					type: "STRING",
					required: true,
					choices: [
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
					]
				},
				{
					name: "output",
					description: "変換先",
					type: "STRING",
					required: true,
					choices: [
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
					]
				}
			]
		}
	];
	
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