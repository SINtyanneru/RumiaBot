package su.rumishistem.rumiabot.aichan;

import java.io.BufferedReader;
import java.io.File;
import java.io.InputStreamReader;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

import su.rumishistem.rumiabot.System.TYPE.CommandInteraction;
import su.rumishistem.rumiabot.System.TYPE.FunctionClass;
import su.rumishistem.rumiabot.System.TYPE.ReceiveMessageEvent;

public class Main implements FunctionClass {
	private static final String FUNCTION_NAME = "藍ちゃんを乗っとるやつ";
	private static final String FUNCTION_VERSION = "1.0";
	private static final String FUNCTION_AUTOR = "Rumisan";

	@Override
	public String FUNCTION_NAME() {
		return FUNCTION_NAME;
	}
	@Override
	public String FUNCTION_VERSION() {
		return FUNCTION_VERSION;
	}
	@Override
	public String FUNCTION_AUTOR() {
		return FUNCTION_AUTOR;
	}

	@Override
	public void Init() {
		try {
			String AIDir = Paths.get("").toAbsolutePath().toString() + "/ai/";
			if (Files.exists(Path.of(AIDir))) {
				ProcessBuilder PB = new ProcessBuilder("/home/rumisan/.nvm/versions/node/v20.15.0/bin/npm", "run", "start");
				PB.directory(new File(AIDir));
				PB.redirectErrorStream(true);

				System.out.println("藍を起動しています");

				//起動
				Process P = PB.start();

				//標準出力
				new Thread(new Runnable() {
					@Override
					public void run() {
						try {
							BufferedReader BR = new BufferedReader(new InputStreamReader(P.getInputStream()));
							String Line;
							while ((Line = BR.readLine()) != null) {
								System.out.println("[  藍  ]" + Line);
							}
						} catch (Exception EX) {
							EX.printStackTrace();
						}
					}
				}).start();
			} else {
				System.out.println("藍が見つからなかったので特に何もしません");
			}
		} catch (Exception EX) {
			EX.printStackTrace();
			System.exit(1);
		}
	}
	@Override
	public void ReceiveMessage(ReceiveMessageEvent e) {
	}
	@Override
	public boolean GetAllowCommand(String Name) {
		return false;
	}
	@Override
	public void RunCommand(CommandInteraction CI) throws Exception {
	}
}
