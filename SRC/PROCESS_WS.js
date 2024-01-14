import { WebSocket } from "ws";

export function pws_main(){
	//WebSocketサーバーのURL
	const serverURL = "ws://localhost:3001/?ID=JS";

	//WebSocket接続を作成
	const socket = new WebSocket(serverURL);

	//接続が確立された際のイベントハンドラ
	socket.on("open", () => {
		console.log("[ OK ][ PWS ]WS Connected!");

		//メッセージをサーバーに送信
		socket.send("HELLO;JS");
	});

	//サーバーからメッセージを受信した際のイベントハンドラ
	socket.on("message", async(DATA) => {
		try {
			const RESULT = DATA.toString().split(";");
			if(RESULT[RESULT.length - 1] === "200"){
				console.log("[ INFO ][ PWS ]200 OK:\"" + RESULT.join(" ") + "\"");
			}else{
				console.log("[ INFO ][ PWS ]" + RESULT[RESULT.length] + " NG:\"" + RESULT.join("\b") + "\"");
			}
		} catch (EX) {
			console.error("[ ERR ][ PWS ]" + EX);
			return;
		}
	});

	//エラー発生時のイベントハンドラ
	socket.on("error", ERR => {
		console.error("エラーが発生しました:", ERR);
	});

	//接続が閉じられた際のイベントハンドラ
	socket.on("close", (CODE, REASON) => {
		console.log("[ INFO ][ PWS ]Disconnected!" + CODE + "REASON:" + REASON);
	});
}