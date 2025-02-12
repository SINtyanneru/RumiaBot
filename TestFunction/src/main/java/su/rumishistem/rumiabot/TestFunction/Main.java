package su.rumishistem.rumiabot.TestFunction;

import su.rumishistem.rumiabot.System.TYPE.FunctionClass;

public class Main implements FunctionClass {
	private static final String FUNCTION_NAME = "Test";
	private static final String FUNCTION_VERSION = "1.0";
	private static final String FUNCTION_AUTOR = "Rumisan";

	@Override
	public String FUNCTION_NAME() {
		return FUNCTION_NAME;
	}
	@Override
	public String FUNCTION_VERSION() {
		return FUNCTION_VERSION;
	}
	@Override
	public String FUNCTION_AUTOR() {
		return FUNCTION_AUTOR;
	}
}
