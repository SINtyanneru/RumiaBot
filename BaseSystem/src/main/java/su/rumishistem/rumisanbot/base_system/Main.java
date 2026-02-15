package su.rumishistem.rumisanbot.base_system;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.Base64;

public class Main {
	public static void main(String[] args) throws IOException{
		System.out.println(">るみさんBOT BaseSystem");

		System.out.println("\\SYSTEM_START");

		System.out.println("/MISSKEY NOTE "+Base64.getEncoder().encodeToString("接続しました".getBytes())+" null null HOME false <0>");
		System.out.println("/DISCORD STATUS ONLINE <0>");
		System.out.println("/DISCORD ACTIVITY WATCHING "+Base64.getEncoder().encodeToString("貴様".getBytes())+" <0>");

		BufferedReader br = new BufferedReader(new InputStreamReader(System.in));
		String line;
		while ((line = br.readLine()) != null) {
			//イベント
			if (line.startsWith("@")) {
				if (line.equals("@SYSTEM_EXIT")) {
					System.out.println(">終了しています...");
					System.out.println("\\SHUTDOWN");
					System.exit(0);
				}

				//System.out.println(">イベント受信：" + line.substring(1));
			}
		}
	}
}
