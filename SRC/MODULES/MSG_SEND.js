export function MSG_SEND(GID, CID, TEXT){
	client.guilds.cache.get(GID).channels.cache.get(CID).send(TEXT);
}