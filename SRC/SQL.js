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
}
