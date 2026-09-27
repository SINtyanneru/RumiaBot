package su.rumishistem.rumisanbot.Fluxer;

import java.util.HashMap;

import com.j4fluxer.events.message.GuildMessageReceivedEvent;
import com.j4fluxer.events.session.ReadyEvent;
import com.j4fluxer.fluxer.Fluxer;
import com.j4fluxer.fluxer.FluxerBuilder;
import com.j4fluxer.hooks.ListenerAdapter;

import su.rumishistem.rumi_java_logger.SeverityLevel;
import su.rumishistem.rumisanbot.BaseSystem;
import su.rumishistem.rumisanbot.Bot;
import su.rumishistem.rumisanbot.Main;

public class FluxerBot {
	private final Fluxer bot;
	private String self_id;

	public FluxerBot(String token) {
		this.bot = FluxerBuilder.create(token).build();

		bot.addEventListener(new ListenerAdapter() {
			@Override
			public void onReady(ReadyEvent e) {
				Main.logger.print(SeverityLevel.Ok, "Fluxerへﾛｸﾞｲﾝしました。");
				Main.logger.print(SeverityLevel.Ok, "FluxerBot: " + e.getUsername() + "("+e.getUserId()+")");
				self_id = e.getUserId();

				Bot.fluxer_ready = true;
			}

			@Override
			public void onGuildMessageReceived(GuildMessageReceivedEvent e) {
				try {
					BaseSystem.send_event("FLUXER", "MESSAGE_RECEIVE", new HashMap<String, Object>(){{
						put("GUILD_ID", e.getGuild().getId());
						put("CHANNEL_ID", e.getChannel().getId());

						put("USER_ID", e.getAuthor().getId());
						put("USER_UID", e.getAuthor().getUsername() + "#" + e.getAuthor().getDiscriminator());
						put("USER_NAME", e.getAuthor().getUsername() + "#" + e.getAuthor().getDiscriminator());
						put("USER_ICON", e.getAuthor().getAvatarUrl());
	
						put("MESSAGE_ID", e.getMessage().getId());
						put("MESSAGE_TEXT", e.getMessage().getContent());
					}});
				} catch (Exception ex) {
					ex.printStackTrace();
				}
			}
		});
	}

	public String get_self_id() {
		return self_id;
	}
}
