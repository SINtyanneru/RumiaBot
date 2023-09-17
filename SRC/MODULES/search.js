import * as command from "./COMMAND/index.js";

export function search(message) {
	const SEARCH_WORD = message.content.replace("検索 ", "");

	message.react("✅");

	new command.SEARCH(message, SEARCH_WORD).main();
}
