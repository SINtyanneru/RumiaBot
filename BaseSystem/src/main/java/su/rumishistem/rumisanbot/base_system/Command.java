package su.rumishistem.rumisanbot.base_system;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PipedInputStream;
import java.io.PipedOutputStream;
import java.io.PrintStream;
import java.nio.charset.StandardCharsets;
import java.util.Base64;
import java.util.HashMap;
import java.util.UUID;

import su.rumishistem.rsdf_java.RSDFEncoder;
import su.rumishistem.rumisanbot.base_system.Type.*;

public class Command {
	private static PrintStream stdout = null;

	public static void init() throws IOException {
		stdout = System.out;

		//出力
		PipedOutputStream out_pos = new PipedOutputStream();
		PipedInputStream out_pis = new PipedInputStream(out_pos);
		PrintStream out = new PrintStream(out_pos, true, StandardCharsets.UTF_8);
		System.setOut(out);

		new Thread(new Runnable() {
			@Override
			public void run() {
				try {
					BufferedReader br = new BufferedReader(new InputStreamReader(out_pis, StandardCharsets.UTF_8));
					try {
						String line;
						while ((line = br.readLine()) != null) {
							print_debug(line);
						}
					} finally {
						br.close();
					}
				} catch (IOException ex) {
					ex.printStackTrace();
				}
			}
		}).start();

		Runtime.getRuntime().addShutdownHook(new Thread(new Runnable() {
			@Override
			public void run() {
				try {
					out_pis.close();
				} catch (Exception e) {
					return;
				}
			}
		}));
	}

	public static void print_debug(String message) {
		stdout.println(">" + message);
		stdout.flush();
	}

	public static void misskey_create_note(String text, String reply, String quote, NotePublicSetting public_setting, boolean local_only) {
		StringBuilder sb = new StringBuilder();
		sb.append("/MISSKEY NOTE ");
		sb.append(Base64.getEncoder().encodeToString(text.getBytes()));
		sb.append(" ");

		if (reply != null) {
			sb.append(reply);
		} else {
			sb.append("null");
		}

		sb.append(" ");

		if (quote != null) {
			sb.append(quote);
		} else {
			sb.append("null");
		}

		sb.append(" ");
		sb.append(public_setting.name().toUpperCase());

		sb.append(" ");
		if (local_only) {
			sb.append("true");
		} else {
			sb.append("false");
		}

		sb.append("<"+UUID.randomUUID().toString()+">");

		stdout.println(sb.toString());
		stdout.flush();
	}

	public static void misskey_follow_user(String user_id) {
		stdout.println("/MISSKEY FOLLOW " + Base64.getEncoder().encodeToString(user_id.getBytes()));
		stdout.flush();
	}

	public static void misskey_unfollow_user(String user_id) {
		stdout.println("/MISSKEY UNFOLLOW " + Base64.getEncoder().encodeToString(user_id.getBytes()));
		stdout.flush();
	}

	public static void misskey_block_user(String user_id) {
		stdout.println("/MISSKEY BLOCK " + Base64.getEncoder().encodeToString(user_id.getBytes()));
		stdout.flush();
	}

	public static void misskey_unblock_user(String user_id) {
		stdout.println("/MISSKEY UNBLOCK " + Base64.getEncoder().encodeToString(user_id.getBytes()));
		stdout.flush();
	}

	public static void discord_change_status(DiscordStatus status) {
		String name = "";
		switch (status) {
			case オンライン:
				name = "ONLINE";
				break;
			case オフライン:
				name = "OFFLINE";
				break;
			case 退席中:
				name = "IDLE";
				break;
			case 取り込み中:
				name = "DO_NOT_DISTURB";
				break;
		}

		stdout.println("/DISCORD STATUS "+name+" <"+UUID.randomUUID().toString()+">");
		stdout.flush();
	}

	public static void discord_change_activity_playing(String text) {
		stdout.println("/DISCORD ACTIVITY PLAYING "+Base64.getEncoder().encodeToString(text.getBytes())+" <"+UUID.randomUUID().toString()+">");
		stdout.flush();
	}

	public static void discord_change_activity_watching(String text) {
		stdout.println("/DISCORD ACTIVITY WATCHING "+Base64.getEncoder().encodeToString(text.getBytes())+" <"+UUID.randomUUID().toString()+">");
		stdout.flush();
	}

	public static void discord_change_activity_streaming(String text, String url) {
		stdout.println("/DISCORD ACTIVITY STREAMING "+Base64.getEncoder().encodeToString(text.getBytes())+" "+Base64.getEncoder().encodeToString(url.getBytes())+" <"+UUID.randomUUID().toString()+">");
		stdout.flush();
	}

	public static void discord_interaction_defer(String id, boolean is_private) {
		if (id.startsWith("!IT")) id = id.substring(3);

		StringBuilder sb = new StringBuilder();
		sb.append("/DISCORD INTERACTION");

		sb.append(" ");
		if (is_private) {
			sb.append("PRIVATE_DEFER");
		} else {
			sb.append("PUBLIC_DEFER");
		}

		sb.append(" ");
		sb.append(id);

		sb.append(" <"+UUID.randomUUID().toString()+">");
		stdout.println(sb.toString());
		stdout.flush();
	}

	public static void discord_interaction_reply(String id, boolean is_defer, String text) {
		if (id.startsWith("!IT")) id = id.substring(3);

		StringBuilder sb = new StringBuilder();
		sb.append("/DISCORD INTERACTION");

		sb.append(" ");
		sb.append("REPLY");

		sb.append(" ");
		sb.append(id);

		sb.append(" ");
		if (is_defer) {
			sb.append("DEFER_REPLY");
		} else {
			sb.append("REPLY");
		}

		try {
			sb.append(" ");
			sb.append(Base64.getEncoder().encodeToString(RSDFEncoder.encode(new HashMap<String, Object>(){{
				put("TEXT", text);
			}})));
		} catch (IOException ex) {
		}

		sb.append(" <"+UUID.randomUUID().toString()+">");
		stdout.println(sb.toString());
		stdout.flush();
	}

	public static void discord_send_message(String channel_id, String text) {
		StringBuilder sb = new StringBuilder();
		sb.append("/DISCORD MESSAGE SEND");

		sb.append(" ");
		sb.append(channel_id);
		sb.append(" ");
		sb.append(Base64.getEncoder().encodeToString(text.getBytes(StandardCharsets.UTF_8)));
		sb.append(" ");
		sb.append("null");

		stdout.println(sb.toString());
		stdout.flush();
	}

		public static void discord_reply_message(String channel_id, String reply_target, String text) {
		StringBuilder sb = new StringBuilder();
		sb.append("/DISCORD MESSAGE SEND");

		sb.append(" ");
		sb.append(channel_id);
		sb.append(" ");
		sb.append(Base64.getEncoder().encodeToString(text.getBytes(StandardCharsets.UTF_8)));
		sb.append(" ");
		sb.append(reply_target);

		stdout.println(sb.toString());
		stdout.flush();
	}
}
