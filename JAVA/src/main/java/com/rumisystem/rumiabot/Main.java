package com.rumisystem.rumiabot;

import com.rumisystem.rumiabot.TELNET.TELNET_SERVER;

public class Main {
	public static void main(String[] args) throws Exception {
		System.out.println("    ____                  _       ____  ____  ______");
		System.out.println("   / __ \\__  ______ ___  (_)___ _/ __ )/ __ \\/_  __/");
		System.out.println("  / /_/ / / / / __ `__ \\/ / __ `/ __  / / / / / /   ");
		System.out.println(" / _, _/ /_/ / / / / / / / /_/ / /_/ / /_/ / / /    ");
		System.out.println("/_/ |_|\\__,_/_/ /_/ /_/_/\\__,_/_____/\\____/ /_/     ");
		System.out.println("V1.1");

		System.out.println("[ *** ]Staring WS Server...");

		Thread WS = new Thread(() -> {
			try {
				TELNET_SERVER.main();
			} catch (Exception e) {
				throw new RuntimeException(e);
			}
		});
		WS.start();

		Thread PS1 = new Thread( new ProcessStart("JDA", new String[]{"java", "-jar", "./SRC/JDA/app/build/libs/app-all.jar"}));
		PS1.start();

		//Thread PS2 =new Thread( new ProcessStart("PY", "python", "./SRC/PY/Main.py"));
		//PS2.start();

		Thread PS3 = new Thread( new ProcessStart("JAVA", new String[]{"java", "-jar", "./SRC/JAVA/app/build/libs/app-all.jar"}));
		PS3.start();

		Thread PS4 = new Thread( new ProcessStart("JS", new String[]{"node", "./SRC/JS/Main.js"}));
		PS4.start();
	}

	public static void LOG(String TAG, String TEXT, int MODE){
		switch (MODE){
			case 0:{
				System.out.println("[ " + TAG + " ]" + TEXT);
				return;
			}
			case 1:{
				System.out.println("\u001B[41m\u001B[37m\u001B[1m[ " + TAG + " ]" + TEXT + "\u001B[0m");
				return;
			}
		}
	}
}
