class PING{
	constructor(INTERACTION) {
		this.E = INTERACTION;
	}
	
	async main(){
		try{
			let E = this.E;
			const CMD = E.options.getString("host").replace(/[^A-Za-z0-9\-\.]/g, "");
			if(CMD != undefined){
				//コマンドを実行し、リアルタイムに出力を取得します
				const EXEC = exec("ping -c5 \"" + CMD + "\"");
	
				let OUTPUT = "";//出力を記録
				let COUNT = 0;//出力した回数を記録
	
				EXEC.stdout.on('data', (data) => {
					OUTPUT = OUTPUT + data + "\n";
					if(COUNT <= 5){//出力が5以下なら更新する(望んだ動作にするため)
						//編集
						E.editReply(OUTPUT);
					}
					COUNT++;
				});
	
				EXEC.stderr.on('data', (data) => {
					//エラーを出す
					OUTPUT = OUTPUT + "PINGがエラーを吐きやがりました\n";
					E.editReply("PINGがエラーを吐きやがりました");
					
				});
	
				EXEC.on('close', (code) => {
					if(code === 0){
						E.editReply(OUTPUT + "\n"+
									"返り値が0だから成功したんじゃないかな");
					}else{
						E.editReply(OUTPUT + "\n"+
									"返り値が「" + code + "」だから失敗したんじゃないかな");
					}
	
				});
			}else{
				E.editReply("ホストの指定がおかしいね！");
			}
		}catch(EX){
			E.editReply("エラーだあああ:" + EX);
		}
	}
}