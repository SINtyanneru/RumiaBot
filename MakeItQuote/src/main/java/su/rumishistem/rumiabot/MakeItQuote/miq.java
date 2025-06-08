package su.rumishistem.rumiabot.MakeItQuote;

import java.awt.Color;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import javax.imageio.ImageIO;

public class miq {
	private static final int ImageWidth = 1200;
	private static final int ImageHeight = 630;
	protected static BufferedImage BackgroundImage = null;

	public static File Gen(String UserID, String UserName, String IconURL, String Text) throws MalformedURLException, IOException {
		BufferedImage Image = new BufferedImage(ImageWidth, ImageHeight, BufferedImage.TYPE_INT_ARGB);
		Graphics2D G = Image.createGraphics();

		//アンチエイリアス有効化
		G.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
		G.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);

		//背景が透明なので黒く塗る
		G.setColor(Color.BLACK);
		G.fillRect(0, 0, ImageWidth, ImageHeight);

		//アイコン描画
		DrawIcon(IconURL, G);

		//上にかぶせる例のアレ
		G.drawImage(BackgroundImage, 0, 0, ImageWidth, ImageHeight, null);

		DrawText(UserID, UserName, Text, G);

		G.dispose();

		File F = new File("/tmp/" + UUID.randomUUID().toString() + ".png");
		ImageIO.write(Image, "png", F);
		return F;
	}

	private static void DrawIcon(String IconURL, Graphics2D G) throws MalformedURLException, IOException {
		BufferedImage IconImage = ImageIO.read(new URL(IconURL));
		double IconScale = (double)ImageHeight / (double)IconImage.getHeight();
		int IconWidth = (int)(IconImage.getWidth() * IconScale);
		G.drawImage(IconImage, 0, 0, IconWidth, ImageHeight, null);
	}

	private static void DrawText(String UserID, String UserName, String Text, Graphics2D G) {
		Font TextFont = new Font("Serif", Font.BOLD, 64);
		Font UserNameFont = new Font("Serif", Font.ITALIC, 40);
		Font UserIDFont = new Font("Serif", Font.BOLD, 30);

		//フォント設定
		G.setFont(TextFont);

		G.setColor(Color.WHITE);

		//フォントメトリクス
		FontMetrics FM = G.getFontMetrics();
		int CX = 850;

		//まずは文字を整理します
		List<String> LineList = new ArrayList<String>();
		StringBuilder Line = new StringBuilder();
		for (int I = 0; I < Text.length(); I++) {
			char C = Text.charAt(I);
			Line.append(C);

			//横幅が最大サイズを超えた
			if ((CX + FM.stringWidth(Line.toString())) - 300 > ImageWidth) {
				//最後の1文字は次行へ
				Line.deleteCharAt(Line.length() - 1);
				//行に追加
				LineList.add(Line.toString());
				//初期化
				Line = new StringBuilder();
				//次行の先頭に
				Line.append(C);
			}
		}

		//まだ行に文字が残ってるなら追加
		if (!Line.isEmpty()) {
			LineList.add(Line.toString());
		}

		//描画する
		int LineHeight = FM.getHeight();
		int TotalHeight = LineHeight * LineList.size();
		int StartY = (ImageHeight / 2) - TotalHeight / 2 + FM.getAscent();
		int LastY = 0;

		for (int I = 0; I < LineList.size(); I++) {
			String L = LineList.get(I);
			int LineWidth = FM.stringWidth(L);
			int X = CX - LineWidth / 2;
			int Y = StartY + I * LineHeight;
			G.drawString(L, X, Y);
			LastY = Y;
		}


		G.setFont(UserNameFont);
		int UserNameLineHeight = G.getFontMetrics().getHeight();
		G.drawString("-"+UserName, CX - G.getFontMetrics().stringWidth("-"+UserName) / 2, LastY + UserNameLineHeight);

		G.setColor(Color.GRAY);
		G.setFont(UserIDFont);
		int UserIDLineHeight = G.getFontMetrics().getHeight();
		G.drawString("@"+UserID, CX - G.getFontMetrics().stringWidth("@"+UserID) / 2, LastY + UserNameLineHeight + UserIDLineHeight);
	}
}
