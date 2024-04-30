package com.rumisystem.rumiabot.jda.MODULE;

import java.awt.*;
import java.util.Random;

public class RND_COLOR {
	private int RED = 0;
	private int GRN = 0;
	private int BLE = 0;

	public RND_COLOR(){
		Random RND = new Random();

		//色を乱数生成
		RED = RND.nextInt(256); //赤
		GRN = RND.nextInt(256); //緑
		BLE = RND.nextInt(256); //青
	}

	public String GEN(){
		return String.format("#%02X%02X%02X", RED, GRN, BLE);
	}

	public Color GEN_COLOR(){
		return new Color(RED, GRN, BLE);
	}
}
