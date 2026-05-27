package su.rumishistem.rumisanbot.base_system.Software.RumiaBot.Type;

public class CommandOptionData {
	public final String name;
	public final String description;
	public final boolean is_required;
	public final Type type;
	public final String[] enum_choise_list;

	public enum Type {
		String,
		Interger,
		Enum
	}

	public CommandOptionData(String name, String description, boolean is_required, Type type) {
		this.name = name;
		this.description = description;
		this.is_required = is_required;
		this.type = type;
		this.enum_choise_list = new String[0];
	}

	public CommandOptionData(String name, String description, boolean is_required, String[] enum_choise_list) {
		this.name = name;
		this.description = description;
		this.is_required = is_required;
		this.type = Type.Enum;
		this.enum_choise_list = enum_choise_list;
	}
}
