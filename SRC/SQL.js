/**
 * SQLに繋ぐやつ
 */

import { CONFIG } from "./MODULES/CONFIG.js";
import mysql from "mysql";

export class SQL {
	constructor() {
		//接続する設定に成っているか
		if (CONFIG.SQL_CONNECT) {
			this.SQL_CONNECTION = mysql.createConnection({
				host: CONFIG.SQL_HOST,
				user: CONFIG.SQL_USER,
				password: CONFIG.SQL_PASS,
				database: CONFIG.SQL_DB
			});

			this.main();
		}
	}

	main() {
		this.SQL_CONNECTION.connect(ERR => {
			if (ERR) {
				console.log("[ ERR ][ SQL ]" + ERR);
				return;
			} else {
				console.log("[ OK ][ SQL ] Connected!");
			}
		});
	}

	/**
	 * SQL文を実行します
	 * @param {String} SQL_SCRIPT SQL文
	 * @param {Array} SQL_PARAM SQL文に必要なパラメーター(無い場合は[]でおｋ)
	 * @returns { Promise<string,Error> }
	 */
	SCRIPT_RUN(SQL_SCRIPT, SQL_PARAM) {
		return new Promise((resolve, reject) => {
			this.SQL_CONNECTION.query(SQL_SCRIPT, SQL_PARAM, (ERR, RESULT) => {
				if (ERR) {
					//エラーチェック
					console.log("[ ERR ]MySQL Connect err:" + ERR);
					reject(ERR);
				} else {
					//せいこうしたので
					resolve(RESULT);
				}
			});
		});
	}
}
