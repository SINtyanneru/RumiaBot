package su.rumishistem.rumisanbot;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.nio.charset.StandardCharsets;
import java.util.Base64;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.CountDownLatch;

import net.dv8tion.jda.api.OnlineStatus;
import net.dv8tion.jda.api.entities.Activity;
import su.rumishistem.rumi_java_logger.SeverityLevel;
import su.rumishistem.rumisanbot.Misskey.API;

public class BaseSystem {
	private static ArrayBlockingQueue<String> bw_queue = new ArrayBlockingQueue<>(100);
	private static CountDownLatch shutdown_cdl = new CountDownLatch(1);

	public static void boot() throws IOException {
		Main.logger.print(SeverityLevel.Notice, "ﾍﾞｰｽｼｽﾃﾑを起動しています...");

		ProcessBuilder pb = new ProcessBuilder(Main.JVM_PATH, "-jar", "./build/os.jar");
		Process p = pb.start();

		new Thread(new Runnable() {
			@Override
			public void run() {
				try {
					BufferedReader br = new BufferedReader(new InputStreamReader(p.getInputStream()));
					String line;
					while ((line = br.readLine()) != null) {
						try {
							if (line.startsWith("\\")) {
								receive_event(line.substring(1));
							} else if (line.startsWith("/")) {
								receive_command(line.substring(1).split(" "));
							} else if (line.startsWith(">")) {
								Main.logger.print(SeverityLevel.Debug, "[ BaseSystem ] " + line.substring(1));
							}
						} catch (Exception ex) {
							ex.printStackTrace();
						}
					}
				} catch (IOException ex) {
					ex.printStackTrace();
				}
			}
		}).start();

		//読む
		new Thread(new Runnable() {
			@Override
			public void run() {
				BufferedWriter bw = new BufferedWriter(new OutputStreamWriter(p.getOutputStream()));
				while (true) {
					try {
						String message = bw_queue.take();
						bw.write(message + "\n");
						bw.flush();
					} catch (Exception ex) {
						ex.printStackTrace();
					}
				}
			}
		}).start();

		//シャットダウン処理
		Runtime.getRuntime().addShutdownHook(new Thread(new Runnable() {
			@Override
			public void run() {
				try {
					send_basesystem("@SYSTEM_EXIT");
					shutdown_cdl.await();
				} catch (InterruptedException ex) {
					System.out.println("異常終了");
					return;
				}
			}
		}));

		Main.logger.print(SeverityLevel.Ok, "ﾍﾞｰｽｼｽﾃﾑが起動しました、BOTは正常です。");
	}

	private static void receive_event(String arg) {
		System.out.println("[ BaseSystem ] ｲﾍﾞﾝﾄ:" + arg);
		if (arg.equals("SHUTDOWN")) {
			shutdown_cdl.countDown();
		}
	}

	private static void receive_command(String[] cmd) throws IOException, InterruptedException {
		String cmd_id = cmd[cmd.length - 1].substring(1, cmd[cmd.length - 1].length() -1);
		System.out.println("[ BaseSystem ] Command:" + cmd_id);

		switch (cmd[0]) {
			case "MISSKEY": {
				if (cmd[1].equals("NOTE")) {
					String note_text = new String(Base64.getDecoder().decode(cmd[2]), StandardCharsets.UTF_8);
					String reply_id = cmd[3];
					String quote_id = cmd[4];
					String public_setting = "public";
					boolean local_only = cmd[6].equals("true");

					switch (cmd[5]) {
						case "PUBLIC":
							public_setting = "public";
							break;
						case "HOME":
							public_setting = "home";
							break;
						case "DM":
							public_setting = "specified";
							break;
					}

					if (reply_id.equals("null")) reply_id = null;
					if (quote_id.equals("null")) quote_id = null;

					API.create_note(note_text, reply_id, quote_id, public_setting, local_only);
					return;
				}
			}

			case "DISCORD": {
				if (cmd[1].equals("STATUS")) {
					Main.logger.print(SeverityLevel.Notice, "ｽﾃｰﾀｽ変更: " + cmd[2]);

					switch (cmd[2]) {
						case "ONLINE":
							Bot.get_discord().get_jda().getPresence().setStatus(OnlineStatus.ONLINE);
							return;
						case "IDLE":
							Bot.get_discord().get_jda().getPresence().setStatus(OnlineStatus.IDLE);
							return;
						case "DO_NOT_DISTURB":
							Bot.get_discord().get_jda().getPresence().setStatus(OnlineStatus.DO_NOT_DISTURB);
							return;
						case "OFFLINE":
							Bot.get_discord().get_jda().getPresence().setStatus(OnlineStatus.OFFLINE);
							return;
					}
				} else if (cmd[1].equals("ACTIVITY")) {
					String text = new String(Base64.getDecoder().decode(cmd[3]), StandardCharsets.UTF_8);
					Main.logger.print(SeverityLevel.Notice, "ｱｸﾃｨﾋﾞﾃｨ変更: " + cmd[2] + "("+text+")");

					switch (cmd[2]) {
						case "PLAYING":
							Bot.get_discord().get_jda().getPresence().setActivity(Activity.playing(text));
							return;
						case "WATCHING":
							Bot.get_discord().get_jda().getPresence().setActivity(Activity.watching(text));
							return;
						case "STREAMING":
							Bot.get_discord().get_jda().getPresence().setActivity(Activity.streaming(text, new String(Base64.getDecoder().decode(cmd[4]), StandardCharsets.UTF_8)));
							return;
					}
				}
			}

			default:
				send_basesystem("<"+cmd_id+"> 0x4000");
				break;
		}
	}

	public static void send_basesystem(String message) {
		bw_queue.offer(message);
	}
}
