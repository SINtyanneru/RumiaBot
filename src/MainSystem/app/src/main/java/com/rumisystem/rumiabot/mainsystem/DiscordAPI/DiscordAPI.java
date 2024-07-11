package com.rumisystem.rumiabot.mainsystem.DiscordAPI;

import static com.rumisystem.rumiabot.mainsystem.Main.CONFIG_DATA;

import static com.rumisystem.rumi_java_lib.LOG_PRINT.Main.LOG;
import com.rumisystem.rumi_java_lib.LOG_PRINT.LOG_TYPE;

import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.JDABuilder;
import net.dv8tion.jda.api.OnlineStatus;
import net.dv8tion.jda.api.entities.Activity;
import net.dv8tion.jda.api.requests.GatewayIntent;
import net.dv8tion.jda.api.utils.MemberCachePolicy;

public class DiscordAPI {
	public static JDA BOT = null;

	public static void Main() {
		try {
			// JDAビルダーを作る
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

			// 設定
			JDA_BUILDER.setRawEventsEnabled(true);
			JDA_BUILDER.setEventPassthrough(true);
			JDA_BUILDER.addEventListeners(new DiscordEvent());
			JDA_BUILDER.setMemberCachePolicy(MemberCachePolicy.ALL);
			JDA_BUILDER.setAutoReconnect(true);

			// ステータス
			JDA_BUILDER.setActivity(Activity.watching("貴様"));
			JDA_BUILDER.setStatus(OnlineStatus.ONLINE);

			// ビルド
			BOT = JDA_BUILDER.build();

			// ログインするまで待つ
			BOT.awaitReady();
		} catch (Exception EX) {
			LOG(LOG_TYPE.FAILED, "DiscordAPI.java ERR!");
			EX.printStackTrace();
			System.exit(1);
		}
	}
}