package su.rumishistem.rumiabot.System.TYPE;

public class CommandOption {
	private String Name;
	private CommandOptionType Type;
	private Object Value;

	public CommandOption(String Name, CommandOptionType Type, Object Value) {
		this.Name = Name;
		this.Type = Type;
	}

	public String GetName() {
		return Name;
	}

	public CommandOptionType GetType() {
		return Type;
	}

	public String GetValueAsString() {
		return (String) Value;
	}

	public int GetValueAsInt() {
		return (int) Value;
	}
}
