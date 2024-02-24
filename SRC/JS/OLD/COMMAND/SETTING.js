// @ts-check
/**
 * るみBOTの機能を設定する
 */

import { SlashCommandBuilder } from "@discordjs/builders";
import { SQL_OBJ, FUNCTION_SETTING_OBJ } from "../Main.js";
import { CONFIG } from "../../MODULES/CONFIG.js";
// import { FUNCTION_SETTING } from "../FUNCTION_SETTING.js";

export class SETTING {
	static command = new SlashCommandBuilder()
		.setName("setting")
		.setDescription("設定します")
		.addStringOption(o =>
			o
				.setName("function")
				.setDescription("どの機能を？")
				.setChoices({
					name: "VXTwitterに置き換え機能",
					value: "vxtwitter"
				},
				{
					name: "あいさつ機能",
					value: "guten_morgen"
				})
				.setRequired(true)
		)
		.addStringOption(o =>
			o
				.setName("enable")
				.setDescription("設定をどうするか")
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
				.setName("mode")
				.setDescription("モード")
				.setChoices(
					{
						name: "鯖全体の設定",
						value: "GUILD"
					},
					{
						name: "チャンネル",
						value: "CHANNEL"
					}
				)
				.setRequired(true)
		);
	constructor(INTERACTION) {
		this.E = INTERACTION;

		this.FUNCTION_LIST = [
			{
				ID:"vxtwitter",
				GUILD:true,
				CHANNEL:false
			},
			{
				ID:"guten_morgen",
				GUILD:false,
				CHANNEL:true
			}
		];
	}
	async SET() {
		if (CONFIG.SQL.SQL_CONNECT) {
			const FUNCTION = this.E.options.getString("function");
			const ENABLE = this.E.options.getString("enable");
			const MODE = this.E.options.getString("mode");
			const FUNCTION_INFO = this.FUNCTION_LIST.find((ROW) => FUNCTION === ROW.ID);
			if(FUNCTION_INFO){
				if(MODE === "GUILD"){
					if(!FUNCTION_INFO.GUILD){
						await this.E.editReply("鯖全体への適応に未対応の機能です");
						return;
					}
				}
				if(MODE === "CHANNEL"){
					if(!FUNCTION_INFO.CHANNEL){
						await this.E.editReply("チャンネルへの適応に未対応の機能です");
						return;
					}
				}

				//設定処理
				if (ENABLE === "true") {
					//有効化する
					try {
						const RESULT = await SQL_OBJ.SCRIPT_RUN(
							"SELECT * FROM `CONFIG` WHERE `GID` = ? AND `FUNC_ID` = ?",
							[this.E.guild.id, FUNCTION]
						);
	
						if (RESULT.length === 0) {
							try {
								if(MODE === "GUILD"){
									await SQL_OBJ.SCRIPT_RUN(
										"INSERT INTO `CONFIG` (`ID`, `GID`, `CID`, `MODE`, `FUNC_ID`) VALUES (NULL, ?, '', '1', ?);",
										[this.E.guild.id, FUNCTION]
									);
								}else{
									await SQL_OBJ.SCRIPT_RUN(
										"INSERT INTO `CONFIG` (`ID`, `GID`, `CID`, `MODE`, `FUNC_ID`) VALUES (NULL, ?, ?, '2', ?);",
										[this.E.guild.id, this.E.channel.id, FUNCTION]
									);
								}
	
								await this.E.editReply(FUNCTION + "を有効化したとおもいます、たぶん");
								//再読込
								FUNCTION_SETTING_OBJ.LOAD();
							} catch (EX) {
								console.error("[ ERR in Promise ][ SETTING ]", EX);
								await this.E.editReply("エラー\n" + EX);
							}
						} else {
							await this.E.editReply("すでに設定済み");
						}
					} catch (EX) {
						console.error("[ ERR in Promise ][ SETTING ]", EX);
						await this.E.editReply("エラー\n" + EX);
					}
				} else {
					//無効化する
					try {
						let RESULT;
						if(FUNCTION_INFO.GUILD){
							RESULT = await SQL_OBJ.SCRIPT_RUN(
								"SELECT * FROM `CONFIG` WHERE `GID` = ? AND `FUNC_ID` = ?",
								[this.E.guild.id, FUNCTION]
							);
						}

						if(FUNCTION_INFO.CHANNEL){
							RESULT = await SQL_OBJ.SCRIPT_RUN(
								"SELECT * FROM `CONFIG` WHERE `GID` = ? AND `CID` = ? AND `FUNC_ID` = ?",
								[this.E.guild.id, this.E.channel.id, FUNCTION]
							);
						}
	
						if (RESULT.length !== 0) {
							try {
								await SQL_OBJ.SCRIPT_RUN("DELETE FROM `CONFIG` WHERE `ID` = ?", [RESULT[0].ID]);
	
								await this.E.editReply(FUNCTION + "を無効化したとおもいます、たぶん");
								//再読込
								FUNCTION_SETTING_OBJ.LOAD();
							} catch (EX) {
								console.error("[ ERR in Promise ][ SETTING ]", EX);
								await this.E.editReply("エラー\n" + EX);
							}
						} else {
							await this.E.editReply("すでに無効化済み");
						}
					} catch (EX) {
						console.error("[ ERR in Promise ][ SETTING ]", EX);
						await this.E.editReply("エラー\n" + EX);
					}
				}
			}else{
				await this.E.editReply("?");
			}
		} else {
			await this.E.editReply("SQLに接続されていません");
		}
	}
}
