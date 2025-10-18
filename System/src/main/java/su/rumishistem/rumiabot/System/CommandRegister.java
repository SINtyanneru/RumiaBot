package su.rumishistem.rumiabot.System;

import static su.rumishistem.rumi_java_lib.LOG_PRINT.Main.LOG;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.entities.Guild;
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

public class CommandRegister {
	private static HashMap<String, CommandData> command_table = new HashMap<>();
	private static HashMap<String, DiscordMessageContext> discord_message_context_table = new HashMap<>();

	//Discord用
	private static List<net.dv8tion.jda.api.interactions.commands.build.CommandData> discord_temp = new ArrayList<>();

	public static void add_command(String name, CommandOptionRegist[] option_list, boolean private_command, RunCommand task) {
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

	public static int discord_regist() {
		for (JDA bot:Main.get_discord_bot().get_bot_list()) {
			bot.updateCommands().addCommands(discord_temp).complete();
		}

		LOG(LOG_TYPE.OK, "[Discord] "+discord_temp.size()+"個のコマンドを登録");
		return discord_temp.size();
	}

	public static CommandData get(String name) {
		return command_table.get(name.toUpperCase());
	}

	public static DiscordMessageContext get_discord_message_context(String name) {
		return discord_message_context_table.get(name.toUpperCase());
	}
}
