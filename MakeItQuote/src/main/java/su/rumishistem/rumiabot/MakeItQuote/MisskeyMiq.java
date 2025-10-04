package su.rumishistem.rumiabot.MakeItQuote;

import java.awt.image.BufferedImage;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.UUID;

import javax.imageio.ImageIO;

import su.rumishistem.rumi_java_lib.FETCH;
import su.rumishistem.rumi_java_lib.Misskey.TYPE.Note;
import su.rumishistem.rumiabot.System.TYPE.CommandInteraction;

public class MisskeyMiq {
	public static void Run(CommandInteraction CI) throws Exception {
		if (CI.GetMisskeyNote().getReply() == null) {
			CI.Reply("リプライ先がありません死ね");
			return;
		}
		Note Target = CI.GetMisskeyNote().getReply();
		String IconURL = Target.getUSER().getICON_URL();
		String IconPath = "/tmp/" + UUID.randomUUID().toString();
		BufferedImage IconImage = null;

		if (!CheckIconSize(IconURL)) {
			CI.Reply("アイコンサイズオーバー");
			return;
		}

		//アイコンを落とす
		FETCH WebpAjax = new FETCH(IconURL);
		File WebpFile = new File(IconPath);
		FileOutputStream FOS = new FileOutputStream(WebpFile);
		FOS.write(WebpAjax.GET().getRaw());
		FOS.flush();
		FOS.close();

		String FileResult = CheckFile(IconPath);

		//Misskeyはだいたいアイコンがwebpなのでチェックする
		if (!(FileResult.contains("image/png;") || FileResult.contains("image/jpeg;"))) {
			String OutPath = "/tmp/" + UUID.randomUUID().toString() + ".png";

			//FFMPEG実行
			ProcessBuilder PB = new ProcessBuilder("/usr/bin/ffmpeg", "-i", IconPath, OutPath);
			Process P = PB.start();

			//FFMPEGの応答を読む
			BufferedReader FFMPEG_BR = new BufferedReader(new InputStreamReader(P.getInputStream()));
			StringBuilder FFMPEB_SB = new StringBuilder();
			String Line;
			while ((Line = FFMPEG_BR.readLine()) != null) {
				FFMPEB_SB.append(Line).append("\n");
			}

			//変換できた？
			int ExitCode = P.waitFor();
			if (ExitCode == 0) {
				IconImage = ImageIO.read(new File(OutPath));
				Files.delete(Path.of(IconPath));
				Files.delete(Path.of(OutPath));
			} else {
				//失敗
				CI.Reply("アイコンをWebpからPNGに変換しようとしたら出来ませんでした。\n\nFFMPEGの応答↓\n```\n" + FFMPEB_SB.toString() + "\n```");
				return;
			}
		} else {
			IconImage = ImageIO.read(new File(IconPath));
			Files.delete(Path.of(IconPath));
		}

		//生成
		MakeItQuote miq = new MakeItQuote();
		miq.setUserID(Target.getUSER().getUID() + "@" + Target.getUSER().getHost());
		if (!Target.getUSER().getNAME().equals("null")) {
			miq.setUserName(Target.getUSER().getNAME());
		} else {
			miq.setUserName(Target.getUSER().getUID());
		}
		miq.setIcon(IconImage);
		if (!Target.getTEXT().equals("null")) {
			miq.setText(Target.getTEXT());
		} else {
			miq.setText(Target.getCW());
		}

		//返す
		File F = miq.Gen();
		CI.AddFile(F);
		CI.Reply("ほい");
		F.delete();
	}

	private static boolean CheckIconSize(String IconURL) throws IOException {
		URL U = new URL(IconURL);
		HttpURLConnection HUC = (HttpURLConnection)U.openConnection();
		HUC.setRequestMethod("HEAD");
		HUC.connect();

		int MaxSizeByte = 5 * 1024 * 1024; //5MB
		int ResCode = HUC.getResponseCode();
		if (ResCode == HttpURLConnection.HTTP_OK) {
			int ContentLength = HUC.getContentLength();
			return ContentLength >= 0 && ContentLength <= MaxSizeByte;
		} else {
			return false;
		}
	}

	private static String CheckFile(String FilePath) throws IOException, InterruptedException {
		ProcessBuilder PB = new ProcessBuilder("/usr/bin/file", "--mime", FilePath);
		Process P = PB.start();

		BufferedReader BR = new BufferedReader(new InputStreamReader(P.getInputStream()));
		StringBuilder SB = new StringBuilder();
		String Line;
		while ((Line = BR.readLine()) != null) {
			SB.append(Line).append("\n");
		}

		P.waitFor();
		return SB.toString();
	}
}
