package su.rumishistem.rumisanbot.Discord;

import java.util.*;
import net.dv8tion.jda.api.*;
import net.dv8tion.jda.api.entities.*;
import net.dv8tion.jda.api.interactions.commands.SlashCommandInteraction;
import net.dv8tion.jda.api.requests.GatewayIntent;
import net.dv8tion.jda.api.utils.MemberCachePolicy;
import net.dv8tion.jda.internal.utils.JDALogger;

public class DiscordBot {
	private JDA bot;
	protected Map<String, SlashCommandInteraction> command_interaction = new HashMap<>();
	private Map<String, Long> command_interaction_time = new HashMap<>();

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

	public void put_interaction(String id, SlashCommandInteraction interaction) {
		cleanup_interaction();
		command_interaction.put(id, interaction);
		command_interaction_time.put(id, System.currentTimeMillis());
	}

	public SlashCommandInteraction get_interaction(String id) {
		return command_interaction.get(id);
	}

	public void remove_interaction(String id) {
		command_interaction.remove(id);
		command_interaction_time.remove(id);
	}

	//インタラクションのトークンは15分で失効するので、それより古い物は捨てる（掃除しないと溜まり続ける）
	private void cleanup_interaction() {
		long now = System.currentTimeMillis();
		command_interaction_time.entrySet().removeIf(entry -> {
			if (now - entry.getValue() > 15 * 60 * 1000) {
				command_interaction.remove(entry.getKey());
				return true;
			}
			return false;
		});
	}
}
