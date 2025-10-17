package su.rumishistem.rumiabot.System.Type;

public class CommandData {
	private String name;
	private CommandOptionRegist[] option;
	private boolean private_command;
	private RunCommand task;

	public CommandData(String name, CommandOptionRegist[] option, boolean private_command, RunCommand task) {
		this.name = name;
		this.option = option;
		this.private_command = private_command;
		this.task = task;
	}

	public String get_name() {
		return name;
	}

	public CommandOptionRegist[] get_option() {
		return option;
	}

	public boolean is_private() {
		return private_command;
	}

	public RunCommand get_task() {
		return task;
	}
}
