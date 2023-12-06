import { SQL_OBJ } from "../Main.js";
import { CONFIG } from "./CONFIG.js";

/** @param {import("discord.js").GuildMember} MEMBER */
export class LOCK_NICK_NAME {
	constructor() {
		this.NICK_LOCK_USER = {};
	}
	//初期化
	INIT() {
		if (CONFIG.DISABLE.includes("locknick")) return;
		let SQL_RESULT = SQL_OBJ.SCRIPT_RUN("SELECT * FROM `NICKNAME_LOCK`; ", []);
		SQL_RESULT.then(RESULT => {
			console.error("[ *** ][ LOCK NICKNAME ]設定をSQLから読み込んでいます...");
			RESULT.forEach(ROW => {
				if (!this.NICK_LOCK_USER[ROW.GID]) {
					this.NICK_LOCK_USER[ROW.GID] = {};
				}
				this.NICK_LOCK_USER[ROW.GID][ROW.UID] = ROW.NICKNAME;
				console.error("[ OK ][ LOCK NICKNAME ]次を読み込みました:" + ROW.GID + "/" + ROW.UID + "=" + ROW.NICKNAME);
			});
			console.error("[ OK ][ LOCK NICKNAME ]全ての設定を再読込しました!");
		});
		SQL_RESULT.catch(EX => {
			console.error("[ ERR ][ LOCK NICKNAME ]" + EX);
		});
	}
	//メイン
	async main(MEMBER) {
		if (CONFIG.DISABLE.includes("locknick")) return;
		try {
			let NICK_NAME = this.NICK_LOCK_USER[MEMBER.guild.id.toString()];
			if (NICK_NAME) {
				NICK_NAME = NICK_NAME[MEMBER.user.id.toString()];
				if (NICK_NAME) {
					if (NICK_NAME !== MEMBER.nickname) {
						console.log("[ INFO ][ LOCK NICKNAME ]" + MEMBER.user.username + "がニックネームを変えました");
						if (MEMBER.manageable) {
							await MEMBER.setNickname(NICK_NAME);
							console.error("[ OK ][ LOCK NICKNAME ]" + MEMBER.user.username + "の名前を変更しました");
						} else {
							console.log("[ ERR ][ LOCK NICKNAME ]権限不足により変更できませんでした");
							return;
						}
					}
				}
			}
		} catch (EX) {
			console.log("[ ERR ][ LOCK NICKNAME ]" + EX);
			return;
		}
	}
}
