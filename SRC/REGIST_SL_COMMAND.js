// @ts-check
/**
 * スラッシュコマンドの登録
 */
import { SlashCommandBuilder } from "@discordjs/builders";
import { CONFIG } from "./MODULES/CONFIG.js";
import { test } from "./COMMAND/test.js";
import { PING } from "./COMMAND/PING.js";
import { FERRIS } from "./COMMAND/FERRIS.js";
import { WS } from "./COMMAND/WS.js";
import { serverInfo, userInfo, mcInfo } from "./COMMAND/infocommand/index.js";
import { KANJI } from "./COMMAND/KANJI.js";
import { LETTER } from "./COMMAND/LETTER.js";
import { HELP } from "./COMMAND/HELP.js";
import { IP } from "./COMMAND/IP.js";
import { WH_CLEAR } from "./COMMAND/WH_CLEAR.js";
import { SETTING } from "./COMMAND/SETTING.js";
import { NUM } from "./COMMAND/NUM.js";
import { VC_MUSIC } from "./COMMAND/VC_MUSIC.js";
import { Unicode_CODEPOINT } from "./COMMAND/Unicode_CODEPOINT.js";
import { sns_login } from "./COMMAND/sns_login.js";
export async function REGIST_SLASH_COMMAND() {
	const CMD_DATA = [
		test.command,
		PING.command,
		FERRIS.command,
		WS.command,
		serverInfo.command,
		userInfo.command,
		mcInfo.command,
		KANJI.command,
		LETTER.command,
		HELP.command,
		IP.command,
		WH_CLEAR.command,
		SETTING.command,
		NUM.command,
		Unicode_CODEPOINT.command,
	];

	//VC-music
	CMD_DATA.push(await VC_MUSIC.generateCommand());

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

	CMD_DATA.push(
		new SlashCommandBuilder()
			.setName("sns_login")
			.setDescription("ログインしよう！！ちん！")
			.addStringOption(o =>
				o
					.setName("type")
					.setDescription("インスタンス")
					.setChoices(...SC_ActivityPub_CHOICES)
					.setRequired(true)
			)
	);
	return CMD_DATA;
}
