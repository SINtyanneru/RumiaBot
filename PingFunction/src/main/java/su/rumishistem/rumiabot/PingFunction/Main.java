package su.rumishistem.rumiabot.PingFunction;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import su.rumishistem.rumiabot.System.CommandRegister;
import su.rumishistem.rumiabot.System.Type.CommandInteraction;
import su.rumishistem.rumiabot.System.Type.CommandOptionRegist;
import su.rumishistem.rumiabot.System.Type.FunctionClass;
import su.rumishistem.rumiabot.System.Type.OptionType;
import su.rumishistem.rumiabot.System.Type.RunCommand;

public class Main implements FunctionClass {
	private static final int PING_LIMIT = 4;

	@Override
	public String function_name() {
		return "ping";
	}
	@Override
	public String function_version() {
		return "1.0";
	}
	@Override
	public String function_author() {
		return "るみ";
	}

	@Override
	public void init() {
		CommandRegister.add_command("ping", new CommandOptionRegist[] {
			new CommandOptionRegist("host", OptionType.String, true)
		}, false, new RunCommand() {
			@Override
			public void run(CommandInteraction e) throws Exception {
				String host = e.get_option_as_string("host");
				if (HostCheck(host)) {
					//プロセス生成
					ProcessBuilder PB = new ProcessBuilder("ping", "-c", String.valueOf(PING_LIMIT), host);
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

					if (ExitCode == 0) {
						//タイトル
						Matcher MatchTitle = Pattern.compile(" (.*) \\((\\d{1,3}\\.\\d{1,3}\\.\\d{1,3}\\.\\d{1,3})\\) (.*)\\(").matcher(PingText[0]);
						MatchTitle.find();
						RESULTText.append(MatchTitle.group(1) + "(" + MatchTitle.group(2) + ")" + "にデータを送信します\n");

						for (int I = 1; I <= PING_LIMIT; I++) {
							Matcher MatchStatus = Pattern.compile("(\\d{1,4}) bytes from (.*): icmp_seq=(\\d{1,4}) ttl=(\\d{1,4}) time=(.*) ms").matcher(PingText[I]);
							MatchStatus.find();
							RESULTText.append("`[" + MatchStatus.group(3) + "]`");
							RESULTText.append(MatchStatus.group(1));
							RESULTText.append("バイトを");
							RESULTText.append(MatchStatus.group(2));
							RESULTText.append("に送信しました。");
							RESULTText.append(" ");
							RESULTText.append("TTL:");
							RESULTText.append(MatchStatus.group(4));
							RESULTText.append(" ");
							RESULTText.append("時間:");
							RESULTText.append(MatchStatus.group(5) + "ms");
							RESULTText.append("\n");
						}
						
						//結果
						Matcher MatchResult = Pattern.compile("(\\d{1,9}) .*, (\\d{1,9}) .*, (\\d{1,3})% .*, time (.*)ms").matcher(PingText[PING_LIMIT + 3]);
						MatchResult.find();

						RESULTText.append("\n");
						RESULTText.append("結果:");
						RESULTText.append(MatchResult.group(1) + "つパケット送信し、");
						RESULTText.append(MatchResult.group(2) + "つパケット受信し、");
						RESULTText.append(MatchResult.group(3) + "の損失がありました、");
						RESULTText.append("合計時間は" + MatchResult.group(4) + "msです。\n");
					} else {
						RESULTText.append("送信できませんでした\n");
						RESULTText.append("デバッグ用\n```\n" + PingTextB.toString() + "\n```");
					}

					//全部吐く
					e.reply(RESULTText.toString());
				} else {
					e.reply("不正を検知、お前を殺します");
				}
			}
		});
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
