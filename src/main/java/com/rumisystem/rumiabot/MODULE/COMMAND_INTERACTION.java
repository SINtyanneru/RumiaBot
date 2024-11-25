package com.rumisystem.rumiabot.MODULE;

import java.util.HashMap;
import java.util.List;

public class COMMAND_INTERACTION {
	private String NAME;
	private List<HashMap<String, String>> OPTION_LIST;

	public COMMAND_INTERACTION(String NAME, List<HashMap<String, String>> OPTION_LIST) {
		this.NAME = NAME;
		this.OPTION_LIST = OPTION_LIST;
	}
}
