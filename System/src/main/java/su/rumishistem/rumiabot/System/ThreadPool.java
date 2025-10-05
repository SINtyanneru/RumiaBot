package su.rumishistem.rumiabot.System;

import static su.rumishistem.rumiabot.System.Main.*;
import java.util.concurrent.*;
import su.rumishistem.rumi_java_lib.ArrayNode;
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

	public static void run_command(Runnable fn) {
		command.submit(fn);
	}

	public static void discord(Runnable fn) {
		discord_event.submit(fn);
	}
}
