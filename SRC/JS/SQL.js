/**
 * SQLに繋ぐやつ
 */

import { CONFIG } from "./MODULES/CONFIG.js";
import mysql from "mysql";

export class SQL {
	constructor() {
		//接続する設定に成っているか
		if (CONFIG.SQL.SQL_CONNECT) {
			this.SQL_CONNECTION = mysql.createConnection({
				host: CONFIG.SQL.SQL_HOST,
				user: CONFIG.SQL.SQL_USER,
				password: CONFIG.SQL.SQL_PASS,
				database: CONFIG.SQL.SQL_DB
			});

			this.main();
		}
	}

	main() {
		this.SQL_CONNECTION.connect(ERR => {
			if (ERR) {
				console.error("[ ERR ][ SQL ]" + ERR);
				return;
			} else {
				console.log("[ OK ][ SQL ] Connected!");

				setInterval(async () => {
					await this.SCRIPT_RUN("SHOW TABLES;", []);
				}, 3600000);
			}
		});
	}

	/**
	 * SQL文を実行します
	 * @param {string} SQL_SCRIPT SQL文
	 * @param {(string | never)[]} SQL_PARAM SQL文に必要なパラメーター(無い場合は[]でおｋ)
	 * @returns { Promise<any> }
	 */
	SCRIPT_RUN(SQL_SCRIPT, SQL_PARAM) {
		console.log("[ *** ][ SQL ]RUN SQL Script:" + SQL_SCRIPT.toString() + "/" + SQL_PARAM.toString());
		return new Promise((resolve, reject) => {
			this.SQL_CONNECTION.query(SQL_SCRIPT, SQL_PARAM, (ERR, RESULT) => {
				if (ERR) {
					//エラーチェック
					console.error("[ ERR ][ SQL ]" + ERR);
					reject(ERR);
				} else {
					//せいこうしたので
					console.log("[ OK ][ SQL ]RUN SQL Script OK");
					resolve(RESULT);
				}
			});
		});
	}
}
