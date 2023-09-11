export class SNS{
	constructor(INTERACTION){
		this.E = INTERACTION;
	}

	async main(){
		let E = this.E;
		const TYPE = E.options.getString("type");

		//インスタンスの設定を取得
		let SNS_CONFIG = CONFIG.SNS.find((ROW) => ROW.ID === TYPE);

		//設定があるか
		if(SNS_CONFIG){
			//ある
			
			await E.editReply(SNS_CONFIG.NAME);
		}else{
			//無い
			await E.editReply("インスタンスが見つかりませんでした");
		}
	}
}