export function GET_ALL_MEMBERS_COUNT(client){
	//参加しているすべてのサーバーのメンバーを計算する
	let ALL_MEMBERS = 0;
	let ALL_GUILDS = Array.from(client.guilds.cache);
	for (let I = 0; I < ALL_GUILDS.length; I++) {
		const GUILD = ALL_GUILDS[I];
		ALL_MEMBERS = ALL_MEMBERS + GUILD[1].memberCount;
	}

	return ALL_MEMBERS;
}