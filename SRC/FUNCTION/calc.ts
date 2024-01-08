import { Message } from "discord.js";
import * as command from "../COMMAND/index.js";

export function calc(message: Message<boolean>) {
	message.react("✅");

	const RESULT = new command.MATH(message.content).main();

	//結果を吐き出す
	message.reply(RESULT);
}
