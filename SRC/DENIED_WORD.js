export class DENIED_WORD{
	constructor(){
		this.DENIED_WORD_LIST = {
			"836142496563068929":[
				{
					WORD:/ちんこ/g,
					WHITE_LIST:[]
				},{
					WORD:/まんこ/g,
					WHITE_LIST:[]
				},{
					WORD:/まんちん/g,
					WHITE_LIST:[]
				},{
					WORD:/ちん/g,
					WHITE_LIST:[]
				},{
					WORD:/(?:ちん){2}/g,
					WHITE_LIST:[]
				},{
					WORD:/BGA/g,
					WHITE_LIST:[]
				}
			]
		};
	}

	main(MESSAGE){
		try{
			//投稿された鯖が、禁止ワードリストに登録されているか
			if(this.DENIED_WORD_LIST[MESSAGE.guild.id]){
				const DETECTED_WORD = this.DENIED_WORD_LIST[MESSAGE.guild.id].find((ROW) => (ROW.WORD.test(MESSAGE.content)));

				//禁止ワードだったか
				if(DETECTED_WORD){
					//元メッセージを削除
					if(MESSAGE.content){
						MESSAGE.delete();
					}
				}
			}
		}catch(EX){
			console.log("[ ERR ][ DEN_WORD ]" + EX);
			return;
		}
	}
}