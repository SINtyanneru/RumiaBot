package su.rumishistem.rumisanbot.base_system.Software.RumiaBot;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import su.rumishistem.rumisanbot.base_system.Type.ContentsSource;
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

			Map<String, String> option_list = new HashMap<>();
			for (String option_text: option_temp) {
				int index = option_text.indexOf('=');
				String name = option_text.substring(0, index);
				String value = option_text.substring(index + 1);
				option_list.put(name, value);
			}

			System.out.println("┌─("+e.get_user().uid+"@"+e.get_user().host+")-["+e.get_message().source.name()+"]");
			System.out.println("├──> "+command_name);

			for (Entry<String, String> option:option_list.entrySet()) {
				System.out.println("├> "+option.getKey()+" => "+option.getValue());
			}
			System.out.println("└────> Runing...");

			return true;
		}

		return false;
	}

	public void run_command() {

	}
}
