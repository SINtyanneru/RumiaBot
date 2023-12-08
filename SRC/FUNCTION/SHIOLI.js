/**
 * しおり機能、誰が使うんだ
 */
import { SQL_OBJ } from "../Main.js";

export class SHIOLI{
	constructor(GID, CID, MID, UID, MSG){
		this.GID = GID;
		this.CID = CID;
		this.MID = MID;
		this.UID = UID;
		this.MSG = MSG;
	}

	async SET(){
		try {
			const RESULT = await SQL_OBJ.SCRIPT_RUN("SELECT * FROM `SHIOLI` WHERE `ID` = ?;", [this.UID + this.GID + this.CID,]);
			if(RESULT.length === 0){
				await SQL_OBJ.SCRIPT_RUN("INSERT INTO `SHIOLI` (`ID`, `GID`, `CID`, `MID`, `UID`) VALUES (?,?,?,?,?);",
										[
											this.UID + this.GID + this.CID,
											this.GID,
											this.CID,
											this.MID,
											this.UID
										]);
			}else{
				await SQL_OBJ.SCRIPT_RUN("DELETE FROM `SHIOLI` WHERE `ID` = ?;", [this.UID + this.GID + this.CID,]);
				await SQL_OBJ.SCRIPT_RUN("INSERT INTO `SHIOLI` (`ID`, `GID`, `CID`, `MID`, `UID`) VALUES (?,?,?,?,?);",
										[
											this.UID + this.GID + this.CID,
											this.GID,
											this.CID,
											this.MID,
											this.UID
										]);
			}
			//結果を返す
			await this.MSG.reply("登録した");
		} catch (EX) {
			console.error(EX);
			await this.MSG.reply("登録できんかったわ");
		}
	}

	async LOAD(){
		const RESULT = await SQL_OBJ.SCRIPT_RUN("SELECT * FROM `SHIOLI` WHERE `ID` = ?;", [this.UID + this.GID + this.CID,]);
		if(RESULT.length === 1){
			await this.MSG.reply(`登録されていたしおりです\nhttps://discord.com/channels/${RESULT[0].GID}/${RESULT[0].CID}/${RESULT[0].MID}`);
		}else{
			await this.MSG.reply("登録されてないんだけどーーーー");
		}
	}
}