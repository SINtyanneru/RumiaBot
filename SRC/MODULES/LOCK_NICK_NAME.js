import { kazemidori, minto, rumiserver, __four__lkmy, midoriReimuChan, massango, rumisub } from "./SYNTAX_SUGER.js";

export async function LOCK_NICK_NAME(MEMBER) {
	try {
		//るみ鯖無いでの出来事に適応
		if (MEMBER.guild.id === rumiserver) {
			const NICK_LOCK_USER = {
				[kazemidori]: "ねこかわ",
				[minto]: "ミント㌨Да！！",
				[__four__lkmy]: "BaGuAr二世",
				[midoriReimuChan]: "ベジタリアン霊夢",
				[massango]: 'まっさんこ"う"',
				[rumisub]: "Rumi hat alonaaaaaaaaaa"
			};
			const NLU = NICK_LOCK_USER[MEMBER.id.toString()];
			if (NLU) {
				if (NLU.NAME !== MEMBER.nickname) {
					console.log("[ INFO ][ LOCK NICKNAME ]" + MEMBER.user.name + "がニックネームを変えました");
					if (MEMBER.manageable) {
						MEMBER.setNickname(NLU.NAME);
					} else {
						console.log("[ ERR ][ LOCK NICKNAME ]権限不足により変更できませんでした");
						return;
					}
				}
			}
		}
	} catch (EX) {
		console.log("[ ERR ][ LOCK NICKNAME ]" + EX);
		return;
	}
}
