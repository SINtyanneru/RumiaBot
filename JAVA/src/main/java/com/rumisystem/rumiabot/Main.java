package com.rumisystem.rumiabot;

public class Main {
	public static void main(String[] args) {
		System.out.println("    ____                  _       ____  ____  ______");
		System.out.println("   / __ \\__  ______ ___  (_)___ _/ __ )/ __ \\/_  __/");
		System.out.println("  / /_/ / / / / __ `__ \\/ / __ `/ __  / / / / / /   ");
		System.out.println(" / _, _/ /_/ / / / / / / / /_/ / /_/ / /_/ / / /    ");
		System.out.println("/_/ |_|\\__,_/_/ /_/ /_/_/\\__,_/_____/\\____/ /_/     ");
		System.out.println("V1.1");

		Thread PS1 = new Thread( new ProcessStart("JS", "node", "./SRC/Main.js"));
		PS1.start();


		Thread PS2 =new Thread( new ProcessStart("PY", "python", "./SRC/Main.py"));
		PS2.start();
	}

	public static void LOG(String TAG, String TEXT, int MODE){
		switch (MODE){
			case 0:{
				System.out.println("[ " + TAG + " ]" + TEXT);
				return;
			}
			case 1:{
				System.out.println("\u001B[31m[ " + TAG + " ]" + TEXT + "\u001B[0m");
				return;
			}
		}
	}
}
