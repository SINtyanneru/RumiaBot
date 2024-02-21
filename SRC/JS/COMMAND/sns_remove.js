import { SlashCommandBuilder } from "@discordjs/builders";
import { SQL_OBJ, SNS_CONNECTION } from "../Main.js";
import { CONFIG } from "../MODULES/CONFIG.js";

export class sns_remove{
	static command = new SlashCommandBuilder()
		.setName("sns_remove")
		.setDescription("設定します")
		.addStringOption(o =>
			o
				.setName("user")
				.setDescription("@ユーザーID@ドメイン")
				.setRequired(true)
		);

	constructor(INTERACTION) {
		this.E = INTERACTION;
	}

	async main() {
		try{
			if (CONFIG.SQL.SQL_CONNECT) {
				const USER = this.E.options.getString("user").split("@");
				const USER_ID = USER[1];
				const DOMAIN = USER[2];
	
				//インスタンスの設定を取得
				let SNS_CONFIG = CONFIG.SNS.find(ROW => ROW.DOMAIN === DOMAIN);
	
				//Nullチェック
				if (SNS_CONFIG) {
					const RES = await fetch("https://" + SNS_CONFIG.DOMAIN + "/api/users/show", {
						method: "POST",
						headers: {
							"Content-Type": "application/json"
						},
						body: JSON.stringify({ username: USER_ID })
					});
	
					if (RES.ok) {
						const RESULT = await RES.json();
						//既に登録されているかをチェック
						let RESULT_SQL = await SQL_OBJ.SCRIPT_RUN("SELECT count(*) FROM `SNS` WHERE `SNS_ID` = ? AND `SNS_UID` = ? AND `CID` = ? AND `GID` = ?; ", [SNS_CONFIG.ID, RESULT.id, this.E.channel.id, this.E.guild.id])
						if (RESULT_SQL[0]["count(*)"] === 1) {
							//SQLから削除
							await SQL_OBJ.SCRIPT_RUN("DELETE FROM `SNS` WHERE `SNS_ID` = ? AND `SNS_UID` = ? AND `CID` = ? AND `GID` = ?; ", [SNS_CONFIG.ID, RESULT.id, this.E.channel.id, this.E.guild.id])

							//再読込
							SNS_CONNECTION.SQL_RELOAD();

							//削除したと伝える
							await this.E.editReply("削除しました");
						}else{
							await this.E.editReply("登録されていないユーザー");
						}
					}else{
						await this.E.editReply("この世に存在しないユーザー");
					}
				}
			}
		}catch(EX){
			await this.E.editReply(EX);
		}
	}
}