package com.rumisystem.rumiabot.jda.COMMAND;

import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.utils.FileUpload;

import java.awt.Color;
import java.awt.image.BufferedImage;
import javax.imageio.ImageIO;
import java.io.File;
import java.io.IOException;

//参考：https://qiita.com/ur_kinsk/items/82aa609d799256258010
public class mandenburo {
	private static final int WIDTH = 1000;
	private static final int HEIGHT = 1000;
	private static final String PATH = "./DOWNLOAD/MANDENBRO/";


	public static void main(SlashCommandInteractionEvent INTERACTION) {
		try{
			int[] PIXELS = new int[WIDTH*HEIGHT];
			for(int I = 0; I < WIDTH; I++){
				for(int J = 0; J < HEIGHT; J++){
					PIXELS[WIDTH * J +I] = CALC_MANDENBRO(I, J);
				}
			}
			BufferedImage IMAGE = new BufferedImage(WIDTH, HEIGHT, BufferedImage.TYPE_INT_ARGB);
			IMAGE.setRGB(0, 0, WIDTH, HEIGHT, PIXELS, 0, WIDTH);
			File IMAGE_FILE = new File(PATH + INTERACTION.getMember().getUser().getId() + ".png");

			//ファイルに完成品を書き込む
			ImageIO.write(IMAGE, "png", IMAGE_FILE);

			//保存する
			INTERACTION.getHook().editOriginal("できた").setAttachments(FileUpload.fromData(IMAGE_FILE)).queue();
		} catch (Exception EX) {
			EX.printStackTrace();
			INTERACTION.getHook().editOriginal("無理でした\n" + EX.getMessage()).queue();
		}
	}

	//マンデルブロ集合のパラメータ
	private static final double MIN_X = -2.1;
	private static final double MAX_X = 0.5;
	private static final double MIN_Y = -1.3;
	private static final double MAX_Y = 1.3;
	private static final int MAX_I = 300;

	//点(I, J)に対する色をAHSVで計算
	private static int CALC_MANDENBRO(int I, int J){
		final double C = MIN_X + I * (MAX_X-MIN_X) / WIDTH;
		final double D = MIN_Y + J * (MAX_Y-MIN_Y) / HEIGHT;
		double X1 = 0.0, Y1 = 0.0, X2, Y2;
		int I_ = 0;
		for(I_ = 0; I_ < MAX_I; I_++){
			X2 = X1 * X1 - Y1 * Y1 + C;
			Y2 = 2 * X1 * Y1 + D;

			if((X2 * X2 + Y2 * Y2) > 4.0){
				break;
			} else {
				X1 = X2;
				Y1 = Y2;
			}
		}
		final float T = (float)I_ / MAX_I;
		if(T >= 1.0f){
			return 0xff000000; //black
		}else{
			return 0xff000000 | Color.HSBtoRGB(T, 0.6f, 1.0f);
		}
	}
}
