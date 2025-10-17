package su.rumishistem.rumiabot.System;

import java.util.HashMap;

import su.rumishistem.rumiabot.System.Type.CommandData;
import su.rumishistem.rumiabot.System.Type.CommandOptionRegist;
import su.rumishistem.rumiabot.System.Type.RunCommand;

public class CommandRegister {
	private static HashMap<String, CommandData> command_table = new HashMap<>();

	public static void add_command(String name, CommandOptionRegist[] option_list, boolean private_command, RunCommand task) {
		command_table.put(name.toUpperCase(), new CommandData(name, option_list, private_command, task));
	}

	public static CommandData get(String name) {
		return command_table.get(name.toUpperCase());
	}
}
