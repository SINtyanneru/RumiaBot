package su.rumishistem.rumiabot.Trash;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;

public class Podman {
	private static final String PODMAN_PATH = "/usr/bin/podman";
	private static final String IMAGE_NAME_DEBIAN = "rumiabot_trash_debian";

	public static boolean check_installed_image() throws IOException, InterruptedException {
		ProcessBuilder pb = new ProcessBuilder(PODMAN_PATH, "image", "list");
		Process p = pb.start();
		BufferedReader out = new BufferedReader(new InputStreamReader(p.getInputStream()));

		try {
			StringBuilder sb = new StringBuilder();
			String line;
			while ((line = out.readLine()) != null) {
				sb.append(line).append("\n");
			}

			p.waitFor();

			String output = sb.toString();
			if (output.contains(IMAGE_NAME_DEBIAN)) {
				return true;
			} else {
				return false;
			}
		} finally {
			out.close();
		}
	}

	public static void build_debian_image() throws IOException, InterruptedException {
		ProcessBuilder pb = new ProcessBuilder(PODMAN_PATH, "build", "-t", IMAGE_NAME_DEBIAN, ".");
		pb.directory(new File(System.getProperty("user.dir"), "Trash/Docker"));
		Process p = pb.start();

		//TODO:エラーを受け取るべきかも？

		if (p.waitFor() != 0) {
			throw new RuntimeException("イメージファイルの構築に失敗しました");
		}
	}

	public static String run(String command) throws InterruptedException, IOException {
		ProcessBuilder pb = new ProcessBuilder(
			PODMAN_PATH, "run",
			"-it", "--rm",
			"--memory=512m", "--cpus=1.0",
			IMAGE_NAME_DEBIAN,
			"bash", "-c", command
			);
		Process p = pb.start();
		BufferedReader out = new BufferedReader(new InputStreamReader(p.getInputStream()));
		PrintWriter in = new PrintWriter(p.getOutputStream(), true);

		try {
			StringBuilder sb = new StringBuilder();

			Thread read_thread = new Thread(new Runnable() {
				@Override
				public void run() {
					try {
						String line;
						while ((line = out.readLine()) != null) {
							sb.append(line).append("\n");
						}
					} catch (Exception EX) {
						EX.printStackTrace();
					}
				}
			});
			read_thread.start();

			in.println("exit");

			p.waitFor();
			read_thread.join();

			String output = sb.toString();
			output = output.replace("exit\n", "");

			return output;
		} finally {
			out.close();
			in.close();
		}
	}
}
