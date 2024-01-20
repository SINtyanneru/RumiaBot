import net from "net";
import { client } from "./MODULES/loadClient.js";
import * as crypto from "node:crypto";

const SERVER_URL = "localhost";
const SERVER_PORT = 3001;

let TL_CONNECT = new net.Socket();

export async function pws_main(){
	TL_CONNECT.connect(SERVER_PORT, SERVER_URL, async ()=>{
		console.log("[ PWS ][ OK ]Connected Telnet");

		PWS_SEND_MSG("HELLO;JS").then((R) => {
			console.log(R);
		});
	});


	TL_CONNECT.addListener("data", async (DATA) =>{
		const MSG = DATA.toString();
		const CMD = MSG.split(";");
		if(CMD[0] === "DISCORD"){
			if(CMD[1] === "MSG_SEND"){
				const CH =await client.channels.fetch(CMD[2]);
				if(CH){
					CH.send(CMD[3]);
				}
			}
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

/*
 * NodeにaddEventListnerはないらしい、addListenerを使おう
 */