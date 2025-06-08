package su.rumishistem.rumiabot.MakeItQuote;

import java.awt.Color;
import java.awt.Font;
import java.awt.FontFormatException;
import java.awt.FontMetrics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.net.MalformedURLException;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import javax.imageio.ImageIO;

public class MakeItQuote {
	private static final int ImageWidth = 1200;
	private static final int ImageHeight = 630;
	protected static BufferedImage BackgroundImage = null;

	private Graphics2D G = null;

	private String UserID = null;
	private String UserName = null;
	private BufferedImage IconImage = null;
	private String Text = null;

	public void setUserID(String UserID) {
		this.UserID = UserID;
	}

	public void setUserName(String UserName) {
		this.UserName = UserName;
	}

	public void setIcon(BufferedImage IconImage) {
		this.IconImage = IconImage;
	}

	public void setText(String Text) {
		this.Text = Text;
	}

	public File Gen() throws MalformedURLException, IOException, FontFormatException {
		BufferedImage Image = new BufferedImage(ImageWidth, ImageHeight, BufferedImage.TYPE_INT_ARGB);
		G = Image.createGraphics();

		//アンチエイリアス有効化
		G.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
		G.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);

		//背景が透明なので黒く塗る
		G.setColor(Color.BLACK);
		G.fillRect(0, 0, ImageWidth, ImageHeight);

		//アイコン描画
		DrawIcon();

		//上にかぶせる例のアレ
		G.drawImage(BackgroundImage, 0, 0, ImageWidth, ImageHeight, null);

		DrawText();

		G.dispose();

		File F = new File("/tmp/" + UUID.randomUUID().toString() + ".png");
		ImageIO.write(Image, "png", F);
		return F;
	}

	private void DrawIcon() throws MalformedURLException, IOException {
		//比率を保ったままリサイズして設置
		double IconScale = (double)ImageHeight / (double)IconImage.getHeight();
		int IconWidth = (int)(IconImage.getWidth() * IconScale);
		G.drawImage(IconImage, 0, 0, IconWidth, ImageHeight, null);
	}

	private static Font LoadFont(int Style, int Size) throws FontFormatException, IOException {
		Font F = Font.createFont(Font.TRUETYPE_FONT, new File("./DATA/FONT/").listFiles()[0]);
		F = F.deriveFont(Style, Size);
		return F;
	}

	private void DrawText() throws FontFormatException, IOException {
		Font TextFont = LoadFont(Font.PLAIN, 64);
		Font UserNameFont = LoadFont(Font.ITALIC, 40);
		Font UserIDFont = LoadFont(Font.PLAIN, 30);

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
