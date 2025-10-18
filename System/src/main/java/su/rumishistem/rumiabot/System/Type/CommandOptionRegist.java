package su.rumishistem.rumiabot.System.Type;

public class CommandOptionRegist {
	private String name;
	private OptionType type;
	private boolean require;

	public CommandOptionRegist(String name, OptionType type, boolean require) {
		this.name = name;
		this.type = type;
	}

	public String get_name() {
		return name;
	}
	
	public OptionType get_type() {
		return type;
	}

	public boolean is_require() {
		return require;
	}
}
