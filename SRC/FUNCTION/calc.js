import * as command from "../COMMAND/index.js";

export function calc(message) {
	message.react("✅");

	let RESULT = new command.MATH(message.content).main();

	//結果を吐き出す
	message.reply(RESULT);
}
