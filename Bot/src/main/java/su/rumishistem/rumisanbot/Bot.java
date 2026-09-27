package su.rumishistem.rumisanbot;

import su.rumishistem.rumi_java_logger.SeverityLevel;
import su.rumishistem.rumisanbot.Discord.DiscordBot;
import su.rumishistem.rumisanbot.Fluxer.FluxerBot;
import su.rumishistem.rumisanbot.Misskey.MisskeyBot;

public class Bot {
	private static Thread discord_thread = null;
	private static DiscordBot discord_bot;
	public static boolean discord_ready = false;

	private static Thread misskey_thread = null;
	private static MisskeyBot misskey_bot;
	public static boolean misskey_ready = false;

	private static Thread fluxer_thread = null;
	private static FluxerBot fluxer_bot;
	public static boolean fluxer_ready = false;

	public static void start() throws InterruptedException {
		discord_thread = new Thread(new Runnable() {
			@Override
			public void run() {
				try {
					discord_bot = new DiscordBot(Config.Discord.token);
				} catch (Exception ex) {
					ex.printStackTrace();
					Main.logger.print(SeverityLevel.Critical, "Discordへのログインに失敗しました");
					System.exit(1);
				}
			}
		});

		misskey_thread = new Thread(new Runnable() {
			@Override
			public void run() {
				try {
					misskey_bot = new MisskeyBot(Config.Misskey.host, Config.Misskey.token, Config.Misskey.admin_token);
				} catch (RuntimeException ex) {
					ex.printStackTrace();
					Main.logger.print(SeverityLevel.Critical, "Misskeyへのログインに失敗しました");
					System.exit(1);
				}
			}
		});

		fluxer_thread = new Thread(new Runnable() {
			@Override
			public void run() {
				try {
					fluxer_bot = new FluxerBot(Config.Fluxer.token);
				} catch (RuntimeException ex) {
					ex.printStackTrace();
					Main.logger.print(SeverityLevel.Critical, "Fluxerへのログインに失敗しました");
					System.exit(1);
				}
			}
		});

		discord_thread.start();
		fluxer_thread.start();
		misskey_thread.start();

		//Ready待機
		while (!discord_ready || !misskey_ready) {
			Thread.sleep(1000);
		}

		//OK
		Main.logger.print(SeverityLevel.Ok, "BOT Ready");
	}

	public static DiscordBot get_discord() {
		return discord_bot;
	}

	public static FluxerBot get_fluxer() {
		return fluxer_bot;
	}

	public static MisskeyBot get_misskey() {
		return misskey_bot;
	}
}
