package su.rumishistem.rumiabot.VXTwitterFunction;

import java.sql.SQLException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.channel.ChannelType;
import net.dv8tion.jda.api.entities.channel.concrete.TextChannel;
import net.dv8tion.jda.api.requests.restaction.WebhookMessageCreateAction;
import su.rumishistem.rumiabot.System.Module.DiscordFunctionCheck;
import su.rumishistem.rumiabot.System.Module.DiscordWebHook;
import su.rumishistem.rumiabot.System.Type.FunctionClass;
import su.rumishistem.rumiabot.System.Type.ReceiveMessageEvent;
import su.rumishistem.rumiabot.System.Type.SourceType;
import su.rumishistem.rumiabot.System.Type.DiscordFunction.DiscordGuildFunction;

public class Main implements FunctionClass {
	@Override
	public String function_name() {
		return "VX/FXTwitter変換";
	}
	@Override
	public String function_version() {
		return "1.0";
	}
	@Override
	public String function_author() {
		return "るみ";
	}

	@Override
	public void init() {}

	@Override
	public void message_receive(ReceiveMessageEvent e) {
		if (e.get_source() != SourceType.Discord) return;
		if (e.get_discord().getAuthor().isBot()) return;
		if (e.get_discord().getChannel().getType() != ChannelType.TEXT) return;

		if (!e.get_discord().getGuild().getSelfMember().hasPermission(Permission.MANAGE_WEBHOOKS)) {
			return;
		}

		try {
			//WebHook用意
			TextChannel ch = (TextChannel) e.get_discord().getChannel();
			DiscordWebHook wh = new DiscordWebHook(ch);

			if (DiscordFunctionCheck.guild(e.get_discord().getGuild().getId(), DiscordGuildFunction.vxtwitter)) {
				VXTwitter.main(e, ch, wh);
			} else if (DiscordFunctionCheck.guild(e.get_discord().getGuild().getId(), DiscordGuildFunction.fxtwitter)) {
				FXTwitter.main(e, ch, wh);
			}
		} catch (SQLException ex) {
			//あ
		} catch (InterruptedException ex) {
			//い
		}
	}
}