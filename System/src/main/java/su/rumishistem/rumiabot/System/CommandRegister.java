package su.rumishistem.rumiabot.System;

import static su.rumishistem.rumi_java_lib.LOG_PRINT.Main.LOG;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.exceptions.RateLimitedException;
import net.dv8tion.jda.api.interactions.commands.OptionType;
import net.dv8tion.jda.api.interactions.commands.Command.Type;
import net.dv8tion.jda.api.interactions.commands.build.Commands;
import net.dv8tion.jda.api.interactions.commands.build.OptionData;
import net.dv8tion.jda.api.interactions.commands.build.SlashCommandData;
import su.rumishistem.rumi_java_lib.LOG_PRINT.LOG_TYPE;
import su.rumishistem.rumiabot.System.Type.CommandData;
import su.rumishistem.rumiabot.System.Type.CommandOptionRegist;
import su.rumishistem.rumiabot.System.Type.DiscordMessageContext;
import su.rumishistem.rumiabot.System.Type.RunCommand;
import su.rumishistem.rumiabot.System.Type.RunDiscordMessageContext;
import su.rumishistem.rumiabot.System.Type.DiscordFunction.DiscordChannelFunction;
import su.rumishistem.rumiabot.System.Type.DiscordFunction.DiscordGuildFunction;

public class CommandRegister {
	private static HashMap<String, CommandData> command_table = new HashMap<>();
	private static HashMap<String, DiscordMessageContext> discord_message_context_table = new HashMap<>();

	//Discord用
	private static List<net.dv8tion.jda.api.interactions.commands.build.CommandData> discord_temp = new ArrayList<>();

	public static void add_command(String name, CommandOptionRegist[] option_list, boolean private_command, RunCommand task) {
		if (command_table.get(name.toUpperCase()) != null) {
			LOG(LOG_TYPE.FAILED, "コマンド登録：" + name.toUpperCase());
			throw new RuntimeException("同名のコマンドがあります：" + name.toUpperCase());
		} else {
			LOG(LOG_TYPE.INFO, "コマンド登録：" + name.toUpperCase());
		}

		//テーブルへ
		command_table.put(name.toUpperCase(), new CommandData(name.toUpperCase(), option_list, private_command, task));

		//Discord
		SlashCommandData sl = Commands.slash(name.toLowerCase(), name);
		for (CommandOptionRegist option:option_list) {
			OptionType type = null;
			switch (option.get_type()) {
				case String: type = OptionType.STRING; break;
				case User: type = OptionType.USER; break;
				case DiscordRole: type = OptionType.ROLE; break;
			}

			OptionData od = new OptionData(type, option.get_name(), "説明", option.is_require());
			sl.addOptions(od);
		}
		discord_temp.add(sl);
	}

	public static void add_message_contextmenu(String name, boolean private_command, RunDiscordMessageContext task) {
		discord_message_context_table.put(name.toUpperCase(), new DiscordMessageContext(name.toUpperCase(), private_command, task));

		discord_temp.add(Commands.context(Type.MESSAGE, name.toLowerCase()));
	}

	public static int discord_regist() throws RateLimitedException {
		//鯖の機能
		SlashCommandData guild_setting = Commands.slash("setting", "設定");
		OptionData guild_select_option = new OptionData(OptionType.STRING, "function", "機能", true);
		for (DiscordGuildFunction f:DiscordGuildFunction.values()) {
			guild_select_option.addChoice(f.name(), f.name());
		}
		guild_setting.addOptions(guild_select_option, new OptionData(OptionType.BOOLEAN, "enable", "有効化無効化", true));

		//チャンネルの機能
		SlashCommandData channel_setting = Commands.slash("channel-setting", "設定");
		OptionData channel_select_option = new OptionData(OptionType.STRING, "function", "機能", true);
		for (DiscordChannelFunction f:DiscordChannelFunction.values()) {
			channel_select_option.addChoice(f.name(), f.name());
		}
		channel_setting.addOptions(channel_select_option, new OptionData(OptionType.BOOLEAN, "enable", "有効化無効化", true));

		for (JDA bot:Main.get_discord_bot().get_bot_list()) {
			bot.updateCommands().addCommands(discord_temp).addCommands(guild_setting).addCommands(channel_setting).queue();
		}

		LOG(LOG_TYPE.OK, "[Discord] "+discord_temp.size()+"個のコマンドを"+Main.get_discord_bot().get_bot_list().length+"体に登録");
		return discord_temp.size();
	}

	public static CommandData get(String name) {
		return command_table.get(name.toUpperCase());
	}

	public static DiscordMessageContext get_discord_message_context(String name) {
		return discord_message_context_table.get(name.toUpperCase());
	}
}
