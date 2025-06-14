package su.rumishistem.rumiabot.System.TYPE;

import net.dv8tion.jda.api.events.interaction.ModalInteractionEvent;
import net.dv8tion.jda.api.events.interaction.component.EntitySelectInteractionEvent;
import net.dv8tion.jda.api.events.interaction.component.StringSelectInteractionEvent;

public class ReturnInteractionEvent {
	private Object Interaction;
	private InteractionType Type;

	public enum InteractionType {
		Modal,
		EntitySelector,
		StringSelector
	}

	public ReturnInteractionEvent(Object Interaction, InteractionType Type) {
		this.Interaction = Interaction;
		this.Type = Type;
	}

	public InteractionType getType() {
		return Type;
	}

	public ModalInteractionEvent getModal() {
		if (Type == InteractionType.Modal) {
			return (ModalInteractionEvent) Interaction;
		} else {
			throw new Error("つぁｈ");
		}
	}

	public EntitySelectInteractionEvent getEntitySelect() {
		if (Type == InteractionType.EntitySelector) {
			return (EntitySelectInteractionEvent) Interaction;
		} else {
			throw new Error("つぁｈ");
		}
	}

	public StringSelectInteractionEvent getStringSelect() {
		if (Type == InteractionType.StringSelector) {
			return (StringSelectInteractionEvent) Interaction;
		} else {
			throw new Error("つぁｈ");
		}
	}
}
