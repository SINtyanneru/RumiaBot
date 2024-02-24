export function MSG_SEND(client, GID, CID, TEXT) {
	client.guilds.cache.get(GID).channels.cache.get(CID).send(TEXT);
}
