package su.rumishistem.rumisanbot.base_system;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.Base64;
import java.util.Map;
import java.util.Map.Entry;

import su.rumishistem.rsdf_java.RSDFDecoder;
import su.rumishistem.rumisanbot.base_system.Software.RumiaBot.RumiaBot;
import su.rumishistem.rumisanbot.base_system.Software.Rumina.Rumina;
import su.rumishistem.rumisanbot.base_system.Software.藍.藍;
import su.rumishistem.rumisanbot.base_system.Type.ContentsSource;
import su.rumishistem.rumisanbot.base_system.Type.DiscordStatus;
import su.rumishistem.rumisanbot.base_system.Type.Message;
import su.rumishistem.rumisanbot.base_system.Type.NotePublicSetting;
import su.rumishistem.rumisanbot.base_system.Type.User;
import su.rumishistem.rumisanbot.base_system.Type.Event.ReceiveMessageEvent;

public class Main {
	public static String self_misskey_id = null;
	public static String self_misskey_uid = null;
	public static String self_discord_id = null;

	public static RumiaBot rumiabot;
	public static Rumina rumina;
	public static 藍 ai;

	public static void main(String[] args) throws IOException{
		Command.init();

		System.out.println("るみさんBOT BaseSystem");

		rumiabot = new RumiaBot();
		rumina = new Rumina();
		ai = new 藍();

		System.out.println("\\SYSTEM_START");

		Command.misskey_create_note("接続しました", null, null, NotePublicSetting.Home, false);
		Command.discord_change_status(DiscordStatus.オンライン);
		Command.discord_change_activity_watching("貴様");

		BufferedReader br = new BufferedReader(new InputStreamReader(System.in));
		String line;
		while ((line = br.readLine()) != null) {
			//イベント
			if (line.startsWith("@")) {
				String[] parts = line.substring(1).split(" ");
				String event_genre = parts[0];
				String event_name = parts[1];
				Map<String, Object> event_data = RSDFDecoder.decode(Base64.getDecoder().decode(parts[2])).get_dict();

				switch (event_genre) {
					case "MISSKEY": {
						switch (event_name) {
							case "SELF_USER": {
								self_misskey_id = (String)event_data.get("ID");
								self_misskey_uid = (String)event_data.get("UID");
								break;
							}

							case "STATS": {
								break;
							}

							case "JOBQUEUE": {
								break;
							}

							//case "MENTION":
							case "NOTE": {
								String id = (String)event_data.get("NOTE_ID");
								String text = (String)event_data.get("NOTE_TEXT");

								String user_id = (String)event_data.get("USER_ID");
								String user_uid = (String)event_data.get("USER_UID");
								String user_host = (String)event_data.get("USER_HOST");
								String user_name = (String)event_data.get("USER_NAME");
								String user_icon = (String)event_data.get("USER_ICON");

								boolean is_mention = false;

								if (text.contains("@"+self_misskey_uid)) {
									is_mention = true;
									text = text.replaceFirst("@"+self_misskey_uid, "");
									text = text.trim();
								}

								receive_message(ContentsSource.Misskey, is_mention, new Message(
									ContentsSource.Misskey,
									id,
									text,
									new User(
										user_id,
										user_uid,
										user_host,
										user_name,
										user_icon
									)
								));
								break;
							}
						}
					}

					case "DISCORD": {
						switch (event_name) {
							case "SELF_USER": {
								self_discord_id = (String)event_data.get("ID");
								break;
							}

							case "MESSAGE_RECEIVE": {
								String id = (String)event_data.get("MESSAGE_ID");
								String text = (String)event_data.get("MESSAGE_TEXT");
								String user_id = (String)event_data.get("USER_ID");
								String user_uid = (String)event_data.get("USER_UID");
								String user_name = (String)event_data.get("USER_NAME");
								String user_icon = (String)event_data.get("USER_ICON");
								boolean is_mention = false;

								if (text.contains("<@"+self_discord_id+">")) {
									is_mention = true;
									text = text.replaceFirst("<@"+self_discord_id+">", "");
									text = text.trim();
								}

								receive_message(ContentsSource.Discord, is_mention, new Message(
									ContentsSource.Discord,
									id,
									text,
									new User(
										user_id,
										user_uid,
										"dicord.com",
										user_name,
										user_icon
									)
								));
								break;
							}

							case "COMMAND_INTERACTION": {
								String interaction_id = (String)event_data.get("ID");
								String command_name = (String)event_data.get("COMMAND_NAME");
								Map<String, Object> command_option = (Map<String, Object>)event_data.get("COMMAND_OPTION");

								String user_id = (String)event_data.get("USER_ID");
								String user_uid = (String)event_data.get("USER_UID");
								String user_name = (String)event_data.get("USER_NAME");
								String user_icon = (String)event_data.get("USER_ICON");

								StringBuilder sb = new StringBuilder();
								sb.append(">");
								sb.append(command_name);
								sb.append(" ");

								for (Entry<String, Object> option:command_option.entrySet()) {
									String option_name = option.getKey();
									Map<String, Object> o = (Map<String, Object>)option.getValue();
									Map<String, Object> data = (Map<String, Object>)o.get("DATA");
									String option_type = (String)o.get("TYPE");

									sb.append(option_name);
									sb.append("=");

									switch (option_type) {
										case "STRING":
											sb.append("\""+data.get("VALUE")+"\"");
											break;
										case "INT":
											sb.append(data.get("VALUE"));
											break;
										case "BOOL":
											if ((boolean)data.get("VALUE")) {
												sb.append("true");
											} else {
												sb.append("false");
											}
											break;
										case "USER":
											sb.append("@"+data.get("VALUE")+"@discord.com");
											break;
										case "DISCORD_CHANNEL":
											sb.append("#"+data.get("VALUE")+"@discord.com");
											break;
										case "DISCORD_ROLE":
											sb.append("!"+data.get("VALUE")+"@discord.com");
											break;
										case "FILE":
											//ファイルは添付ファイルとして認識させるべき
											break;
									}

									sb.append(" ");
								}

								String text = sb.toString();
								System.out.println("Discordｽﾗｯｼｭｺﾏﾝﾄﾞをﾒｯｾｰｼﾞｺﾏﾝﾄﾞに変換: ["+sb.toString()+"]");

								receive_message(ContentsSource.Discord, true, new Message(
									ContentsSource.Discord,
									"!IT" + interaction_id,
									text,
									new User(
										user_id,
										user_uid,
										"dicord.com",
										user_name,
										user_icon
									)
								));
								break;
							}
						}
					}
				}
			}
		}
	}

	private static void receive_message(ContentsSource source, boolean is_mention, Message message) {
		boolean rb_return = rumiabot.receive_message(is_mention, new ReceiveMessageEvent(message));
		if (rb_return) return;

		if (is_mention) {
			if (message.text.startsWith("::")) {
				ai.receive_message(new ReceiveMessageEvent(message));
			} else {
				rumina.receive_message(new ReceiveMessageEvent(message));
			}
		} else {
			ai.receive_message(new ReceiveMessageEvent(message));
			rumina.receive_message(new ReceiveMessageEvent(message));
		}
	}
}
