package su.rumishistem.rumiabot.MakeItQuote;

import static su.rumishistem.rumiabot.System.FunctionModuleLoader.AddDiscordContextMenu;

import java.io.ByteArrayInputStream;
import java.io.File;
import javax.imageio.ImageIO;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.User;
import net.dv8tion.jda.api.events.interaction.command.MessageContextInteractionEvent;
import net.dv8tion.jda.api.interactions.commands.Command.Type;
import net.dv8tion.jda.api.interactions.commands.build.Commands;
import net.dv8tion.jda.api.utils.FileUpload;
import su.rumishistem.rumi_java_lib.RESOURCE.RESOURCE_MANAGER;
import su.rumishistem.rumiabot.System.Discord.MODULE.NameParse;
import su.rumishistem.rumiabot.System.TYPE.CommandInteraction;
import su.rumishistem.rumiabot.System.TYPE.FunctionClass;
import su.rumishistem.rumiabot.System.TYPE.ReceiveMessageEvent;
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
			miq.BackgroundImage = ImageIO.read(new ByteArrayInputStream(new RESOURCE_MANAGER(Main.class).getResourceData("/miq_bg.png")));
		} catch (Exception EX) {
			EX.printStackTrace();
		}

		AddDiscordContextMenu(Commands.context(Type.MESSAGE, "miq"));
	}

	@Override
	public void ReceiveMessage(ReceiveMessageEvent e) {}

	@Override
	public boolean GetAllowCommand(String Name) {
		return Name.equals("Message:miq");
	}

	@Override
	public void RunCommand(CommandInteraction CI) throws Exception {}

	@Override
	public void RunInteraction(RunInteractionEvent Interaction) throws Exception {
		if (Interaction.getType() == InteractionType.MessageContext) {
			MessageContextInteractionEvent MI = Interaction.getMessageContext();
			Message MSG = MI.getTarget();
			Member MEM = MSG.getMember();
			User U = MEM.getUser();

			MI.deferReply().queue();

			String IconURL = MI.getTarget().getAuthor().getEffectiveAvatarUrl() + "?size=4096";
			File F = miq.Gen(U.getName(), new NameParse(MEM).getDisplayName(), IconURL, MSG.getContentRaw());
			MI.getHook().sendFiles(FileUpload.fromData(F)).queue();
			F.delete();
		}
	}
}
