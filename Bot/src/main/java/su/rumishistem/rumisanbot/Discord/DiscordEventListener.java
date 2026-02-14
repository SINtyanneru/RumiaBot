package su.rumishistem.rumisanbot.Discord;

import net.dv8tion.jda.api.events.session.ReadyEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import su.rumishistem.rumi_java_logger.SeverityLevel;
import su.rumishistem.rumisanbot.Bot;
import su.rumishistem.rumisanbot.Main;

public class DiscordEventListener extends ListenerAdapter{
	private DiscordBot bot;

	public DiscordEventListener(DiscordBot bot) {
		this.bot = bot;
	}

	@Override
	public void onReady(ReadyEvent e) {
		Main.logger.print(SeverityLevel.Ok, "Discordへﾛｸﾞｲﾝしました。");
		Main.logger.print(SeverityLevel.Ok, "DiscordBot: " + bot.get_self().getName() + "("+bot.get_self().getId()+")");

		Bot.discord_ready = true;
	}
}
