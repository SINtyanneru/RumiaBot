import { WebSocket } from "ws";

let WS_SOCKET;

export function pws_main(){
	//WebSocketサーバーのURL
	const serverURL = "ws://localhost:3001/?ID=JS";

	//WebSocket接続を作成
	WS_SOCKET = new WebSocket(serverURL);

	//接続が確立された際のイベントハンドラ
	WS_SOCKET.addEventListener("open", async() => {
		console.log("[ OK ][ PWS ]WS Connected!");

		//pingする
		setInterval(async() => {
			await PWS_SEND_MSG("PING");
			console.log("[ INFO ][ PWS ]PING PONG");
		}, 60000);
	});

	
	//サーバーからメッセージを受信した際のイベントハンドラ
	WS_SOCKET.addEventListener("message", async(DATA) => {
		try {
			const RESULT = DATA.toString().split(";");
			console.log("[ INFO ][ PWS ]" + RESULT[RESULT.length - 1] + " OK:\"" + RESULT.join(" ") + "\"");
		} catch (EX) {
			console.error("[ ERR ][ PWS ]" + EX);
			return;
		}
	});

	//エラー発生時のイベントハンドラ
	WS_SOCKET.addEventListener("error", ERR => {
		console.error("エラーが発生しました:", ERR);
	});

	//接続が閉じられた際のイベントハンドラ
	WS_SOCKET.addEventListener("close", (CODE, REASON) => {
		console.log("[ INFO ][ PWS ]Disconnected!" + CODE + "REASON:" + REASON);
	});
}

/**
 * 
 * @param {string} TEXT 命令
 * @returns 
 */
export async function PWS_SEND_MSG(TEXT){
	return new Promise((resolve, reject) => {
		const CMD = TEXT.split(";");

		//命令の返答を待つやつ
		function EV_LISENER(DATA){
			const RESULT = DATA.data.toString().split(";");
			if(RESULT[0] === CMD[0]){
				//イベントリスナーを破棄
				WS_SOCKET.removeEventListener("message", EV_LISENER);

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
		WS_SOCKET.addEventListener("message", EV_LISENER);

		//命令を送る
		WS_SOCKET.send(TEXT);
	});
}