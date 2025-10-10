package su.rumishistem.rumiabot.VXTwitterFunction;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.channel.concrete.TextChannel;
import net.dv8tion.jda.api.requests.restaction.WebhookMessageCreateAction;
import su.rumishistem.rumiabot.System.Discord.MODULE.DiscordWebHook;
import su.rumishistem.rumiabot.System.TYPE.CommandInteraction;
import su.rumishistem.rumiabot.System.TYPE.DiscordFunction;
import su.rumishistem.rumiabot.System.TYPE.FunctionClass;
import su.rumishistem.rumiabot.System.TYPE.ReceiveMessageEvent;
import su.rumishistem.rumiabot.System.TYPE.SourceType;

public class Main implements FunctionClass {
	private static final String FUNCTION_NAME = "VX/FXTwitter変換";
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
	}

	@Override
	public void ReceiveMessage(ReceiveMessageEvent e) {
		try {
			if (e.GetSource() == SourceType.Discord && !e.GetUser().isMe()) {
				//WebHook用意
				TextChannel ch = (TextChannel) e.GetMessage().GetDiscordChannel();
				DiscordWebHook wh = new DiscordWebHook(ch);

				if (e.GetMessage().CheckDiscordGuildFunctionEnabled(DiscordFunction.vxtwitter)) {
					VXTwitter.main(e, ch, wh);
				} else if (e.GetMessage().CheckDiscordGuildFunctionEnabled(DiscordFunction.fxtwitter)) {
					FXTwitter.main(e, ch, wh);
				}
			}
		} catch (Exception EX) {
			EX.printStackTrace();
		}
	}

	@Override
	public boolean GetAllowCommand(String Name) {
		return false;
	}

	@Override
	public void RunCommand(CommandInteraction CI) throws Exception {
	}

}