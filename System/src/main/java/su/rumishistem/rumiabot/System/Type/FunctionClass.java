package su.rumishistem.rumiabot.System.Type;

public interface FunctionClass {
	String function_name();
	String function_version();
	String function_author();

	void init();
	
	default void event_receive() {}
}
