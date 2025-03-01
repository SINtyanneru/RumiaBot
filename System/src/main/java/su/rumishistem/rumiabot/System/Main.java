package su.rumishistem.rumiabot.System;

import static su.rumishistem.rumi_java_lib.LOG_PRINT.Main.LOG;
import static su.rumishistem.rumiabot.System.Main.CommandList;
import static su.rumishistem.rumiabot.System.Main.DISCORD_BOT;

import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.PrintStream;
import java.io.StringReader;
import java.util.ArrayList;
import java.util.List;

import su.rumishistem.rumi_java_lib.ArrayNode;
import su.rumishistem.rumi_java_lib.CONFIG;
import su.rumishistem.rumi_java_lib.EXCEPTION_READER;
import su.rumishistem.rumi_java_lib.SQL;
import su.rumishistem.rumi_java_lib.HTTP_SERVER.HTTP_SERVER;
import su.rumishistem.rumi_java_lib.LOG_PRINT.LOG_TYPE;
import su.rumishistem.rumi_java_lib.Misskey.MisskeyClient;
import su.rumishistem.rumi_java_lib.SmartHTTP.SmartHTTP;
import su.rumishistem.rumiabot.System.Discord.DiscordBOT;
import su.rumishistem.rumiabot.System.HTTP.HTTP;
import su.rumishistem.rumiabot.System.TYPE.CommandData;
import su.rumishistem.rumiabot.System.TYPE.CommandOption;
import su.rumishistem.rumiabot.System.TYPE.DiscordFunction;
import su.rumishistem.rumiabot.System.TYPE.FunctionClass;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.interactions.commands.OptionType;
import net.dv8tion.jda.api.interactions.commands.build.Commands;
import net.dv8tion.jda.api.interactions.commands.build.OptionData;
import net.dv8tion.jda.api.interactions.commands.build.SlashCommandData;

public class Main {
	public static ArrayNode CONFIG_DATA = null;
	public static JDA DISCORD_BOT = null;
	public static MisskeyClient MisskeyBOT = null;
	public static FunctionModuleLoader FML = null;
	public static List<FunctionClass> FunctionModuleList = new ArrayList<FunctionClass>();
	public static List<CommandData> CommandList = new ArrayList<CommandData>();
	public static SmartHTTP SH = null;
	public static final int MaxLineSize = 25;

