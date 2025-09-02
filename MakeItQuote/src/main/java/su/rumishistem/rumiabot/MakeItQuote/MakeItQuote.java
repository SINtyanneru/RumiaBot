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
		Font TextFont = LoadFont(Font.PLAIN, 70);
		Font UserNameFont = LoadFont(Font.ITALIC, 40);
		Font UserIDFont = LoadFont(Font.PLAIN, 20);

		// フォント設定
		G.setFont(TextFont);
		G.setColor(Color.WHITE);

		// フォントメトリクス
		FontMetrics FM = G.getFontMetrics();
		int TextMaxWidth = 1000;

		// テキストを行ごとに分割
		List<String> LineList = new ArrayList<>();
		StringBuilder Line = new StringBuilder();
		for (int i = 0; i < Text.length(); i++) {
			char C = Text.charAt(i);
			Line.append(C);
			if (FM.stringWidth(Line.toString()) > TextMaxWidth || C == '\n') {
				// 最後の1文字は次行へ
				if (C != '\n') Line.deleteCharAt(Line.length() - 1);
				LineList.add(Line.toString());
				Line = new StringBuilder();
				if (C != '\n') Line.append(C);
			}
		}
		if (!Line.isEmpty()) LineList.add(Line.toString());

		// 全体の高さ計算
		int TotalHeight = FM.getHeight() * LineList.size();

		// テキストの描画開始位置
		int CX = ImageWidth / 2 + 400;
		int StartY = (ImageHeight - TotalHeight) / 2 + FM.getAscent();

		for (int i = 0; i < LineList.size(); i++) {
			String L = LineList.get(i);
			int LineWidth = FM.stringWidth(L);
			int X = CX - (LineWidth / 2);
			int Y = StartY + i * FM.getHeight();
			G.drawString(L, X, Y);
		}

		// ユーザー名描画
		G.setFont(UserNameFont);
		int UserNameLineHeight = G.getFontMetrics().getHeight();
		int UserInfoBaseY = StartY + TotalHeight;
		G.drawString("-" + UserName, CX - G.getFontMetrics().stringWidth("-" + UserName) / 2, UserInfoBaseY + UserNameLineHeight);

		// ユーザーID描画
		G.setColor(Color.GRAY);
		G.setFont(UserIDFont);
		int UserIDLineHeight = G.getFontMetrics().getHeight();
		G.drawString("@" + UserID, CX - G.getFontMetrics().stringWidth("@" + UserID) / 2, UserInfoBaseY + UserNameLineHeight + UserIDLineHeight);
	}

}
