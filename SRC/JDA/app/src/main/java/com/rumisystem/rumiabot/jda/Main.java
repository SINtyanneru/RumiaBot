package com.rumisystem.rumiabot.jda;

import com.rumisystem.rumiabot.jda.FUNCTION.GUTEN_MORGEN;
import com.rumisystem.rumiabot.jda.MODULE.FUNCTION;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.JDABuilder;
import net.dv8tion.jda.api.OnlineStatus;
import net.dv8tion.jda.api.entities.Activity;
import net.dv8tion.jda.api.interactions.commands.OptionType;
import net.dv8tion.jda.api.interactions.commands.build.Commands;
import net.dv8tion.jda.api.interactions.commands.build.OptionData;
import net.dv8tion.jda.api.interactions.commands.build.SlashCommandData;
import net.dv8tion.jda.api.requests.GatewayIntent;
import net.dv8tion.jda.api.utils.MemberCachePolicy;

import java.util.HashMap;

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
				JDA_BUILDER.setAutoReconnect(true);

				//ステータス
				JDA_BUILDER.setActivity(Activity.watching("貴様"));
				JDA_BUILDER.setStatus(OnlineStatus.ONLINE);

				//ビルド
				BOT = JDA_BUILDER.build();

				//ログインするまで待つ
				BOT.awaitReady();

				System.out.println("BOT Ready");

				REGIST_SLASHCOMMAND();

				//機能を有効化する
				GUTEN_MORGEN.Main();

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
		//test
		SlashCommandData test = Commands.slash("test", "テスト用");

		//help
		SlashCommandData help = Commands.slash("help", "ヘルプコマンド");

		//IP
		SlashCommandData ip = Commands.slash("ip", "IPアドレスを開示します");

		//鯖情報
		SlashCommandData info_server = Commands.slash("info_server", "鯖の情報を取得");

		//ユーザー情報
		SlashCommandData info_user = Commands.slash("info_user", "ユーザー情報取得")
				.addOption(OptionType.USER, "user", "ユーザーを指定しろ", false);

		//ヱブサイトスクショ
		OptionData WS_OPTION = new OptionData(OptionType.STRING, "size", "ヰンドウサイズ", false);
		WS_OPTION.addChoice("フルHD", "1098x1080");
		WS_OPTION.addChoice("フルサイズ", "FULL");

		SlashCommandData ws = Commands.slash("ws", "ヱブサイトスクショ")
				.addOption(OptionType.STRING, "url", "ウーエルエル", true)
				.addOptions(WS_OPTION);

		//まぞくイラスト
		SlashCommandData mazokupic = Commands.slash("mazokupic", "まちカドまぞくのイラストをランダムに");

		//ping
		SlashCommandData ping = Commands.slash("ping", "pingする")
				.addOption(OptionType.STRING, "ip", "ping先", true);

		//Webhook全消し
		SlashCommandData wh_clear = Commands.slash("wh_clear", "WebHookを全消しする");

		//マンデンブロ
		SlashCommandData mandenburo = Commands.slash("mandenburo", "マンデンブロ集合を作る");

		//VOICEVOX
		OptionData VOICEVOX_OPTION = new OptionData(OptionType.STRING, "speeker", "話者", true);
		VOICEVOX_OPTION.addChoice("ずんだもん/ノーマル", "3");
		VOICEVOX_OPTION.addChoice("ずんだもん/あまあま", "1");

		SlashCommandData voicevox = Commands.slash("voicevox", "VOICEVOXに音声を生成させます")
				.addOption(OptionType.STRING, "text", "本文", true)
				.addOptions(VOICEVOX_OPTION);

		//認証パネル
		SlashCommandData VERIFY_PANEL = Commands.slash("verify_panel", "認証パネルを設置します")
				.addOption(OptionType.ROLE, "role", "ロール", true);

		//QR生成
		SlashCommandData QR = Commands.slash("qr", "QRを生成する")
				.addOption(OptionType.STRING, "data", "内容", true);

		OptionData QR_OPTION = new OptionData(OptionType.STRING, "type", "形式", true);
		QR_OPTION.addChoice("QR", "QR");
		QR_OPTION.addChoice("rMQR", "RMQR");
		QR_OPTION.addChoice("tQR", "TQR");
		QR_OPTION.addChoice("mQR", "MQR");
		QR.addOptions(QR_OPTION);

		//全部追加する
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
				FUNCTION.CREATE_SLASH_COMMAND(),
				VERIFY_PANEL,
				QR
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

	public static HashMap<String, String> URI_PARAM_PARSE(String URI){
		HashMap<String, String> RESULT = new HashMap<>();

		String[] SPLIT_URI = URI.split("\\?")[1].split("&");

		for(int I = 0; I < SPLIT_URI.length; I++){
			String KEY = SPLIT_URI[I].split("=")[0];
			String VAL = SPLIT_URI[I].split("=")[1];

			RESULT.put(KEY, VAL);
		}

		return RESULT;
	}
}
