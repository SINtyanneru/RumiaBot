package su.rumishistem.rumiabot.System.Discord;

import java.util.ArrayList;
import java.util.List;

import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.JDABuilder;
import net.dv8tion.jda.api.OnlineStatus;
import net.dv8tion.jda.api.entities.Activity;
import net.dv8tion.jda.api.requests.GatewayIntent;
import net.dv8tion.jda.api.utils.MemberCachePolicy;
import net.dv8tion.jda.internal.utils.JDALogger;

public class DiscordBot {
	private List<JDA> bot_list = new ArrayList<JDA>();

	public DiscordBot(String token) throws InterruptedException {
		String[] token_list = token.split(", ");

		JDALogger.setFallbackLoggerEnabled(false);

		for (String tk:token_list) {
			JDABuilder builder = JDABuilder.createDefault(tk);

			//インテンツ
			builder.enableIntents(
				GatewayIntent.GUILD_MEMBERS,
				GatewayIntent.GUILD_MODERATION,
				GatewayIntent.GUILD_WEBHOOKS,
				GatewayIntent.GUILD_INVITES,
				GatewayIntent.GUILD_VOICE_STATES,
				GatewayIntent.GUILD_PRESENCES,
				GatewayIntent.GUILD_MESSAGES,
				GatewayIntent.GUILD_MESSAGE_REACTIONS,
				GatewayIntent.GUILD_MESSAGE_TYPING,
				GatewayIntent.DIRECT_MESSAGES,
				GatewayIntent.DIRECT_MESSAGE_REACTIONS,
				GatewayIntent.DIRECT_MESSAGE_TYPING,
				GatewayIntent.MESSAGE_CONTENT,
				GatewayIntent.SCHEDULED_EVENTS,
				GatewayIntent.AUTO_MODERATION_CONFIGURATION,
				GatewayIntent.AUTO_MODERATION_EXECUTION
			);

			//設定
			builder.setRawEventsEnabled(true);
			builder.setEventPassthrough(true);
			builder.setMemberCachePolicy(MemberCachePolicy.ALL);
			builder.setAutoReconnect(true);
			builder.addEventListeners(new DiscordEventListener());

			//ステータス
			builder.setActivity(Activity.watching("貴様"));
			builder.setStatus(OnlineStatus.ONLINE);

			JDA bot = builder.build();
			bot_list.add(bot);
			bot.awaitReady();
		}
	}

	public JDA get_primary_bot() {
		return bot_list.get(0);
	}

	public JDA[] get_bot_list() {
		JDA[] list = new JDA[bot_list.size()];
		for (int i = 0; i < bot_list.size(); i++) {
			list[i] = bot_list.get(i);
		}
		return list;
	}
}
