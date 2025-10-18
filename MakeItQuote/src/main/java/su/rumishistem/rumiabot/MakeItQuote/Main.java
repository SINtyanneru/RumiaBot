package su.rumishistem.rumiabot.MakeItQuote;

import java.io.ByteArrayInputStream;

import javax.imageio.ImageIO;
import net.dv8tion.jda.api.events.interaction.command.MessageContextInteractionEvent;
import su.rumishistem.rumi_java_lib.RESOURCE.RESOURCE_MANAGER;
import su.rumishistem.rumiabot.System.CommandRegister;
import su.rumishistem.rumiabot.System.Type.CommandInteraction;
import su.rumishistem.rumiabot.System.Type.CommandOptionRegist;
import su.rumishistem.rumiabot.System.Type.FunctionClass;
import su.rumishistem.rumiabot.System.Type.RunCommand;
import su.rumishistem.rumiabot.System.Type.RunDiscordMessageContext;
import su.rumishistem.rumiabot.System.Type.SourceType;

public class Main implements FunctionClass{
	@Override
	public String function_name() {
		return "Make It Quote";
	}
	@Override
	public String function_version() {
		return "1.5";
	}
	@Override
	public String function_author() {
		return "るみ";
	}

	@Override
	public void init() {
		try {
			MakeItQuote.BackgroundImage = ImageIO.read(new ByteArrayInputStream(new RESOURCE_MANAGER(Main.class).getResourceData("/miq_bg.png")));
		} catch (Exception EX) {
			EX.printStackTrace();
		}

		CommandRegister.add_command("miq", new CommandOptionRegist[0], false, new RunCommand() {
			@Override
			public void run(CommandInteraction e) throws Exception {
				if (e.get_source() != SourceType.Misskey) {
					e.reply("Misskeyのみで使用可能。\nDiscordではメッセージのコンテキストメニューから使用可能です！");
					return;
				}

				MisskeyMiq.Run(e);
			}
		});

		CommandRegister.add_message_contextmenu("miq", false, new RunDiscordMessageContext() {
			@Override
			public void run(MessageContextInteractionEvent e) throws Exception {
				DiscordMiq.RunMessageContext(e);
			}
		});
	}

	/*@Override
	public void ReturnInteraction(ReturnInteractionEvent Interaction) throws Exception {
		if (Interaction.getType() == su.rumishistem.rumiabot.System.TYPE.ReturnInteractionEvent.InteractionType.StringSelector) {
			StringSelectInteractionEvent SI = Interaction.getStringSelect();
			SI.deferReply().queue();
			DiscordMiq.ChangeSetting(SI);
		}
	}*/
}
