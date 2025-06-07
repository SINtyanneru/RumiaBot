package su.rumishistem.rumiabot.System.TYPE;

import net.dv8tion.jda.api.events.interaction.ModalInteractionEvent;
import net.dv8tion.jda.api.events.interaction.component.EntitySelectInteractionEvent;

public class ReturnInteractionEvent {
	private Object Interaction;
	private InteractionType Type;

	public enum InteractionType {
		Modal,
		EntitySelector,
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
		if (Type == InteractionType.Modal) {
			return (EntitySelectInteractionEvent) Interaction;
		} else {
			throw new Error("つぁｈ");
		}
	}
}
