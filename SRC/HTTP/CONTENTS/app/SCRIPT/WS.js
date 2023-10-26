const WS = new WebSocket("ws://localhost:3001/");
WS.addEventListener("open", (E) => {
	console.log("WebSocketに接続しました");
});

WS.addEventListener("message", (event) => {
	const MESSAGE = JSON.parse(event.data);
	console.log(MESSAGE);

	console.log(SELECT_CHANNEL_ID);
	console.log(MESSAGE.CHANNEL.ID);

	//メッセージ受信
	if (SELECT_CHANNEL_ID === MESSAGE.CHANNEL.ID) {
		let MESSAGE_EL = document.createElement("DIV");
		MESSAGE_EL.className = "MESSAGE_ITEM";

		let AUTHOR_EL = document.createElement("DIV");
		AUTHOR_EL.className = "MESSAGE_AUTHOR";
		AUTHOR_EL.innerHTML = "<IMG SRC=\"" + MESSAGE.AUTHOR.ICON + "\">" + MESSAGE.AUTHOR.NAME;

		let TEXT_EL = document.createElement("DIV");
		TEXT_EL.className = "MESSAGE_TEXT";
		TEXT_EL.innerText = MESSAGE.MSG.TEXT;


		MESSAGE_EL.appendChild(AUTHOR_EL);
		MESSAGE_EL.appendChild(TEXT_EL);

		//TODO:画像表示を付ける

		MESSAGE_LIST.appendChild(MESSAGE_EL);

		MESSAGE_LIST.scrollTo(0, MESSAGE_LIST.scrollHeight);
	}
});
