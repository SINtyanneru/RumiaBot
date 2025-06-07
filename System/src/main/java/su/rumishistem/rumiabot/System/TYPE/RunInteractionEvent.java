package su.rumishistem.rumiabot.System.TYPE;

import net.dv8tion.jda.api.events.interaction.command.MessageContextInteractionEvent;
import net.dv8tion.jda.api.events.interaction.component.ButtonInteractionEvent;

public class RunInteractionEvent {
	private Object Interaction;
	private InteractionType Type;

	public enum InteractionType {
		Button,
		MessageContext,
	}

	public RunInteractionEvent(Object Interaction, InteractionType Type) {
		this.Interaction = Interaction;
		this.Type = Type;
	}

	public InteractionType getType() {
		return Type;
	}

	public ButtonInteractionEvent getButton() {
		if (Type == InteractionType.Button) {
			return (ButtonInteractionEvent) Interaction;
		} else {
			throw new Error("ボタンInteractionではない。");
		}
	}

	public MessageContextInteractionEvent getMessageContext() {
		if (Type == InteractionType.Button) {
			return (MessageContextInteractionEvent) Interaction;
		} else {
			throw new Error("MessageContextではない。");
		}
	}
}
