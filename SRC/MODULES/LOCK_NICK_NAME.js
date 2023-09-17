import {
	kazemidori, minto,
	rumiserver, __four__lkmy,
	midoriReimuChan,
	massango
} from "./SYNTAX_SUGER.js";

export async function LOCK_NICK_NAME(MEMBER) {
	try {
		//るみ鯖無いでの出来事に適応
		if (MEMBER.guild.id === rumiserver) {
			const NICK_LOCK_USER = new Map();
			NICK_LOCK_USER.set(kazemidori, { NAME: "BaGuAr二世" });
			NICK_LOCK_USER.set(minto, { NAME: "ミント㌨Да！！" });
			NICK_LOCK_USER.set(__four__lkmy, { NAME: "BaGuAr二世" });
			NICK_LOCK_USER.set(midoriReimuChan, { NAME: "緑霊夢" });
			NICK_LOCK_USER.set(massango, { NAME: 'まっさんご"う"' });

			const NLU = NICK_LOCK_USER.get[MEMBER.id.toString()];
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
