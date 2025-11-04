package su.rumishistem.rumiabot.System.Discord;

import java.sql.SQLException;

import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import su.rumishistem.rumi_java_lib.SQL;
import su.rumishistem.rumiabot.System.Module.DiscordFunctionCheck;
import su.rumishistem.rumiabot.System.Type.DiscordFunction.DiscordChannelFunction;
import su.rumishistem.rumiabot.System.Type.DiscordFunction.DiscordGuildFunction;

public class FunctionSetting {
	public static void guild_setting(SlashCommandInteractionEvent e) {
		String name = e.getOption("function").getAsString();
		boolean enable = e.getOption("enable").getAsBoolean();

		try {
			DiscordGuildFunction function = DiscordGuildFunction.get_from_name(name);

			if (enable) {
				//有効化
				if (!DiscordFunctionCheck.guild(e.getGuild().getId(), function)) {
					SQL.UP_RUN("INSERT INTO `CONFIG` (`GID`, `CID`, `FUNC_ID`) VALUES (?, '', ?)", new Object[] {e.getGuild().getId(), name});
				}
			} else {
				//無効化
				if (DiscordFunctionCheck.guild(e.getGuild().getId(), function)) {
					SQL.UP_RUN("DELETE FROM `CONFIG` WHERE `GID` = ? AND `CID` = '' AND `FUNC_ID` = ?;", new Object[] {e.getGuild().getId(), name});
				}
			}

			e.getHook().editOriginal("OK").queue();
		} catch (RuntimeException ex) {
			e.getHook().editOriginal("その機能はありません").queue();
		} catch (SQLException ex) {
			e.getHook().editOriginal("エラー").queue();
		}
	}

	public static void channel_setting(SlashCommandInteractionEvent e) {
		String name = e.getOption("function").getAsString();
		boolean enable = e.getOption("enable").getAsBoolean();

		try {
			DiscordChannelFunction function = DiscordChannelFunction.get_from_name(name);

			if (enable) {
				//有効化
				if (!DiscordFunctionCheck.channel(e.getGuild().getId(), e.getChannelId(), function)) {
					SQL.UP_RUN("INSERT INTO `CONFIG` (`GID`, `CID`, `FUNC_ID`) VALUES (?, ?, ?)", new Object[] {e.getGuild().getId(), e.getChannelId(), name});
				}
			} else {
				//無効化
				if (DiscordFunctionCheck.channel(e.getGuild().getId(), e.getChannelId(), function)) {
					SQL.UP_RUN("DELETE FROM `CONFIG` WHERE `GID` = ? AND `CID` = ? AND `FUNC_ID` = ?;", new Object[] {e.getGuild().getId(), e.getChannelId(), name});
				}
			}

			e.getHook().editOriginal("OK").queue();
		} catch (RuntimeException ex) {
			e.getHook().editOriginal("その機能はありません").queue();
		} catch (SQLException ex) {
			e.getHook().editOriginal("エラー").queue();
		}
	}
}
