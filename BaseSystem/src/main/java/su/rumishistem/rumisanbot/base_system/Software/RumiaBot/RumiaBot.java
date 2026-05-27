package su.rumishistem.rumisanbot.base_system.Software.RumiaBot;

import java.util.*;
import java.util.Map.Entry;
import su.rumishistem.rumisanbot.base_system.Command;
import su.rumishistem.rumisanbot.base_system.Software.RumiaBot.Runer.TestCommand;
import su.rumishistem.rumisanbot.base_system.Software.RumiaBot.Type.*;
import su.rumishistem.rumisanbot.base_system.Software.RumiaBot.Type.Event.CommandEvent;
import su.rumishistem.rumisanbot.base_system.Tool.*;
import su.rumishistem.rumisanbot.base_system.Type.*;
import su.rumishistem.rumisanbot.base_system.Type.Event.*;

public class RumiaBot {
	private final CommandData[] command_list;

	public RumiaBot() {
		command_list = new CommandData[] {
			new CommandData("test", "テストコマンドだお", new CommandOptionData[0], false, new TestCommand())
		};
	}

	/**
	 * るみあBOTが応答したのならばtrueを返す
	 */
	public boolean receive_message(boolean is_mention, ReceiveMessageEvent e) {
		System.out.println("るみあBOT->" + e.get_message().text);
		//コマンド
		if (e.get_message().text.startsWith(">")) {
			String command = e.get_message().text.substring(1);

			//Misskeyならメンションをしているか？
			if (e.get_message().source == ContentsSource.Misskey) {
				if (is_mention == false) return false;
			}

			//コマンド名を抽出
			String command_name = command.split(" ")[0];

			//オプション
			List<String> option_temp = new ArrayList<>();
			StringBuilder sb = new StringBuilder();
			for (char c:(command.substring(command.indexOf(' ') + 1) + " ").toCharArray()) {
				if (c == ' ') {
					option_temp.add(sb.toString());
					sb.delete(0, sb.length());
					continue;
				}
				sb.append(c);
			}

			Map<String, CommandOptionValue> option_list = new HashMap<>();
			for (String option_text: option_temp) {
				int index = option_text.indexOf('=');
				if (index == -1) continue;
				String name = option_text.substring(0, index);
				String value_string = option_text.substring(index + 1);
				CommandOptionValue value;

				if (value_string.startsWith("\"") && value_string.endsWith("\"")) {
					//「"」で囲まれているなら文字列
					value = new CommandOptionValue(value_string.subSequence(1, value_string.length() - 2), CommandOptionValue.Type.String);
				} else if (value_string.matches("\\d+")) {
					//数字のみなら数値
					value = new CommandOptionValue(Integer.parseInt(value_string), CommandOptionValue.Type.Int);
				} else if (value_string.equals("true") || value_string.equals("false")) {
					//「true」か「false」ならブール
					value = new CommandOptionValue(value_string.equals("true"), CommandOptionValue.Type.Boolean);
				} else {
					//終わってるオプションなので拒否
					return true;
				}

				option_list.put(name, value);
			}

			//コマンド一覧から探す
			CommandData cmd = get_command_from_name(command_name);
			if (cmd == null) {
				command_error_reply(e, "コマンドが見つかりませんでした💀");
				return true;
			}

			if (e.get_message().source == ContentsSource.Discord && !e.get_message().id.startsWith("!IT")) {
				String channel_id = DiscordMessageID.parse(e.get_message().id)[1];
				String message_id = DiscordMessageID.parse(e.get_message().id)[2];
				Command.discord_reaction(channel_id, message_id, "1039992459209490513", "1508072889566367886");
				Command.discord_reaction(channel_id, message_id, "1039992459209490513", "1509009044025638993");
			} else if (e.get_message().source == ContentsSource.Misskey) {
				Command.misskey_note_reaction(e.get_message().id, ":1039992459209490513:");
			}

			//実行
			try {
				cmd.runer.run(new CommandEvent(e, option_list));
			} catch (Exception ex) {
				command_error_reply(e, "エラー: `"+ex.getMessage()+"`");
			}

			//ログ
			System.out.println("┌─("+e.get_user().uid+"@"+e.get_user().host+")-["+e.get_message().source.name()+"]");
			System.out.println("├──> "+command_name);
			for (Entry<String, CommandOptionValue> option:option_list.entrySet()) {
				System.out.println("├> "+option.getKey()+" => "+option.getValue());
			}
			System.out.println("└────> Runing...");

			return true;
		}

		return false;
	}

	private CommandData get_command_from_name(String name) {
		for (CommandData cmd:command_list) {
			if (cmd.name.equalsIgnoreCase(name)) {
				return cmd;
			}
		}

		return null;
	}

	private void command_error_reply(ReceiveMessageEvent e, String text) {
		if (e.get_message().id.startsWith("!IT")) {
			Command.discord_interaction_defer(e.get_message().id, true);
			Command.discord_interaction_reply(e.get_message().id, true, text);
		} else if (e.get_message().source == ContentsSource.Misskey) {
			Command.misskey_create_note(text, e.get_message().id, null, NotePublicSetting.Home, false);
		} else if (e.get_message().source == ContentsSource.Discord) {
			String channel_id = DiscordMessageID.parse(e.get_message().id)[1];
			String message_id = DiscordMessageID.parse(e.get_message().id)[2];
			Command.discord_reply_message(channel_id, message_id, text);
		}
	}
}
