package su.rumishistem.rumiabot.System.Type;

public class DiscordMessageContext {
	private String name;
	private boolean private_command;
	private RunDiscordMessageContext task;

	public DiscordMessageContext(String name, boolean private_command, RunDiscordMessageContext task) {
		this.name = name;
		this.private_command = private_command;
		this.task = task;
	}

	public String get_name() {
		return name;
	}

	public boolean is_private() {
		return private_command;
	}

	public RunDiscordMessageContext get_task() {
		return task;
	}
}
