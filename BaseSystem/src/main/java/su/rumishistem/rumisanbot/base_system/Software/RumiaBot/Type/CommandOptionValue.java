package su.rumishistem.rumisanbot.base_system.Software.RumiaBot.Type;

public class CommandOptionValue {
	private final Object value;
	public final Type type;

	public enum Type {
		String,
		Int,
		Boolean,
		User
	}

	public CommandOptionValue(Object value, Type type) {
		this.value = value;
		this.type = type;
	}

	public String get_as_string() {
		return (String)value;
	}

	public int get_as_int() {
		return (int)value;
	}

	public boolean get_as_boolean() {
		return (boolean)value;
	}
}
