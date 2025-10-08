package su.rumishistem.rumiabot.System;

import static su.rumishistem.rumiabot.System.Main.*;

import java.io.IOException;
import java.util.UUID;
import java.util.concurrent.*;

import net.dv8tion.jda.api.entities.channel.concrete.TextChannel;
import net.dv8tion.jda.api.interactions.commands.SlashCommandInteraction;
import su.rumishistem.rumi_java_lib.ArrayNode;
import su.rumishistem.rumi_java_lib.EXCEPTION_READER;
import su.rumishistem.rumi_java_lib.ExceptionRunnable;
import su.rumishistem.rumi_java_lib.Misskey.Builder.NoteBuilder;
import su.rumishistem.rumi_java_lib.Misskey.Event.NewNoteEvent;
import su.rumishistem.rumiabot.System.TYPE.*;

public class ThreadPool {
	private static ThreadPoolExecutor command = null;
	private static ThreadPoolExecutor message_event = null;
	private static ThreadPoolExecutor discord_event = null;

	public static void init() {
		ArrayNode config = CONFIG_DATA.get("THREAD_POOL");

		command = (ThreadPoolExecutor)Executors.newFixedThreadPool(config.getData("COMMAND").asInt());
		message_event = (ThreadPoolExecutor)Executors.newFixedThreadPool(config.getData("MESSAGE_EVENT").asInt());
		discord_event = (ThreadPoolExecutor)Executors.newFixedThreadPool(config.getData("DISCORD_EVENT").asInt());
	}

	public static ThreadPoolStatus get_command_status() {
		ArrayNode config = CONFIG_DATA.get("THREAD_POOL");
		return new ThreadPoolStatus(command.getActiveCount(), config.getData("COMMAND").asInt());
	}

	public static ThreadPoolStatus get_message_status() {
		ArrayNode config = CONFIG_DATA.get("THREAD_POOL");
		return new ThreadPoolStatus(message_event.getActiveCount(), config.getData("MESSAGE_EVENT").asInt());
	}

	public static ThreadPoolStatus get_discord_status() {
		ArrayNode config = CONFIG_DATA.get("THREAD_POOL");
		return new ThreadPoolStatus(discord_event.getActiveCount(), config.getData("DISCORD_EVENT").asInt());
	}

	public static void receive_message(SourceType source, ReceiveMessageEvent e) {
		//イベント着火
		for (FunctionClass Function:FunctionModuleList) {
			message_event.submit(new Runnable() {
				@Override
				public void run() {
					Function.ReceiveMessage(e);
				}
			});
		}
	}

	public static void run_discord_command(ExceptionRunnable fn, SlashCommandInteraction e, boolean defer) {
		command.submit(new Runnable() {
			@Override
			public void run() {
				try {
					fn.run();
				} catch (Exception ex) {
					String ex_text = EXCEPTION_READER.READ(ex);
					String id = UUID.randomUUID().toString();
					ex.printStackTrace();

					//エラーを吐き出すチャンネル
					TextChannel ch = DISCORD_BOT.getTextChannelById("1382127695273529455");
					if (ch != null) {
						if (defer) {
							e.getHook().editOriginal("エラー:" + ex.getMessage() + "\n["+id+"]").queue();
						} else {
							e.reply("エラー:" + ex.getMessage() + "\n["+id+"]").queue();
						}

						ch.sendMessage("["+id+"]\n```" + ex_text + "\n```").queue();
					}
				}
			}
		});
	}

	public static void run_misskey_command(ExceptionRunnable fn, NewNoteEvent e) {
		command.submit(new Runnable() {
			@Override
			public void run() {
				try {
					fn.run();
				} catch (Exception ex) {
					String ex_text = EXCEPTION_READER.READ(ex);
					String id = UUID.randomUUID().toString();
					ex.printStackTrace();

					//エラーを吐き出すチャンネル
					TextChannel ch = DISCORD_BOT.getTextChannelById("1382127695273529455");
					if (ch != null) {
						try {
							NoteBuilder NB = new NoteBuilder();
							NB.setTEXT("エラー:" + ex.getMessage() + "\n["+id+"]");
							NB.setREPLY(e.getNOTE());
							MisskeyBOT.PostNote(NB.Build());
						} catch (IOException EX) {
							//なにもしない
						}

						ch.sendMessage("["+id+"]\n```" + ex_text + "\n```").queue();
					}
				}
			}
		});
	}

	public static void discord(Runnable fn) {
		discord_event.submit(fn);
	}
}
