package su.rumishistem.rumisanbot.Discord;

import java.util.*;
import net.dv8tion.jda.api.events.guild.*;
import net.dv8tion.jda.api.events.guild.member.*;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import net.dv8tion.jda.api.events.session.ReadyEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import net.dv8tion.jda.api.interactions.commands.*;
import su.rumishistem.rumi_java_logger.SeverityLevel;
import su.rumishistem.rumisanbot.*;

public class DiscordEventListener extends ListenerAdapter{
	private DiscordBot bot;

	public DiscordEventListener(DiscordBot bot) {
		this.bot = bot;
	}

	//BOT起動
	@Override
	public void onReady(ReadyEvent e) {
		Main.logger.print(SeverityLevel.Ok, "Discordへﾛｸﾞｲﾝしました。");
		Main.logger.print(SeverityLevel.Ok, "DiscordBot: " + bot.get_self().getName() + "("+bot.get_self().getId()+")");

		Bot.discord_ready = true;
	}

	//サーバーに参加
	@Override
	public void onGuildJoin(GuildJoinEvent e) {
		try {
			BaseSystem.send_event("DISCORD", "GUILD_JOIN", new HashMap<String, Object>(){{
				put("GUILD_ID", e.getGuild().getId());
			}});
		} catch (Exception ex) {
			ex.printStackTrace();
		}
	}

	//サーバーから蹴られた
	@Override
	public void onGuildLeave(GuildLeaveEvent e) {
		try {
			BaseSystem.send_event("DISCORD", "GUILD_LEAVE", new HashMap<String, Object>(){{
				put("GUILD_ID", e.getGuild().getId());
			}});
		} catch (Exception ex) {
			ex.printStackTrace();
		}
	}

	//サーバーにメンバーが参加
	@Override
	public void onGuildMemberJoin(GuildMemberJoinEvent e) {
		try {
			BaseSystem.send_event("DISCORD", "GUILD_MEMBER_JOIN", new HashMap<String, Object>(){{
				put("GUILD_ID", e.getGuild().getId());
				put("USER_ID", e.getMember().getId());
			}});
		} catch (Exception ex) {
			ex.printStackTrace();
		}
	}

	//サーバーからメンバーが脱退
	@Override
	public void onGuildMemberRemove(GuildMemberRemoveEvent e) {
		try {
			BaseSystem.send_event("DISCORD", "GUILD_MEMBER_LEAVE", new HashMap<String, Object>(){{
				put("GUILD_ID", e.getGuild().getId());
				put("USER_ID", e.getMember().getId());
			}});
		} catch (Exception ex) {
			ex.printStackTrace();
		}
	}

	//メッセージ受信
	@Override
	public void onMessageReceived(MessageReceivedEvent e) {
		try {
			//DMではGuild/Memberがnullになる
			String guild_id = (e.getGuild() == null) ? "" : e.getGuild().getId();
			String user_name = (e.getMember() == null) ? e.getAuthor().getName() : e.getMember().getEffectiveName();
			String user_icon = (e.getMember() == null) ? e.getAuthor().getEffectiveAvatarUrl() : e.getMember().getEffectiveAvatarUrl();

			BaseSystem.send_event("DISCORD", "MESSAGE_RECEIVE", new HashMap<String, Object>(){{
				put("GUILD_ID", guild_id);
				put("CHANNEL_ID", e.getChannel().getId());

				put("USER_ID", e.getAuthor().getId());
				put("USER_UID", e.getAuthor().getName());
				put("USER_NAME", user_name);
				put("USER_ICON", user_icon);

				put("MESSAGE_ID", e.getMessage().getId());
				put("MESSAGE_TEXT", e.getMessage().getContentRaw());
			}});
		} catch (Exception ex) {
			ex.printStackTrace();
		}
	}

	//スラッシュコマンド
	@Override
	public void onSlashCommandInteraction(SlashCommandInteractionEvent e) {
		try {
			Map<String, Object> option_list = new HashMap<>();

			for (OptionMapping option:e.getOptions()) {
				String name = option.getName();
				OptionType type = option.getType();
				Object value;
				switch (type) {
					case STRING:
						value = new HashMap<String, Object>(){{
							put("TYPE", "STRING");
							put("DATA", new HashMap<>(){{
								put("VALUE", option.getAsString());
							}});
						}};
						break;
					case INTEGER:
						value = new HashMap<String, Object>(){{
							put("TYPE", "INT");
							put("DATA", new HashMap<>(){{
								put("VALUE", option.getAsInt());
							}});
						}};
						break;
					case BOOLEAN:
						value = new HashMap<String, Object>(){{
							put("TYPE", "BOOL");
							put("DATA", new HashMap<>(){{
								put("VALUE", option.getAsBoolean());
							}});
						}};
						break;
					case USER:
						value = new HashMap<String, Object>(){{
							put("TYPE", "USER");
							put("DATA", new HashMap<>(){{
								put("VALUE", option.getAsUser().getId());
							}});
						}};
						break;
					case CHANNEL:
						value = new HashMap<String, Object>(){{
							put("TYPE", "DISCORD_CHANNEL");
							put("DATA", new HashMap<>(){{
								put("VALUE", option.getAsChannel().getId());
							}});
						}};
						break;
					case ROLE:
						value = new HashMap<String, Object>(){{
							put("TYPE", "DISCORD_ROLE");
							put("DATA", new HashMap<>(){{
								put("VALUE", option.getAsRole().getId());
							}});
						}};
						break;
					case ATTACHMENT:
						value = new HashMap<String, Object>(){{
							put("TYPE", "FILE");
							put("DATA", new HashMap<>(){{
								put("NAME", option.getAsAttachment().getFileName());
								put("SIZE", option.getAsAttachment().getSize());
								put("TYPE", option.getAsAttachment().getContentType());
								put("URL", option.getAsAttachment().getUrl());
							}});
						}};
						break;
					default:
						continue;
				}

				option_list.put(name, value);
			}

			String id = UUID.randomUUID().toString();
			bot.put_interaction(id, e.getInteraction());

			//DMではGuild/Memberがnullになる
			String guild_id = (e.getGuild() == null) ? "" : e.getGuild().getId();
			String user_name = (e.getMember() == null) ? e.getUser().getName() : e.getMember().getEffectiveName();
			String user_icon = (e.getMember() == null) ? e.getUser().getEffectiveAvatarUrl() : e.getMember().getEffectiveAvatarUrl();

			BaseSystem.send_event("DISCORD", "COMMAND_INTERACTION", new HashMap<String, Object>(){{
				put("ID", id);

				put("GUILD_ID", guild_id);
				put("CHANNEL_ID", e.getChannel().getId());

				put("USER_ID", e.getUser().getId());
				put("USER_UID", e.getUser().getName());
				put("USER_NAME", user_name);
				put("USER_ICON", user_icon);

				put("COMMAND_NAME", e.getName());
				put("COMMAND_OPTION", option_list);
			}});
		} catch (Exception ex) {
			ex.printStackTrace();
		}
	}

}
