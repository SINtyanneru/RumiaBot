package su.rumishistem.rumiabot.System.Type;

public class CommandOptionRegist {
	private String name;
	private OptionType type;

	public CommandOptionRegist(String name, OptionType type) {
		this.name = name;
		this.type = type;
	}

	public String get_name() {
		return name;
	}
	
	public OptionType get_type() {
		return type;
	}
}
