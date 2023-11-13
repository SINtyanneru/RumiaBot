// eslint-disable-next-line no-unused-vars
import { GuildMember } from "discord.js";
import { SQL_OBJ } from "../Main.js";
import { CONFIG } from "./CONFIG.js";

/** @param {GuildMember} MEMBER */
export class LOCK_NICK_NAME {
	constructor() {
		this.NICK_LOCK_USER = {};
	}
	//初期化
	INIT() {
		if (CONFIG.DISABLE.includes("locknick")) return;
		let SQL_RESULT = SQL_OBJ.SCRIPT_RUN("SELECT * FROM `NICKNAME_LOCK`; ", []);
		SQL_RESULT.then(RESULT => {
			RESULT.forEach(ROW => {
				if (!this.NICK_LOCK_USER[ROW.GID]) {
					this.NICK_LOCK_USER[ROW.GID] = {};
				}
				this.NICK_LOCK_USER[ROW.GID][ROW.UID] = ROW.NICKNAME;
			});
		})
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
