package su.rumishistem.rumiabot.MakeItQuote;

import java.awt.Color;
import java.awt.image.BufferedImage;

public class GrayScaleConvert {
	public enum ConvertMode {
		NTSC_601,			//NTSC
		BT_709,			//BT.709
		BT_2020,			//BT.2020
		AVERAGE,			//加重無し
		WEIGHTED_ROOT		//加重レート平均
	}

	public static BufferedImage toGrayScale(BufferedImage Original, ConvertMode Mode) {
		int Width = Original.getWidth();
		int Height = Original.getHeight();
		BufferedImage GrayImage = new BufferedImage(Width, Height, BufferedImage.TYPE_BYTE_GRAY);

		//色を抜く
		for (int Y = 0; Y < Height; Y++) {
			for (int X = 0; X < Width; X++) {
				Color C = new Color(Original.getRGB(X, Y));
				int R = C.getRed();
				int G = C.getGreen();
				int B = C.getBlue();

				int Gray = 0;

				switch (Mode) {
					case NTSC_601:
						Gray = (int)(0.299 * R + 0.587 * G + 0.114 * B);
						break;
					case BT_709:
						Gray = (int)(0.2126 * R + 0.7152 * G + 0.0722 * B);
						break;
					case BT_2020:
						Gray = (int)(0.2627 * R + 0.6780 * G + 0.0593 * B);
						break;
					case AVERAGE:
						Gray = (R + G + B) / 3;
						break;
					case WEIGHTED_ROOT:
						Gray = (int)Math.sqrt(0.299 * R * R + 0.587 * G * G + 0.114 * B * B);
						break;
				}

				Color GrayColor = new Color(Gray, Gray, Gray);

				GrayImage.setRGB(X, Y, GrayColor.getRGB());
			}
		}

		return GrayImage;
	}
}
