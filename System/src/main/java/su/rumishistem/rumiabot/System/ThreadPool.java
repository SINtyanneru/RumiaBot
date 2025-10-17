package su.rumishistem.rumiabot.System;

import static su.rumishistem.rumiabot.System.Main.config;

import java.util.concurrent.Executors;
import java.util.concurrent.ThreadPoolExecutor;

import su.rumishistem.rumi_java_lib.ArrayNode;

public class ThreadPool {
	private static ThreadPoolExecutor command_pool;
	private static ThreadPoolExecutor message_event;
	private static ThreadPoolExecutor event_pool;

	public static void init() {
		ArrayNode pool_config = config.get("THREAD_POOL");

		command_pool = (ThreadPoolExecutor)Executors.newFixedThreadPool(pool_config.getData("COMMAND").asInt());
		message_event = (ThreadPoolExecutor)Executors.newFixedThreadPool(pool_config.getData("MESSAGE_EVENT").asInt());
		event_pool = (ThreadPoolExecutor)Executors.newFixedThreadPool(pool_config.getData("EVENT").asInt());
	}

	public static void run_command(Runnable task) {
		command_pool.submit(task);
	}
}
