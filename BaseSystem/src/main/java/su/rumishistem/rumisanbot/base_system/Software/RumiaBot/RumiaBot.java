package su.rumishistem.rumisanbot.base_system.Software.RumiaBot;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import su.rumishistem.rumisanbot.base_system.Command;
import su.rumishistem.rumisanbot.base_system.Software.RumiaBot.Type.CommandOptionValue;
import su.rumishistem.rumisanbot.base_system.Type.ContentsSource;
import su.rumishistem.rumisanbot.base_system.Type.NotePublicSetting;
import su.rumishistem.rumisanbot.base_system.Type.Event.ReceiveMessageEvent;

public class RumiaBot {
	public RumiaBot() {

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

			System.out.println("┌─("+e.get_user().uid+"@"+e.get_user().host+")-["+e.get_message().source.name()+"]");
			System.out.println("├──> "+command_name);

			for (Entry<String, CommandOptionValue> option:option_list.entrySet()) {
				System.out.println("├> "+option.getKey()+" => "+option.getValue());
			}
			System.out.println("└────> Runing...");

			if (command_name.equals("test")) {
				String return_text = "こにゃん";
				//Discordのインタラクションか？
				if (e.get_message().id.startsWith("!IT")) {
					Command.discord_interaction_defer(e.get_message().id, false);
					Command.discord_interaction_reply(e.get_message().id, true, return_text);
				} else if (e.get_message().source == ContentsSource.Misskey) {
					Command.misskey_create_note(return_text, e.get_message().id, null, NotePublicSetting.Home, false);
				} else if (e.get_message().source == ContentsSource.Discord) {
					//作る
				}
			}

			return true;
		}

		return false;
	}

	public void run_command() {

	}
}
