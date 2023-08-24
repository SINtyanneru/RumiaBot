class MISSKEY{
	constructor(){
		this.USER = {
			"9i642yz0h7":{
				"GID":"836142496563068929",
				"CID":"1128742498194444298"
			}
		};
	}

	main(){
		let USER = this.USER;//ユーザー

		// WebSocketサーバーのURL
		const serverURL = 'wss://ussr.rumiserver.com/streaming?i=0wmcVp8aNuBRZD8lS9E7ArqHNXPZlVtu'; // あなたのサーバーのURLに変更してください

		// WebSocket接続を作成
		const socket = new WebSocket(serverURL);

		// 接続が確立された際のイベントハンドラ
		socket.on('open', () => {
			console.log('WebSocket接続が確立されました。');
		
			//メッセージをサーバーに送信
			socket.send('{"type":"connect","body":{"channel":"localTimeline","id":"1","params":{"withReplies":false}}}');
		});

		// サーバーからメッセージを受信した際のイベントハンドラ
		socket.on('message', (DATA) => {
			const RESULT = JSON.parse(DATA);
			if(RESULT.body.type === "note"){
				let IT_MIS_USER = RESULT.body.body.user;
				let IT_DIS_USER = USER[IT_MIS_USER.id];
				let NOTE_TEXT = RESULT.body.body.text;
				let NOTE_FILES = RESULT.body.body.files;
				let NOTE_ID = RESULT.body.body.id;
				let RENOTE_ID = RESULT.body.body.renoteId;
				let RENOTE_NOTE = RESULT.body.body.renote;
				
				console.log("[ INFO ][ MISSKEY ]Note res:" +NOTE_ID);

				if(IT_DIS_USER !== undefined){
					const EB = new MessageEmbed();
					//ユーザー名
					EB.setTitle(IT_MIS_USER.name);

					//本文
					if(NOTE_TEXT !== undefined && NOTE_TEXT !== null){//本文が有るか
						//ある
						EB.setDescription(NOTE_TEXT);
					}

					//色
					EB.setColor(RND_COLOR());

					//URL
					EB.setURL("https://ussr.rumiserver.com/@" + IT_MIS_USER.id);

					if(NOTE_FILES[0] !== undefined && NOTE_FILES[0] !== null){
						EB.setImage(NOTE_FILES[0].thumbnailUrl);
					}

					//リノート関連
					if(RENOTE_ID !== null && RENOTE_ID !== undefined){//リノートはあるか
						//あるのでリノート元を貼る
						if(RENOTE_NOTE.text !== undefined && RENOTE_NOTE.text !== null){
							EB.addFields({
								name: "リノート元\n" + RENOTE_NOTE.user.name,
								value: RENOTE_NOTE.text,
								inline: false
							});
						}else{
							EB.addFields({
								name: "リノート元\n" + RENOTE_NOTE.user.name,
								value: "[テキストナシ]",
								inline: false
							});
						}
						
						//リノートじの画像
						if(NOTE_FILES[0] === undefined){//既に画像が有るか
							//リノート元に画像は有るか
							if(RENOTE_NOTE.files[0] !== undefined && RENOTE_NOTE.files[0] !== null){
								EB.setImage(RENOTE_NOTE.files[0].thumbnailUrl);
							}
						}
					}

					//アクション
					EB.addFields({
						name: "ｱクション",
						value: "[見に行く](https://ussr.rumiserver.com/notes/" + NOTE_ID +")|"+
								"[何もしない](https://google.com)",
						inline: false
					});

					//そのまま送りつける
					MSG_SEND(IT_DIS_USER.GID, IT_DIS_USER.CID, { embeds: [EB] });
				}
			}
		});

		// エラー発生時のイベントハンドラ
		socket.on('error', (ERR) => {
			console.error('エラーが発生しました:', ERR);
		});

		// 接続が閉じられた際のイベントハンドラ
		socket.on('close', (CODE, REASON) => {
			console.log("[ INFO ][ MISSKEY ]Disconnected!" + CODE);
			console.log("[ *** ][ MISSKEY ]Re Connecting...");
			clearInterval(SEND_H);
			main();//再接続する
		});

		let SEND_H =  setInterval(() => {
			socket.send("h");
		}, 60000);
	}
}