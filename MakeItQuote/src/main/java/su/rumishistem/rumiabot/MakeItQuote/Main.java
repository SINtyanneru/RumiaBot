package su.rumishistem.rumiabot.MakeItQuote;

import static su.rumishistem.rumiabot.System.FunctionModuleLoader.AddDiscordContextMenu;
import java.io.ByteArrayInputStream;
import javax.imageio.ImageIO;
import net.dv8tion.jda.api.events.interaction.command.MessageContextInteractionEvent;
import net.dv8tion.jda.api.events.interaction.component.StringSelectInteractionEvent;
import net.dv8tion.jda.api.interactions.commands.Command.Type;
import net.dv8tion.jda.api.interactions.commands.build.Commands;
import su.rumishistem.rumi_java_lib.RESOURCE.RESOURCE_MANAGER;
import su.rumishistem.rumiabot.System.TYPE.CommandInteraction;
import su.rumishistem.rumiabot.System.TYPE.FunctionClass;
import su.rumishistem.rumiabot.System.TYPE.ReceiveMessageEvent;
import su.rumishistem.rumiabot.System.TYPE.ReturnInteractionEvent;
import su.rumishistem.rumiabot.System.TYPE.RunInteractionEvent;
import su.rumishistem.rumiabot.System.TYPE.RunInteractionEvent.InteractionType;

public class Main implements FunctionClass{
	private static final String FUNCTION_NAME = "よけ";
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

	@Override
	public void Init() {
		try {
			MakeItQuote.BackgroundImage = ImageIO.read(new ByteArrayInputStream(new RESOURCE_MANAGER(Main.class).getResourceData("/miq_bg.png")));
		} catch (Exception EX) {
			EX.printStackTrace();
		}

		AddDiscordContextMenu(Commands.context(Type.MESSAGE, "miq"));
	}

	@Override
	public void ReceiveMessage(ReceiveMessageEvent e) {}

	@Override
	public boolean GetAllowCommand(String Name) {
		return Name.equals("Message:miq") || Name.equals("StringSelect:miq-icon-color-select");
	}

	@Override
	public void RunCommand(CommandInteraction CI) throws Exception {}

	@Override
	public void RunInteraction(RunInteractionEvent Interaction) throws Exception {
		if (Interaction.getType() == InteractionType.MessageContext) {
			MessageContextInteractionEvent MI = Interaction.getMessageContext();
			MI.deferReply().queue();
			DiscordMiq.RunMessageContext(MI);
		}
	}

	@Override
	public void ReturnInteraction(ReturnInteractionEvent Interaction) throws Exception {
		if (Interaction.getType() == su.rumishistem.rumiabot.System.TYPE.ReturnInteractionEvent.InteractionType.StringSelector) {
			StringSelectInteractionEvent SI = Interaction.getStringSelect();
			SI.deferReply().queue();
			DiscordMiq.ChangeSetting(SI);
		}
	}
}