	public static void main(String[] args) {
		try {
			//デフォの標準出力を保存
			List<String> OutBuffer = new ArrayList<String>();
			PrintStream SysOut = System.out;

			//標準出力を傍受
			ByteArrayOutputStream OutBAOS = new ByteArrayOutputStream();
			PrintStream OutPS = new PrintStream(OutBAOS) {
				@Override
				public void print(String Line) {
					/*
					if (OutBuffer.size() >= MaxLineSize) {
						OutBuffer.remove(0);
					}*/

					OutBuffer.add(Line);
					SysOut.println(Line);
				}
			};
			System.setOut(OutPS);

			//標準エラー出力を傍受
			ByteArrayOutputStream ErrBAOS = new ByteArrayOutputStream();
			PrintStream ErrPS = new PrintStream(ErrBAOS) {
				@Override
				public void print(String Line) {
					/*
					if (OutBuffer.size() >= MaxLineSize) {
						OutBuffer.remove(0);
					}*/

					OutBuffer.add("\u001b[41m" + Line + "\u001b[0m");
					SysOut.println(Line);
				}
			};
			System.setErr(ErrPS);

			//TUI的なアレを描画するための関数
			new Thread(new Runnable() {
				@Override
				public void run() {
					int LastLineIndex = 0;

					while (true) {
						if (OutBuffer.size() != LastLineIndex) {
							/*
							SysOut.print("\u001b[2J");

							//枠
							SysOut.println("    ____                  _       ____  ____  ______    ");
							SysOut.println("   / __ \\__  ______ ___  (_)___ _/ __ )/ __ \\/_  __/  ");
							SysOut.println("  / /_/ / / / / __ `__ \\/ / __ `/ __  / / / / / /      ");
							SysOut.println(" / _, _/ /_/ / / / / / / / /_/ / /_/ / /_/ / / /        ");
							SysOut.println("/_/ |_|\\__,_/_/ /_/ /_/_/\\__,_/_____/\\____/ /_/      ");
							SysOut.println("V1.1");
							SysOut.println("--------------------------------------------------------");

							//出力
							for (int I = 0; I < MaxLineSize; I++) {
								if (OutBuffer.size() >= MaxLineSize) {
									String Line = OutBuffer.get(I);
									SysOut.println(Line);
								} else {
									SysOut.println("");
								}
							}

							SysOut.println("--------------------------------------------------------");
							SysOut.print(">");
							*/
							LastLineIndex = OutBuffer.size();
						}
					}
				}
			}).start();

			LOG(LOG_TYPE.PROCESS, "Config load...");
			if (new File("./Config.ini").exists()) {
				CONFIG_DATA = new CONFIG().DATA;
				LOG(LOG_TYPE.PROCESS_END_OK, "");
			} else {
				LOG(LOG_TYPE.PROCESS_END_FAILED, "Config.ini GA NAI!!!!!!!");
				System.exit(1);
			}

			//SQL用意
			SQL.CONNECT(
				CONFIG_DATA.get("SQL").getData("HOST").asString(),
				CONFIG_DATA.get("SQL").getData("PORT").asString(),
				CONFIG_DATA.get("SQL").getData("DB").asString(),
				CONFIG_DATA.get("SQL").getData("USER").asString(),
				CONFIG_DATA.get("SQL").getData("PASS").asString()
			);

			//HTTPサーバー起動
			new Thread(new Runnable() {
				@Override
				public void run() {
					try {
						HTTP.Init();
					} catch (Exception EX) {
						EX.printStackTrace();
						LOG(LOG_TYPE.FAILED, "HTTPサーバー起動失敗");
						System.exit(1);
					}
				}
			}).start();

			//DiscordBOTを起動
			DiscordBOT.Init();

			//MisskeyBOTを起動
			su.rumishistem.rumiabot.System.Misskey.MisskeyBOTMain.Init();

			//モジュールをロード
			FML = new FunctionModuleLoader();
			FML.Load();

			//スラッシュコマンド集計
			List<SlashCommandData> SlashCommandList = new ArrayList<SlashCommandData>();
			for (CommandData Command:CommandList) {
				//オプション
				List<OptionData> OptionList = new ArrayList<OptionData>();
				for (CommandOption Option:Command.GetOptionList()) {
					OptionType Type = null;
					switch (Option.GetType()) {
						case String: {
							Type = OptionType.STRING;
							break;
						}

						case Int: {
							Type = OptionType.INTEGER;
							break;
						}
					}

					OptionData SlashOption = new OptionData(Type, Option.GetName(), "説明", Option.isRequire());
					OptionList.add(SlashOption);
				}

				//コマンドの情報
				SlashCommandData SlashCommand = Commands.slash(Command.GetName(), "説明");
				SlashCommand.addOptions(OptionList);
				//追加
				SlashCommandList.add(SlashCommand);
			}

			//機能設定用コマンド
			SlashCommandList.add(GenFunctionSettingCommand());

			//スラッシュコマンド登録
			DISCORD_BOT.updateCommands().addCommands(SlashCommandList).queue();
			LOG(LOG_TYPE.OK, "DiscordBOT:" + SlashCommandList.size() + "個のスラッシュコマンドを登録しました");
		} catch (Exception EX) {
			EX.printStackTrace();
		}
	}

	private static SlashCommandData GenFunctionSettingCommand() {
		SlashCommandData Command = Commands.slash("setting", "機能を設定します");

		//機能一覧
		OptionData FunctionOption = new OptionData(OptionType.STRING, "function", "機能", true);
		for (DiscordFunction Function:DiscordFunction.values()) {
			FunctionOption.addChoice(Function.name(), Function.name());
		}
		Command.addOptions(FunctionOption);

		//有効化無効化
		OptionData EnableOption = new OptionData(OptionType.BOOLEAN, "enable", "有効化無効化", true);
		Command.addOptions(EnableOption);

		return Command;
	}
}
