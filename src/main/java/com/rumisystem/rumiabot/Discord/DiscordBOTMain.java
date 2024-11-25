package com.rumisystem.rumiabot.Discord;

import net.dv8tion.jda.api.JDABuilder;
import net.dv8tion.jda.api.OnlineStatus;
import net.dv8tion.jda.api.entities.Activity;
import net.dv8tion.jda.api.interactions.commands.OptionType;
import net.dv8tion.jda.api.interactions.commands.build.Commands;
import net.dv8tion.jda.api.interactions.commands.build.OptionData;
import net.dv8tion.jda.api.interactions.commands.build.SlashCommandData;
import net.dv8tion.jda.api.requests.GatewayIntent;
import net.dv8tion.jda.api.utils.MemberCachePolicy;
import net.dv8tion.jda.internal.utils.JDALogger;

import static com.rumisystem.rumi_java_lib.LOG_PRINT.Main.LOG;
import static com.rumisystem.rumiabot.Main.CONFIG_DATA;
import static com.rumisystem.rumiabot.Main.DISCORD_BOT;

import com.rumisystem.rumi_java_lib.LOG_PRINT.LOG_TYPE;

public class DiscordBOTMain {
	public static void START_DISCORD_BOT() throws InterruptedException {
		JDALogger.setFallbackLoggerEnabled(false);

		//JDAビルダーを作る
		JDABuilder JDA_BUILDER = JDABuilder.createDefault(
				CONFIG_DATA.get("DISCORD").asString("TOKEN"),
				GatewayIntent.GUILD_MEMBERS,
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
		JDA_BUILDER.addEventListeners(new DiscordEventListener());
		JDA_BUILDER.setMemberCachePolicy(MemberCachePolicy.ALL);
		JDA_BUILDER.setAutoReconnect(true);

		//ステータス
		JDA_BUILDER.setActivity(Activity.watching("貴様"));
		JDA_BUILDER.setStatus(OnlineStatus.ONLINE);

		//ビルド
		DISCORD_BOT = JDA_BUILDER.build();

		//ログインするまで待つ
		DISCORD_BOT.awaitReady();

		LOG(LOG_TYPE.OK, "BOT ready!");

		REGIST_SLASHCOMMAND();
	}
	
	public static void REGIST_SLASHCOMMAND(){
		SlashCommandData test = Commands.slash("test", "テスト用");
		
		SlashCommandData help = Commands.slash("help", "テスト用");

		SlashCommandData ip = Commands.slash("ip", "IPを開示します");

		SlashCommandData dam = Commands.slash("dam", "石手川ダムの情報を取得します");

		SlashCommandData info_server = Commands.slash("info_server", "サーバー情報開示");

		SlashCommandData info_user = Commands.slash("info_user", "ユーザー情報開示")
			.addOption(OptionType.USER, "user", "ユーザー指定", false);

		OptionData WS_OPTION = new OptionData(OptionType.STRING, "size", "ヰンドウサイズ", false);
		WS_OPTION.addChoice("フルHD", "1098x1080");
		WS_OPTION.addChoice("フルサイズ", "FULL");

		SlashCommandData ws = Commands.slash("ws", "ヱブサイトスクショ")
				.addOption(OptionType.STRING, "url", "ウーエルエル", true)
				.addOptions(WS_OPTION);

		SlashCommandData wh_clear = Commands.slash("wh_clear", "WebHookを全消しする");
		
		OptionData SETTING_FUNCTION_OPTION = new OptionData(OptionType.STRING, "function", "設定項目", true);
		SETTING_FUNCTION_OPTION.addChoice("VXTwitter変換", "vxtwitter");

		OptionData SETTING_TF_OPTION = new OptionData(OptionType.STRING, "tf", "有効無効", true);
		SETTING_TF_OPTION.addChoice("true", "true");
		SETTING_TF_OPTION.addChoice("false", "false");

		SlashCommandData SETTING = Commands.slash("setting", "設定");
		SETTING.addOptions(SETTING_FUNCTION_OPTION, SETTING_TF_OPTION);

		DISCORD_BOT.updateCommands().addCommands(
			test,
			help,
			ip,
			info_server,
			info_user,
			ws,
			wh_clear,
			SETTING,
			dam
		).queue();

		LOG(LOG_TYPE.OK, "コマンドを登録");
	}
}
