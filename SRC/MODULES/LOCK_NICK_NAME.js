// eslint-disable-next-line no-unused-vars
import { GuildMember } from "discord.js";
import { kazemidori, minto, rumiserver, __four__lkmy, midoriReimuChan, massango, rumisub, p_nsk, hassy1216, rusklabo, azusa } from "./SYNTAX_SUGER.js";
/** @param {GuildMember} MEMBER */
export async function LOCK_NICK_NAME(MEMBER) {
	try {
		//るみ鯖無いでの出来事に適応
		if (MEMBER.guild.id === rumiserver) {
			const NICK_LOCK_USER = {
				[kazemidori]: "もふもふ風緑",
				[minto]: "ミント㌨Да！！",
				[__four__lkmy]: "BaGuAr二世",
				[midoriReimuChan]: "ベジタリアン霊夢",
				[massango]: 'もふもふまっさんこ"う"',
				[rumisub]: "Rumi hat alonaaaaaaaaaa",
				[p_nsk]: "プヌスク㌨",
				[hassy1216]: " もふもふhassyTK",
				[rusklabo]: " もふもふラスクラボ",
				[azusa]: "†行動の代償† もふもふ梓"
			};

			const NLU = NICK_LOCK_USER[MEMBER.user.id.toString()];
			if (NLU) {
				if (NLU !== MEMBER.nickname) {
					console.log("[ INFO ][ LOCK NICKNAME ]" + MEMBER.user.username + "がニックネームを変えました");
					if (MEMBER.manageable) {
						await MEMBER.setNickname(NLU);
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
