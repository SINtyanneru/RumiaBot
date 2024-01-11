import { MAP_KILLER } from "./MAP_KILLER.js";
import * as FS from "node:fs";

/**参加してるメンバーをしゅうけいするやつ
* @param {import("discord.js").Client} client
*/
export async function GET_ALL_MEMBERS_COUNT(client){
	//参加しているすべてのサーバーのメンバーを配列に入れる
	let ALL_MEMBERS = [];
	let ALL_GUILDS = MAP_KILLER(client.guilds.cache);
	const ALL_GUILDS_KEY = Object.keys(ALL_GUILDS);
	//全サバを回る
	for (let I_G = 0; I_G < ALL_GUILDS_KEY.length; I_G++) {
		const GUILD = ALL_GUILDS[ALL_GUILDS_KEY[I_G]];
		const MEMBERS = MAP_KILLER(await GUILD.members.fetch());
		const MEMBERS_KEY = Object.keys(MEMBERS);
		//鯖のメンバーを集計
		for (let I_M = 0; I_M < MEMBERS_KEY.length; I_M++) {
			const MEMBER = MEMBERS[MEMBERS_KEY[I_M]];
			if(!ALL_MEMBERS.some((ROW) => ROW === MEMBER.user.id)){
				ALL_MEMBERS.push(MEMBER.user.id);
			}
		}
	}
	return ALL_MEMBERS.length;
}