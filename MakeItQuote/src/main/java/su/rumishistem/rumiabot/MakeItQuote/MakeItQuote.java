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
	private static final int ImageWidth = 1980;
	private static final int ImageHeight = 1040;
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
		Font UserNameFont = LoadFont(Font.ITALIC, 30);
		Font UserIDFont = LoadFont(Font.PLAIN, 20);

		//フォント設定
		G.setFont(TextFont);
		G.setColor(Color.WHITE);

		//フォントメトリクス
		FontMetrics FM = G.getFontMetrics();

		int TextMaxWidth = 500;

		//まずは文字を整理します
		List<String> LineList = new ArrayList<String>();
		StringBuilder Line = new StringBuilder();
		for (int I = 0; I < Text.length(); I++) {
			char C = Text.charAt(I);
			Line.append(C);

			//横幅が最大サイズを超えた
			if (FM.stringWidth(Line.toString()) > TextMaxWidth || C == '\n') {
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

		int TotalHeight = FM.getHeight() * LineList.size();
		BufferedImage TextImage = new BufferedImage(TextMaxWidth, TotalHeight, BufferedImage.TYPE_4BYTE_ABGR);
		Graphics2D TG = TextImage.createGraphics();
		//フォント設定
		TG.setFont(TextFont);
		TG.setColor(Color.WHITE);

		//テキストの描画関係の情報を纏める
		int StartY = FM.getAscent();
		int LineHeight = FM.getHeight();
		int LastY = 0;
		for (int I = 0; I < LineList.size(); I++) {
			String L = LineList.get(I);
			int LineWidth = FM.stringWidth(L);
			int X = (TextMaxWidth / 2) - (LineWidth / 2);
			int Y = StartY + I * LineHeight;
			TG.drawString(L, X, Y);
			LastY = Y;
		}

		int CX = ImageWidth / 2 + 400;
		int TextDrawMaxWidth = ImageWidth - CX;				//最大横幅
		int TextDrawMaxHeight = ImageHeight - 300;			//最大縦幅
		int TextDrawOriginalWidth = TextImage.getWidth();	//元画像の横幅
		int TextDrawOriginalHeight = TextImage.getHeight();	//元画像の縦幅
		int TextDrawWidth = TextDrawOriginalWidth;
		int TextDrawHeight = TextDrawOriginalHeight;

		double WidthRaito = (double) TextDrawMaxWidth / TextDrawOriginalWidth;
		double HeightRaito = (double) TextDrawMaxHeight / TextDrawOriginalHeight;
		double Scale = Math.min(WidthRaito, HeightRaito);

		if (!(Scale >= 1.0)) {
			TextDrawWidth = (int)(TextDrawOriginalWidth * Scale);
			TextDrawHeight = (int)(TextDrawOriginalHeight * Scale);
		}

		int TextDrawX = CX - (TextDrawWidth / 2);
		int TextDrawY = (ImageHeight / 2) - (TextDrawHeight / 2);

		G.drawImage(
			TextImage,
			TextDrawX,
			TextDrawY,
			TextDrawWidth,
			TextDrawHeight,
			null
		);

		int UserInfoBaseY = TextDrawY + TextDrawHeight;

		G.setFont(UserNameFont);
		int UserNameLineHeight = G.getFontMetrics().getHeight();
		G.drawString("-"+UserName, CX - G.getFontMetrics().stringWidth("-"+UserName) / 2, UserInfoBaseY + UserNameLineHeight);

		G.setColor(Color.GRAY);
		G.setFont(UserIDFont);
		int UserIDLineHeight = G.getFontMetrics().getHeight();
		G.drawString("@"+UserID, CX - G.getFontMetrics().stringWidth("@"+UserID) / 2, UserInfoBaseY + UserNameLineHeight + UserIDLineHeight);
	}
}
