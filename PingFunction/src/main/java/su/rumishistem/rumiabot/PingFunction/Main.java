package su.rumishistem.rumiabot.PingFunction;

import static su.rumishistem.rumiabot.System.FunctionModuleLoader.AddCommand;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.util.regex.Pattern;

import su.rumishistem.rumiabot.System.TYPE.CommandData;
import su.rumishistem.rumiabot.System.TYPE.CommandInteraction;
import su.rumishistem.rumiabot.System.TYPE.CommandOption;
import su.rumishistem.rumiabot.System.TYPE.CommandOptionType;
import su.rumishistem.rumiabot.System.TYPE.FunctionClass;
import su.rumishistem.rumiabot.System.TYPE.ReceiveMessageEvent;

public class Main implements FunctionClass {
	private static final String FUNCTION_NAME = "ping";
	private static final String FUNCTION_VERSION = "1.0";
	private static final String FUNCTION_AUTOR = "Rumisan";

	private static final int PING_LIMIT = 4;

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
		AddCommand(new CommandData("ping", new CommandOption[] {
			new CommandOption("host", CommandOptionType.String, null, true)
		}, false));
	}

	@Override
	public void ReceiveMessage(ReceiveMessageEvent e) {
	}

	@Override
	public boolean GetAllowCommand(String Name) {
		return Name.equals("ping");
	}

	@Override
	public void RunCommand(CommandInteraction CI) throws Exception {
		String Host = CI.GetCommand().GetOption("host").GetValueAsString();
		if (HostCheck(Host)) {
			//プロセス生成
			ProcessBuilder PB = new ProcessBuilder("ping", "-c", String.valueOf(PING_LIMIT), Host);
			Process P = PB.start();

			//応答を受信
			StringBuilder PingTextB = new StringBuilder();
			BufferedReader reader = new BufferedReader(new InputStreamReader(P.getInputStream()));
			String Line;
			while ((Line = reader.readLine()) != null) {
				PingTextB.append(Line + "\n");
			}
			String[] PingText = PingTextB.toString().split("\n");
			int ExitCode = P.waitFor();

			//応答を解析する
			StringBuilder RESULTText = new StringBuilder();
			RESULTText.append(PingText[0].split(" ")[1] + PingText[0].split(" ")[2] + "にデータを送信します\n");

			if (ExitCode == 0) {
				for (int I = 1; I <= PING_LIMIT; I++) {
					String[] PingStatusText = PingText[I].split(" ");
					RESULTText.append("`[" + PingStatusText[4].split("=")[1] + "]`");
					RESULTText.append(PingStatusText[0]);
					RESULTText.append("バイトを");
					RESULTText.append(PingStatusText[3]);
					RESULTText.append("に送信しました。");
					RESULTText.append(" ");
					RESULTText.append("TTL:");
					RESULTText.append(PingStatusText[5].split("=")[1]);
					RESULTText.append("時間:");
					RESULTText.append(PingStatusText[6].split("=")[1] + "ms");
					RESULTText.append("\n");
				}
				
				//結果
				String[] PingResult = PingText[PING_LIMIT + 3].split(" ");

				RESULTText.append("\n");
				RESULTText.append("結果:");
				RESULTText.append(PingResult[0] + "つパケット送信し、");
				RESULTText.append(PingResult[3] + "つパケット受信し、");
				RESULTText.append(PingResult[3] + "の損失がありました、");
				RESULTText.append("合計時間は" + PingResult[9] + "です。\n");
			} else {
				RESULTText.append("送信できませんでした\n");
			}

			RESULTText.append("デバッグ用\n```\n" + PingTextB.toString() + "\n```");

			//全部吐く
			CI.Reply(RESULTText.toString());
		} else {
			CI.Reply("不正を検知、お前を殺します");
		}
	}

	public boolean HostCheck(String Host) {
		String ipv4Pattern = "^([0-9]{1,3}\\.){3}[0-9]{1,3}$";
		String ipv6Pattern = "^[0-9a-fA-F:]+$";
		String fqdnPattern = "^[a-zA-Z0-9.-]+$";
		
		return Pattern.matches(ipv4Pattern, Host) ||
				Pattern.matches(ipv6Pattern, Host) ||
				Pattern.matches(fqdnPattern, Host);
	}
}
