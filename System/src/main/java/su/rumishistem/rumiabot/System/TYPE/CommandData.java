package su.rumishistem.rumiabot.System.TYPE;

public class CommandData {
	private String Name;
	private CommandOption[] OptionList;

	public CommandData(String Name, CommandOption[] OptionList) {
		this.Name = Name;
		this.OptionList = OptionList;
	}

	public String GetName() {
		return Name;
	}

	public CommandOption[] GetOptionList() {
		return OptionList;
	}

	public CommandOption GetOption(String Name) {
		for (CommandOption Option:OptionList) {
			if (Option.GetName().equals(Name)) {
				return Option;
			}
		}

		return null;
	}
}
