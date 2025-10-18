package su.rumishistem.rumiabot.System.Discord;

import java.util.HashMap;
import java.util.UUID;

import net.dv8tion.jda.api.events.guild.member.GuildMemberJoinEvent;
import net.dv8tion.jda.api.events.guild.member.GuildMemberRemoveEvent;
import net.dv8tion.jda.api.events.interaction.command.MessageContextInteractionEvent;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.events.interaction.component.ButtonInteractionEvent;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import net.dv8tion.jda.api.events.session.ReadyEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import su.rumishistem.rumiabot.System.CommandRegister;
import su.rumishistem.rumiabot.System.FunctionLoader;
import su.rumishistem.rumiabot.System.ThreadPool;
import su.rumishistem.rumiabot.System.Module.ErrorPrinter;
import su.rumishistem.rumiabot.System.Type.CommandData;
import su.rumishistem.rumiabot.System.Type.CommandInteraction;
import su.rumishistem.rumiabot.System.Type.CommandOptionRegist;
import su.rumishistem.rumiabot.System.Type.DiscordMessageContext;
import su.rumishistem.rumiabot.System.Type.EventReceiveEvent;
import su.rumishistem.rumiabot.System.Type.FunctionClass;
import su.rumishistem.rumiabot.System.Type.ReceiveMessageEvent;

public class DiscordEventListener extends ListenerAdapter{
	@Override
	public void onReady(ReadyEvent e) {
		//TODO:機能設定
		
		/*
		SlashCommandData Command = Commands.slash("setting", "機能を設定します");

		//機能一覧
		OptionData FunctionOption = new OptionData(OptionType.STRING, "function", "機能", true);
		for (DiscordFunction Function:DiscordFunction.values()) {
			FunctionOption.addChoice(Function.name(), Function.name());
		}
		for (DiscordChannelFunction Function:DiscordChannelFunction.values()) {
			FunctionOption.addChoice(Function.name(), Function.name());
		}
		Command.addOptions(FunctionOption);

		//有効化無効化
		OptionData EnableOption = new OptionData(OptionType.BOOLEAN, "enable", "有効化無効化", true);
		Command.addOptions(EnableOption);
		 */
	}

	@Override
	public void onMessageReceived(MessageReceivedEvent e) {
		for (FunctionClass f:FunctionLoader.get_list()) {
			ThreadPool.run_message_event(new Runnable() {
				@Override
				public void run() {
					try {
						f.message_receive(new ReceiveMessageEvent(e));
					} catch (Exception ex) {
						ErrorPrinter.print(UUID.randomUUID().toString(), ex);
					}
				}
			});
		}
	}

	@Override
	public void onSlashCommandInteraction(SlashCommandInteractionEvent e) {
		CommandData command = CommandRegister.get(e.getName());
		HashMap<String, Object> option = new HashMap<>();

		if (!command.is_private()) {
			e.deferReply().queue();
		}

		for (CommandOptionRegist o:command.get_option()) {
			switch (o.get_type()) {
				case String:
					option.put(o.get_name().toUpperCase(), e.getOption(o.get_name().toLowerCase()).getAsString());
					break;
				case User:
					option.put(o.get_name().toUpperCase(), e.getOption(o.get_name().toLowerCase()).getAsMember());
					break;
				case DiscordRole:
					option.put(o.get_name().toUpperCase(), e.getOption(o.get_name().toLowerCase()).getAsRole());
					break;
			}
		}

		//実行
		ThreadPool.run_command(new Runnable() {
			@Override
			public void run() {
				try {
					command.get_task().run(new CommandInteraction(e, option, command.is_private()));
				} catch (Exception ex) {
					String id = UUID.randomUUID().toString();
					ErrorPrinter.print(id, ex);

					if (command.is_private()) {
						e.reply("エラー:" + ex.getMessage() + "\n["+id+"]").queue();
					} else {
						e.getHook().editOriginal("エラー:" + ex.getMessage() + "\n["+id+"]").queue();
					}
				}
			}
		});
	}

	@Override
	public void onButtonInteraction(ButtonInteractionEvent e) {
		String name = e.getButton().getCustomId().split("\\?")[0];
		HashMap<String, String> param = new HashMap<String, String>();

		String[] param_text = e.getButton().getCustomId().split("\\?")[1].split("&");
		for (String field:param_text) {
			String key = field.split("=")[0];
			String val = field.split("=")[1];
			param.put(key, val);
		}

		for (FunctionClass function:FunctionLoader.get_list()) {
			function.discord_button_event(name, param, e);
		}
	}

	@Override
	public void onMessageContextInteraction(MessageContextInteractionEvent e) {
		DiscordMessageContext command = CommandRegister.get_discord_message_context(e.getName());

		if (!command.is_private()) {
			e.deferReply().queue();
		}

		//実行
		ThreadPool.run_command(new Runnable() {
			@Override
			public void run() {
				try {
					command.get_task().run(e);
				} catch (Exception ex) {
					String id = UUID.randomUUID().toString();
					ErrorPrinter.print(id, ex);

					if (command.is_private()) {
						e.reply("エラー:" + ex.getMessage() + "\n["+id+"]").queue();
					} else {
						e.getHook().editOriginal("エラー:" + ex.getMessage() + "\n["+id+"]").queue();
					}
				}
			}
		});
	}

	@Override
	public void onGuildMemberJoin(GuildMemberJoinEvent e) {
		for (FunctionClass function:FunctionLoader.get_list()) {
			function.event_receive(new EventReceiveEvent(e));
		}
	}

	@Override
	public void onGuildMemberRemove(GuildMemberRemoveEvent e) {
		for (FunctionClass function:FunctionLoader.get_list()) {
			function.event_receive(new EventReceiveEvent(e));
		}
	}
}
