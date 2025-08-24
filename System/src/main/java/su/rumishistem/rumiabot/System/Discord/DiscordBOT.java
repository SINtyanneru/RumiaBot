package su.rumishistem.rumiabot.System.Discord;

import static su.rumishistem.rumiabot.System.Main.*;
import static su.rumishistem.rumi_java_lib.LOG_PRINT.Main.LOG;

import java.util.HashMap;
import net.dv8tion.jda.api.*;
import net.dv8tion.jda.api.entities.*;
import net.dv8tion.jda.api.exceptions.InsufficientPermissionException;
import net.dv8tion.jda.api.requests.GatewayIntent;
import net.dv8tion.jda.api.utils.MemberCachePolicy;
import net.dv8tion.jda.internal.utils.JDALogger;
import su.rumishistem.rumi_java_lib.LOG_PRINT.LOG_TYPE;

public class DiscordBOT {
	public static HashMap<String, HashMap<String, Integer>> InviteTable = new HashMap<String, HashMap<String, Integer>>();

	public static void Init() throws InterruptedException {
		JDALogger.setFallbackLoggerEnabled(false);

		//JDAビルダー
		JDABuilder JDA_BUILDER = JDABuilder.createDefault(CONFIG_DATA.get("DISCORD").getData("TOKEN").asString());

		JDA_BUILDER.enableIntents(
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
		JDA_BUILDER.setActivity(Activity.watching("お前"));
		JDA_BUILDER.setStatus(OnlineStatus.ONLINE);

		//ビルド
		DISCORD_BOT = JDA_BUILDER.build();

		DISCORD_BOT.awaitReady();
		LOG(LOG_TYPE.OK, "Discord Logined");
	}

	public static void GetAllGuildInvite() {
		for (Guild G:DISCORD_BOT.getGuilds()) {
			GetGuildInvite(G);
		}
	}

	public static void GetGuildInvite(Guild G) {
		try {
			G.retrieveInvites().queue(InvList->{
				HashMap<String, Integer> Data = new HashMap<String, Integer>();
				for (Invite Inv:InvList) {
					Data.put(Inv.getCode(), Inv.getUses());
				}
				InviteTable.put(G.getId(), Data);
				LOG(LOG_TYPE.OK, "招待コード取得：" + G.getId());
			});
		} catch (InsufficientPermissionException EX) {
			//無視
		}
	}
}