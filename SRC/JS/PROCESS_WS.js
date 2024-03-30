import net from "net";
import * as crypto from "node:crypto";
import { SQL_OBJ } from "./Main.js";

const SERVER_URL = "localhost";
const SERVER_PORT = 3001;

let TL_CONNECT = new net.Socket();

export async function pws_main(){
	TL_CONNECT.connect(SERVER_PORT, SERVER_URL, async ()=>{
		console.log("[ PWS ][ OK ]Connected Telnet");

		await PWS_SEND_MSG("HELLO;JS");
	});


	TL_CONNECT.addListener("data", async (DATA) =>{
		const MSG = DATA.toString();
		const CMD = MSG.split(";");

		if(CMD[1] === "SQL"){
			const SQL_RESULT = await SQL_OBJ.SCRIPT_RUN(
				decodeURIComponent(atob(CMD[2])),
				JSON.parse(decodeURIComponent(atob(CMD[3])))
			);

			TL_CONNECT.write(CMD[0] + ";" + btoa(encodeURIComponent(JSON.stringify(SQL_RESULT))) + ";200");
		}
	});
}

/**
 * 
 * @param {string} TEXT 命令
 * @returns 
 */
export async function PWS_SEND_MSG(TEXT){
	return new Promise((resolve, reject) => {
		//const CMD = TEXT.split(";");
		const UUID = crypto.randomUUID();

		//命令の返答を待つやつ
		function EV_LISENER(DATA){
			const RESULT = DATA.toString().split(";");
			if(RESULT[0] === UUID){
				//イベントリスナーを破棄
				TL_CONNECT.removeListener("data", EV_LISENER);

				//UUIDを殺す
				RESULT.splice(0, 1);

				//ステータスコードが200か
				if(RESULT[RESULT.length - 1] === "200"){
					//200なのでresolve
					resolve(RESULT);
				}else{//200以外なのでrejectする
					reject(RESULT);
				}
			}
		}

		//命令の返答を待つやつをイベントリスナーに追加
		TL_CONNECT.addListener("data", EV_LISENER);

		//命令を送る
		TL_CONNECT.write(UUID + ";" + TEXT);
	});
}