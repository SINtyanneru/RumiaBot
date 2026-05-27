package su.rumishistem.rumisanbot.base_system.Software.RumiaBot.Type;

public class CommandData {
	public final String name;
	public final String description;
	public final CommandOptionData[] option_list;
	public final boolean is_private;
	public final CommandRuner runer;

	public CommandData(String name, String description, CommandOptionData[] option_list, boolean is_private, CommandRuner runer) {
		this.name = name;
		this.description = description;
		this.option_list = option_list;
		this.is_private = is_private;
		this.runer = runer;
	}
}
