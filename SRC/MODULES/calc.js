import * as command from "../COMMAND/index.js";

export function calc(message) {
	const MATH_TEXT = message.content
		.replace("計算 ", "")
		.replace("×", "*")
		.replace("÷", "/")
		.replace(/[^0-9\-+*/().]/g, "");

	message.react("✅");
	console.log(MATH_TEXT);

	let RESULT = new command.MATH(message.content).main();

	//結果を吐き出す
	message.reply(RESULT);
}
