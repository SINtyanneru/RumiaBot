package su.rumishistem.rumiabot.System.TYPE;

public interface FunctionClass {
	String FUNCTION_NAME();
	String FUNCTION_VERSION();
	String FUNCTION_AUTOR();

	void Init();

	void ReceiveMessage(ReceiveMessageEvent e);

	boolean GetAllowCommand(String Name);

	void RunCommand(CommandInteraction CI) throws Exception;

	default void RunInteraction(RunInteractionEvent Interaction) throws Exception {
	}
	
	default void ReturnInteraction(ReturnInteractionEvent Interaction) throws Exception {
	}

	default void DiscordEventReceive(DiscordEvent e) throws Exception {}
}
