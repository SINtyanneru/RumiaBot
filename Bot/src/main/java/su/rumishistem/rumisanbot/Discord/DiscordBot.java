package su.rumishistem.rumisanbot.Discord;

import java.util.HashMap;
import java.util.Map;

import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.JDABuilder;
import net.dv8tion.jda.api.OnlineStatus;
import net.dv8tion.jda.api.entities.Activity;
import net.dv8tion.jda.api.entities.SelfUser;
import net.dv8tion.jda.api.interactions.commands.SlashCommandInteraction;
import net.dv8tion.jda.api.requests.GatewayIntent;
import net.dv8tion.jda.api.utils.MemberCachePolicy;
import net.dv8tion.jda.internal.utils.JDALogger;

public class DiscordBot {
	private JDA bot;
	protected Map<String, SlashCommandInteraction> command_interaction = new HashMap<>();

	public final String self_id;

	public DiscordBot(String token) throws InterruptedException {
		JDALogger.setFallbackLoggerEnabled(false);

		JDABuilder b = JDABuilder.createDefault(token);

		b.enableIntents(
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

		b.setRawEventsEnabled(true);
		b.setEventPassthrough(true);
		b.setMemberCachePolicy(MemberCachePolicy.ALL);
		b.setAutoReconnect(true);
		b.addEventListeners(new DiscordEventListener(this));

		b.setActivity(Activity.playing("初期化中..."));
		b.setStatus(OnlineStatus.IDLE);

		bot = b.build();
		bot.awaitReady();

		self_id = get_self().getId();
	}

	public JDA get_jda() {
		return bot;
	}

	public SelfUser get_self() {
		return bot.getSelfUser();
	}

	public SlashCommandInteraction get_interaction(String id) {
		return command_interaction.get(id);
	}
}
