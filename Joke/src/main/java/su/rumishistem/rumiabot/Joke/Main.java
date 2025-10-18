package su.rumishistem.rumiabot.Joke;

import net.dv8tion.jda.api.interactions.commands.Command.Type;
import net.dv8tion.jda.api.interactions.commands.build.Commands;
import su.rumishistem.rumi_java_lib.LOG_PRINT.LOG_TYPE;
import su.rumishistem.rumiabot.System.CommandRegister;
import su.rumishistem.rumiabot.System.Module.DiscordFunctionCheck;
import su.rumishistem.rumiabot.System.Type.CommandInteraction;
import su.rumishistem.rumiabot.System.Type.CommandOptionRegist;
import su.rumishistem.rumiabot.System.Type.FunctionClass;
import su.rumishistem.rumiabot.System.Type.OptionType;
import su.rumishistem.rumiabot.System.Type.ReceiveMessageEvent;
import su.rumishistem.rumiabot.System.Type.RunCommand;
import su.rumishistem.rumiabot.System.Type.SourceType;
import su.rumishistem.rumiabot.System.Type.DiscordFunction.DiscordGuildFunction;

public class Main implements FunctionClass {
	@Override
	public String function_name() {
		return "よけ";
	}
	@Override
	public String function_version() {
		return "1.0";
	}
	@Override
	public String function_author() {
		return "るみ";
	}

	@Override
	public void init() {
		CommandRegister.add_command("cam", new CommandOptionRegist[] {
			new CommandOptionRegist("user", OptionType.User, true),
			new CommandOptionRegist("text", OptionType.String, true)
		}, true, new RunCommand() {
			@Override
			public void run(CommandInteraction e) throws Exception {
				if (e.get_source() != SourceType.Discord) {
					e.reply("Discordのみで使用可能");
					return;
				}

				cam.Command(e);
			}
		});

		//AddDiscordContextMenu(Commands.context(Type.MESSAGE, "rvtr"));
	}

	@Override
	public void message_receive(ReceiveMessageEvent e) {
		if (e.get_source() == SourceType.Discord) {
			try {
				if (DiscordFunctionCheck.guild(e.get_discord().getGuild().getId(), DiscordGuildFunction.Joke)) {
					//そういうのよくないよ
					SouiunoYokunaiyo.Main(e);
					//きも
					Kimo.Main(e);
					//住所
					Zhuusho.Main(e);
					//うんこ
					Unko.Main(e);
				}
			} catch (Exception ex) {
				ex.printStackTrace();
			}
		}
	}
}
