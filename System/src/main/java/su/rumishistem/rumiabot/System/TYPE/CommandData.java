package su.rumishistem.rumiabot.System.TYPE;

public class CommandData {
	private String Name;
	private CommandOption[] OptionList;
	private boolean Private;

	public CommandData(String Name, CommandOption[] OptionList, boolean Private) {
		this.Name = Name;
		this.OptionList = OptionList;
		this.Private = Private;
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

	public boolean isPrivate() {
		return Private;
	}
}
