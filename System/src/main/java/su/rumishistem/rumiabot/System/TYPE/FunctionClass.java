package su.rumishistem.rumiabot.System.TYPE;

import net.dv8tion.jda.api.events.interaction.component.ButtonInteractionEvent;

public interface FunctionClass {
	String FUNCTION_NAME();
	String FUNCTION_VERSION();
	String FUNCTION_AUTOR();

	void Init();
	void ReceiveMessage(ReceiveMessageEvent e);

	boolean GetAllowCommand(String Name);
	void RunCommand(CommandInteraction CI) throws Exception;
	default void RunButton(ButtonInteractionEvent BI) throws Exception {
		BI.reply("このボタンの応答に対応する機能が存在しません").queue();
	}
}
