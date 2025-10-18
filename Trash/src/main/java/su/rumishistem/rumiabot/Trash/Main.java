package su.rumishistem.rumiabot.Trash;

import static su.rumishistem.rumi_java_lib.LOG_PRINT.Main.LOG;
import java.io.IOException;
import su.rumishistem.rumi_java_lib.LOG_PRINT.LOG_TYPE;
import su.rumishistem.rumiabot.System.CommandRegister;
import su.rumishistem.rumiabot.System.Type.CommandInteraction;
import su.rumishistem.rumiabot.System.Type.CommandOptionRegist;
import su.rumishistem.rumiabot.System.Type.FunctionClass;
import su.rumishistem.rumiabot.System.Type.OptionType;
import su.rumishistem.rumiabot.System.Type.RunCommand;

public class Main implements FunctionClass {
	@Override
	public String function_name() {
		return "使い捨て環境";
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
		try {
			if (!Podman.check_installed_image()) {
				LOG(LOG_TYPE.INFO, "使い捨て環境のイメージの作成を開始しました");
				Podman.build_debian_image();
				LOG(LOG_TYPE.OK, "使い捨て環境が完成しました！");
			}

			CommandRegister.add_command("exec", new CommandOptionRegist[] {
				new CommandOptionRegist("language", OptionType.String, true),
				new CommandOptionRegist("code", OptionType.String, true)
			}, false, new RunCommand() {
				@Override
				public void run(CommandInteraction e) throws Exception {
					String language = e.get_option_as_string("language").toUpperCase();
					String result = exec(language, e.get_option_as_string("code"));
					result = sanitize(result);
					e.reply("```\n"+result+"\n```");
				}
			});
		} catch (Exception EX) {
			EX.printStackTrace();
			LOG(LOG_TYPE.FAILED, "使い捨て環境の構築に失敗しました！");
		}
	}

	private static String sanitize(String input) {
		input = input.replace("`", "\\`");
		return input;
	}

	private static String replace_dk(String input) {
		input = input.replace("'", "'\\'");
		return input;
	}

	private static String exec(String language, String code) throws InterruptedException, IOException {
		StringBuilder command = new StringBuilder();

		switch (language.toUpperCase()) {
			case "RUST":
				command.append("echo ").append("'fn main(){").append(replace_dk(code)).append("}'").append(">> main.rs");
				command.append(";");
				command.append("/root/.cargo/bin/rustc --edition 2024 -o main main.rs");
				command.append(";");
				command.append("./main");
				break;
			case "JS":
				command.append("echo ").append("'").append(replace_dk(code)).append("'").append(">> main.js");
				command.append(";");
				command.append("/root/.bun/bin/bun ./main.js");
				break;
			case "TS":
				command.append("echo ").append("'").append(replace_dk(code)).append("'").append(">> main.ts");
				command.append(";");
				command.append("/root/.bun/bin/bun ./main.ts");
				break;
			case "BASH":
				command.append(code);
				break;
			default:
				throw new RuntimeException("言語名が不正です");
		}

		return Podman.run(command.toString());
	}
}
