package com.rumisystem.rumiabot.jda;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.rumisystem.rumiabot.jda.MODULE.FUNCTION;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.JDABuilder;
import net.dv8tion.jda.api.OnlineStatus;
import net.dv8tion.jda.api.entities.Activity;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.interactions.DiscordLocale;
import net.dv8tion.jda.api.interactions.commands.Command;
import net.dv8tion.jda.api.interactions.commands.OptionType;
import net.dv8tion.jda.api.interactions.commands.build.Commands;
import net.dv8tion.jda.api.interactions.commands.build.OptionData;
import net.dv8tion.jda.api.interactions.commands.build.SlashCommandData;
import net.dv8tion.jda.api.requests.GatewayIntent;
import net.dv8tion.jda.api.utils.MemberCachePolicy;
import net.dv8tion.jda.api.utils.Result;

import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;

import static com.rumisystem.rumiabot.jda.PT.SEND;

public class Main {
	public static JDA BOT = null;

	public static void main(String[] args) {
		try{
			System.out.println("JDA");

			//設定ファイルをロード
			CONFIG.LOAD();

			//new SQL();
			//ResultSet SQL_RESULT = SQL.RUN("SELECT * FROM `CONFIG` ", new Object[]{});
			//System.out.println(SQL.SQL_RESULT_TO_JSON(SQL_RESULT));

			//設定ファイルを読み込めたか
			if(CONFIG.CONFIG_DATA != null){
				//JDAビルダーを作る
				JDABuilder JDA_BUILDER = JDABuilder.createDefault(
						CONFIG.CONFIG_DATA.get("DISCORD").get("TOKEN").asText(),
						GatewayIntent.GUILD_MEMBERS,
						GatewayIntent.GUILD_BANS,
						GatewayIntent.GUILD_MODERATION,
						GatewayIntent.GUILD_EMOJIS_AND_STICKERS,
						GatewayIntent.GUILD_WEBHOOKS,
						GatewayIntent.GUILD_INVITES,
						GatewayIntent.GUILD_VOICE_STATES,
						GatewayIntent.GUILD_PRESENCES,
						GatewayIntent.GUILD_MESSAGES,
						GatewayIntent.GUILD_MESSAGE_REACTIONS,
						GatewayIntent.GUILD_MESSAGE_TYPING,
						GatewayIntent.DIRECT_MESSAGES,
						GatewayIntent.DIRECT_MESSAGE_REACTIONS,
						GatewayIntent.DIRECT_MESSAGE_TYPING,
						GatewayIntent.MESSAGE_CONTENT,
						GatewayIntent.SCHEDULED_EVENTS,
						GatewayIntent.AUTO_MODERATION_CONFIGURATION,
						GatewayIntent.AUTO_MODERATION_EXECUTION
				);

				//設定
				JDA_BUILDER.setRawEventsEnabled(true);
				JDA_BUILDER.setEventPassthrough(true);
				JDA_BUILDER.addEventListeners(new DiscordEvent());
				JDA_BUILDER.setMemberCachePolicy(MemberCachePolicy.ALL);

				//ステータス
				JDA_BUILDER.setActivity(Activity.watching("貴様"));
				JDA_BUILDER.setStatus(OnlineStatus.ONLINE);

				//ビルド
				BOT = JDA_BUILDER.build();

				//ログインするまで待つ
				BOT.awaitReady();

				System.out.println("BOT Ready");

				REGIST_SLASHCOMMAND();

				//TELNETに接続
				new Thread(new Runnable() {
					@Override
					public void run() {
						PT.main(3001);
					}
				}).start();
			}
		}catch (Exception EX){
			EX.printStackTrace();
		}
	}

	public static void REGIST_SLASHCOMMAND(){
		SlashCommandData test = Commands.slash("test", "テスト用");

		SlashCommandData help = Commands.slash("help", "ヘルプコマンド");

		SlashCommandData ip = Commands.slash("ip", "IPアドレスを開示します");

		SlashCommandData info_server = Commands.slash("info_server", "鯖の情報を取得");

		SlashCommandData info_user = Commands.slash("info_user", "ユーザー情報取得")
				.addOption(OptionType.USER, "user", "ユーザーを指定しろ", false);

		OptionData WS_OPTION = new OptionData(OptionType.STRING, "size", "ヰンドウサイズ", false);
		WS_OPTION.addChoice("フルHD", "1098x1080");
		WS_OPTION.addChoice("フルサイズ", "FULL");

		SlashCommandData ws = Commands.slash("ws", "ヱブサイトスクショ")
				.addOption(OptionType.STRING, "url", "ウーエルエル", true)
				.addOptions(WS_OPTION);

		SlashCommandData mazokupic = Commands.slash("mazokupic", "まちカドまぞくのイラストをランダムに");

		SlashCommandData ping = Commands.slash("ping", "pingする")
				.addOption(OptionType.STRING, "ip", "ping先", true);

		SlashCommandData wh_clear = Commands.slash("wh_clear", "WebHookを前消しする");

		SlashCommandData mandenburo = Commands.slash("mandenburo", "マンデンブロ集合を作る");

		OptionData VOICEVOX_OPTION = new OptionData(OptionType.STRING, "speeker", "話者", true);
		VOICEVOX_OPTION.addChoice("ずんだもん/ノーマル", "3");
		VOICEVOX_OPTION.addChoice("ずんだもん/あまあま", "1");

		SlashCommandData voicevox = Commands.slash("voicevox", "VOICEVOXに音声を生成させます")
				.addOption(OptionType.STRING, "text", "本文", true)
				.addOptions(VOICEVOX_OPTION);

		BOT.updateCommands().addCommands(
				test,
				help,
				ip,
				info_server,
				info_user,
				ws,
				mazokupic,
				ping,
				wh_clear,
				mandenburo,
				voicevox,
				FUNCTION.CREATE_SLASH_COMMAND()
		).queue();

		System.out.println("コマンドを全て登録しました");
	}

	public static void LOG(int LEVEL, String CLASS, String TEXT){
		switch (LEVEL){
			case 0:{
				System.out.println("[  \u001B[32mOK\u001B[0m  ][" + CLASS + "] " + TEXT);
				break;
			}

			case 1:{
				System.out.println("[\u001B[31mFAILED\u001B[0m][" + CLASS + "] " + TEXT);
				break;
			}

			case 2:{
				System.out.println("[ INFO ][" + CLASS + "] " + TEXT);
				break;
			}

			case 3:{
				System.out.println("[ **** ][" + CLASS + "] " + TEXT);
				break;
			}

			case 4:{
				System.out.println("\u001B[1F[  \u001B[32mOK\u001B[0m  ]");
				break;
			}

			case 5:{
				System.out.println("\u001B[1F[\u001B[31mFAILED\u001B[0m]");
				break;
			}
		}
	}
}
