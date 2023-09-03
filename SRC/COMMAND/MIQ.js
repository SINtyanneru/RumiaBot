class MIQ{
	load_miq(message){
		try{
			const MSG_ID = message.id;
			const DWN_PATH = PATH.join("DOWNLOAD", "MIQ", MSG_ID + ".png");
	
	
			if (FS.existsSync(DWN_PATH)) {
				message.channel.send({
					content: "🇨🇳🇨🇳🇨🇳削除を検知！！！！🇨🇳🇨🇳🇨🇳",
					files:[DWN_PATH]
				})
			}
		}catch(EX){
			console.log("[ ERR ][ MIQ ]" + EX);
			return;
		}
	}

	save_miq(message){
			//ダウンロード先
			const DOWNLOAD_URL = message.attachments.map(attachment => attachment.url)[0];
			//保存先
			const DWN_PATH = PATH.join("DOWNLOAD", "MIQ", message.id + ".png");
			
			//ファイルを作るやつ
			const FILE_STREAM = FS.createWriteStream(DWN_PATH);
			
			//ダウンロード開始
			console.error("[ *** ][ MIQDL ]Downloading...");
			https.get(DOWNLOAD_URL, RES => {
				RES.pipe(FILE_STREAM);
			
				RES.on('end', () => {//完了
					console.error("[ OK ][ MIQDL ]Donwloaded");
				});
			}).on('error', EX => {
				console.error("[ ERR ][ MIQDL ]" + EX);
			});
	}
}