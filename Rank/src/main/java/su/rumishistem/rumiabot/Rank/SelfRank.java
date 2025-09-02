package su.rumishistem.rumiabot.Rank;

import java.awt.Color;
import java.awt.Font;
import java.awt.FontFormatException;
import java.awt.FontMetrics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.sql.SQLException;
import java.util.UUID;

import javax.imageio.ImageIO;

import su.rumishistem.rumi_java_lib.ArrayNode;
import su.rumishistem.rumi_java_lib.SQL;

public class SelfRank {
	private static final int width = 400;
	private static final int height = 150;

	protected static BufferedImage background_image = null;

	public static File image_gen(String user_id, String guild_id, BufferedImage user_icon, String user_name) throws IOException, FontFormatException {
		File image_file = new File("/tmp/" + UUID.randomUUID().toString() + ".png");
		BufferedImage image = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);
		Graphics2D g = image.createGraphics();
		Font font = LoadFont(Font.PLAIN, 25);

		//設定
		g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
		g.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);
		g.setFont(font);
		g.setColor(Color.WHITE);

		//アイコン
		g.drawImage(user_icon, 5, 7, 52, 52, null);

		//背景(アイコンの上から被せる)
		g.drawImage(background_image, 0, 0, width, height, null);

		//名前
		g.drawString(user_name, 80, 40);

		try {
			long[] rank = rank_get(user_id, guild_id);
			long level = rank[0];
			long exp = rank[1];

			//経験値
			g.drawString("EXP:"+exp, 80, 90);

			//レベル
			g.drawString("LV:"+level, 260, 90);

			//進捗
			final int progressbar_height = 20;
			long levelup_exp = 100 * level;
			double progress = (double)exp / levelup_exp;
			int progress_width = (int) (progress * width);

			g.setColor(Color.CYAN);
			g.fillRect(0, height - progressbar_height, progress_width, progressbar_height);

			//進捗(%)
			String progres_text = ((int)(progress * 100)) + "%";
			FontMetrics fm = g.getFontMetrics();
			int text_width = fm.stringWidth(progres_text);
			int text_height = 15;
			Font progress_font = LoadFont(Font.PLAIN, text_height);

			g.setFont(progress_font);
			g.setColor(Color.WHITE);
			g.drawString(progres_text, (width - text_width) / 2, height - progressbar_height + (text_height / 2));
		} catch (SQLException EX) {
			
		} catch (RuntimeException EX) {
			
		}

		ImageIO.write(image, "png", image_file);
		return image_file;
	}

	private static Font LoadFont(int style, int size) throws FontFormatException, IOException {
		Font font = Font.createFont(Font.TRUETYPE_FONT, new File("./DATA/FONT/").listFiles()[0]);
		font = font.deriveFont(style, size);
		return font;
	}

	private static long[] rank_get(String user_id, String guild_id) throws SQLException {
		ArrayNode result = SQL.RUN("SELECT `EXP`, `LEVEL` FROM `DISCORD_RANK` WHERE `UID` = ? AND `GUILD` = ?;", new Object[] {
			user_id, guild_id
		});

		if (result.length() == 0) {
			throw new RuntimeException("データなし");
		}

		long exp = result.get(0).getData("EXP").asLong();
		long level = result.get(0).getData("LEVEL").asLong();

		return new long[] {level, exp};
	}
}
