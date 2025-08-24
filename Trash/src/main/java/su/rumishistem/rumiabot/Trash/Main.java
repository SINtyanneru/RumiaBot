package su.rumishistem.rumiabot.Trash;

import static su.rumishistem.rumi_java_lib.LOG_PRINT.Main.LOG;
import static su.rumishistem.rumiabot.System.FunctionModuleLoader.AddCommand;

import java.io.IOException;

import net.dv8tion.jda.api.interactions.components.ActionRow;
import net.dv8tion.jda.api.interactions.components.text.TextInput;
import net.dv8tion.jda.api.interactions.components.text.TextInputStyle;
import net.dv8tion.jda.api.interactions.modals.Modal;
import su.rumishistem.rumi_java_lib.LOG_PRINT.LOG_TYPE;
import su.rumishistem.rumiabot.System.TYPE.CommandData;
import su.rumishistem.rumiabot.System.TYPE.CommandInteraction;
import su.rumishistem.rumiabot.System.TYPE.CommandOption;
import su.rumishistem.rumiabot.System.TYPE.CommandOptionType;
import su.rumishistem.rumiabot.System.TYPE.FunctionClass;
import su.rumishistem.rumiabot.System.TYPE.ReceiveMessageEvent;
import su.rumishistem.rumiabot.System.TYPE.ReturnInteractionEvent;

public class Main implements FunctionClass {
	private static final String FUNCTION_NAME = "使い捨て環境";
	private static final String FUNCTION_VERSION = "1.0";
	private static final String FUNCTION_AUTOR = "Rumisan";

	@Override
	public String FUNCTION_NAME() {
		return FUNCTION_NAME;
	}
	@Override
	public String FUNCTION_VERSION() {
		return FUNCTION_VERSION;
	}
	@Override
	public String FUNCTION_AUTOR() {
		return FUNCTION_AUTOR;
	}

	@Override
	public void Init() {
		try {
			if (!Podman.check_installed_image()) {
				LOG(LOG_TYPE.INFO, "使い捨て環境のイメージの作成を開始しました");
				Podman.build_debian_image();
				LOG(LOG_TYPE.OK, "使い捨て環境が完成しました！");
			}

			AddCommand(new CommandData("exec", new CommandOption[] {
				new CommandOption("language", CommandOptionType.String, "", true),
				new CommandOption("code", CommandOptionType.String, "", true)
			}, false));

			AddCommand(new CommandData("exec-modal", new CommandOption[] {
				new CommandOption("language", CommandOptionType.String, "", true)
			}, true));
		} catch (Exception EX) {
			EX.printStackTrace();
			LOG(LOG_TYPE.FAILED, "使い捨て環境の構築に失敗しました！");
		}
	}

	@Override
	public void ReceiveMessage(ReceiveMessageEvent e) {}

	@Override
	public boolean GetAllowCommand(String Name) {
		return (Name.equals("exec") || Name.equals("exec-modal") || Name.equals("Modal:exec-modal"));
	}

	@Override
	public void RunCommand(CommandInteraction CI) throws Exception {
		String language = CI.GetCommand().GetOption("language").GetValueAsString().toUpperCase();

		if (CI.GetCommand().GetName().equals("exec-modal")) {
			TextInput Text = TextInput.create("code", "コード", TextInputStyle.PARAGRAPH).setPlaceholder("...").setRequired(true).build();
			Modal modal = Modal.create("exec-modal?" + language, "あ").addComponents(ActionRow.of(Text)).build();
			CI.GetDiscordInteraction().replyModal(modal).queue();
			return;
		}

		try {
			String result = exec(language, CI.GetCommand().GetOption("code").GetValueAsString());
			result = sanitize(result);

			CI.Reply("```\n"+result+"\n```");
		} catch (RuntimeException ex) {
			CI.Reply("実行エラー\n```\n"+ex.getMessage()+"\n```");
		}
	}

	@Override
	public void ReturnInteraction(ReturnInteractionEvent Interaction) throws Exception {
		if (Interaction.getType() == su.rumishistem.rumiabot.System.TYPE.ReturnInteractionEvent.InteractionType.Modal) {
			String language = Interaction.getModal().getModalId().split("\\?")[1];
			String code = Interaction.getModal().getValue("code").getAsString();

			try {
				String result = exec(language, code);
				result = sanitize(result);

				Interaction.getModal().reply("```\n"+result+"\n```").queue();
			} catch (RuntimeException ex) {
				Interaction.getModal().reply("実行エラー\n```\n"+ex.getMessage()+"\n```").queue();
			}
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
