package su.rumishistem.rumiabot.System.TYPE;

public class CommandOption {
	private String Name;
	private CommandOptionType Type;
	private Object Value;
	private boolean Require;

	public CommandOption(String Name, CommandOptionType Type, Object Value, boolean Require) {
		this.Name = Name;
		this.Type = Type;
		this.Value = Value;
		this.Require = Require;
	}

	public String GetName() {
		return Name;
	}

	public CommandOptionType GetType() {
		return Type;
	}

	public Object GetValueAsObject() {
		return Value;
	}

	public String GetValueAsString() {
		return (String) Value;
	}

	public int GetValueAsInt() {
		return (int) Value;
	}
	
	public boolean isRequire() {
		return Require;
	}
}
