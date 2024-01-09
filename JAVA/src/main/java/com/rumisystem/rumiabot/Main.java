package com.rumisystem.rumiabot;

public class Main {
	public static void main(String[] args) {
		System.out.println("    ____                  _       ____  ____  ______");
		System.out.println("   / __ \\__  ______ ___  (_)___ _/ __ )/ __ \\/_  __/");
		System.out.println("  / /_/ / / / / __ `__ \\/ / __ `/ __  / / / / / /   ");
		System.out.println(" / _, _/ /_/ / / / / / / / /_/ / /_/ / /_/ / / /    ");
		System.out.println("/_/ |_|\\__,_/_/ /_/ /_/_/\\__,_/_____/\\____/ /_/     ");
		System.out.println("V1.1");

		ProcessStart PS1 = new ProcessStart("JS", "ping", "192.168.0.3");
		PS1.start();

		ProcessStart PS2 = new ProcessStart("JS", "ping", "192.168.0.132");
		PS2.start();
	}
}
