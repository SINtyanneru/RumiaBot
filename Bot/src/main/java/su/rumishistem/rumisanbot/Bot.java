package su.rumishistem.rumisanbot;

import su.rumishistem.rumi_java_logger.SeverityLevel;
import su.rumishistem.rumisanbot.Discord.DiscordBot;

public class Bot {
	private static Thread discord_thread = null;
	private static DiscordBot discord_bot;
	public static boolean discord_ready = false;

	public static void start() throws InterruptedException {
		discord_thread = new Thread(new Runnable() {
			@Override
			public void run() {
				try {
					discord_bot = new DiscordBot(Config.Discord.token);
				} catch (InterruptedException e) {
					return;
				}
			}
		});

		discord_thread.start();

		//Ready待機
		while (!discord_ready) {
			Thread.sleep(1000);
		}

		Main.logger.print(SeverityLevel.Ok, "BOT Ready");

		//ステータス
		//discord_bot.get_jda().getPresence().setActivity(Activity.watching("貴様"));
		//discord_bot.get_jda().getPresence().setStatus(OnlineStatus.ONLINE);
	}

	public static DiscordBot get_discord() {
		return discord_bot;
	}
}
