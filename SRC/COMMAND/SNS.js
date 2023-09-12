import { CONFIG } from "../MODULES/CONFIG.js";
export class SNS {
	constructor(INTERACTION) {
		this.E = INTERACTION;
	}

	async main() {
		let E = this.E;
		const TYPE = E.options.getString("type");
		const USER_NAME = E.options.getString("userid");

		//インスタンスの設定を取得
		let SNS_CONFIG = CONFIG.SNS.find((ROW) => ROW.ID === TYPE);

		//設定があるか
		if (SNS_CONFIG) {
			//ある
			if (SNS_CONFIG.TYPE === "MISSKEY") {
				const RES = await fetch("https://" + SNS_CONFIG.DOMAIN + "/api/users/show", {
					method: "POST",
					headers: {
						"Content-Type": "application/json",
					},
					body: JSON.stringify({username: USER_NAME})
				});

				if (RES.ok) {
					const RESULT = await RES.json();
					console.log(RESULT);
	
					await E.editReply(RESULT.id);
				}else{
					if(RES.status === 404){
						await E.editReply("指定されたアカウントは見つかりませんでした！");
					}else{
						await E.editReply("APIがエラーを吐きました");
					}
				}
			} else {
				await E.editReply("Misskeyしかまだ対応してない");
			}
		} else {
			//無い
			await E.editReply("インスタンスが見つかりませんでした");
		}
	}
}