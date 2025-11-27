package su.rumishistem.rumiabot.MakeItQuote;

import java.awt.image.BufferedImage;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.UUID;

import javax.imageio.ImageIO;

import su.rumishistem.rumi_java_lib.FETCH;
import su.rumishistem.rumi_java_lib.MisskeyBot.Type.Note;
import su.rumishistem.rumi_java_lib.MisskeyBot.Type.User;
import su.rumishistem.rumiabot.System.Type.CommandInteraction;

public class MisskeyMiq {
	public static void Run(CommandInteraction CI) throws Exception {
		if (CI.get_misskey_event().get_reply() == null) {
			CI.reply("リプライ先がありません死ね");
			return;
		}

		Note Target = CI.get_misskey_event().get_reply();
		User target_user = Target.get_user();
		String IconURL = target_user.get_icon_url();
		String IconPath = "/tmp/" + UUID.randomUUID().toString();
		BufferedImage IconImage = null;

		if (!CheckIconSize(IconURL)) {
			CI.reply("アイコンサイズオーバー");
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
				CI.reply("アイコンをWebpからPNGに変換しようとしたら出来ませんでした。\n\nFFMPEGの応答↓\n```\n" + FFMPEB_SB.toString() + "\n```");
				return;
			}
		} else {
			IconImage = ImageIO.read(new File(IconPath));
			Files.delete(Path.of(IconPath));
		}

		//TODO:これは応急処理
		String host = "eth.rumiserver.com";
		try {
			host = target_user.get_host();
		} catch (NullPointerException ex) {
			//こんなエラーが出る
			/*
			 	java.lang.NullPointerException: Cannot invoke "su.rumishistem.rumi_java_lib.MisskeyBot.MisskeyClient.get_host()" because "this.client" is null
				at su.rumishistem.rumi_java_lib.MisskeyBot.Type.User.get_host(User.java:61)
				at su.rumishistem.rumiabot.MakeItQuote.MisskeyMiq.Run(MisskeyMiq.java:85)
				at su.rumishistem.rumiabot.MakeItQuote.Main$1.run(Main.java:46)
				at su.rumishistem.rumiabot.System.Misskey.MisskeyBot$1$1.run(MisskeyBot.java:167)
				at java.base/java.util.concurrent.Executors$RunnableAdapter.call(Executors.java:572)
				at java.base/java.util.concurrent.FutureTask.run(FutureTask.java:317)
				at java.base/java.util.concurrent.ThreadPoolExecutor.runWorker(ThreadPoolExecutor.java:1144)
				at java.base/java.util.concurrent.ThreadPoolExecutor$Worker.run(ThreadPoolExecutor.java:642)
				at java.base/java.lang.Thread.run(Thread.java:1583)
			*/
		}

		//生成
		MakeItQuote miq = new MakeItQuote();
		miq.setUserID(target_user.get_username() + "@" + host);
		miq.setUserName(target_user.get_name());
		miq.setIcon(IconImage);
		if (Target.get_text() != null) {
			miq.setText(Target.get_text());
		} else {
			miq.setText(Target.get_cw());
		}

		//返す
		File F = miq.Gen();
		CI.add_file(F);
		CI.reply("ほい");
		F.delete();
	}

	private static boolean CheckIconSize(String IconURL) throws IOException {
		URL url = new URL(IconURL);
		HttpURLConnection huc = (HttpURLConnection)url.openConnection();
		huc.setRequestMethod("GET");
		huc.connect();

		int max_size_byte = 10 * 1024 * 1024; //10MB
		int code = huc.getResponseCode();

		if (code != 200) {
			return false;
		}

		InputStream is = huc.getInputStream();
		byte[] buffer = new byte[8192];
		int total = 0;
		int read_length;
		while ((read_length = is.read(buffer)) != -1) {
			total += read_length;
			if (total > max_size_byte) {
				return false;
			}
		}

		return true;
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
