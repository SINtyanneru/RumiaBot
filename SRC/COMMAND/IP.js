export class IP {
	constructor(INTERACTION) {
		this.E = INTERACTION;
	}

	async main() {
		let E = this.E;

		const RES = await fetch("https://api.ipify.org?format=json", {
			method: "GET",
			headers: {
				"Content-Type": "application/json"
			}
		});

		if (RES.ok) {
			const RESULT = await RES.json();
			await E.editReply("私のIPは" + RESULT.ip + "です！");
		} else {
			await E.editReply("取得失敗");
		}
	}
}
