package su.rumishistem.rumiabot.System.Discord;

import static su.rumishistem.rumiabot.System.Main.CommandList;
import static su.rumishistem.rumiabot.System.Main.DISCORD_BOT;

import java.util.ArrayList;
import java.util.List;

import static su.rumishistem.rumiabot.System.Main.CONFIG_DATA;
import static su.rumishistem.rumi_java_lib.LOG_PRINT.Main.LOG;
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
import su.rumishistem.rumi_java_lib.LOG_PRINT.LOG_TYPE;
import su.rumishistem.rumiabot.System.TYPE.CommandData;
import su.rumishistem.rumiabot.System.TYPE.CommandOption;

public class DiscordBOT {
	public static void Init() throws InterruptedException {
		JDALogger.setFallbackLoggerEnabled(false);

		//JDAビルダー
		JDABuilder JDA_BUILDER = JDABuilder.createDefault(
			CONFIG_DATA.get("DISCORD").getData("TOKEN").asString(),
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

		//ログイン待ち
		DISCORD_BOT.awaitReady();
		LOG(LOG_TYPE.OK, "DiscordBOT ready!");

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

		//スラッシュコマンド登録
		DISCORD_BOT.updateCommands().addCommands(SlashCommandList).queue();
		LOG(LOG_TYPE.OK, "DiscordBOT:" + SlashCommandList.size() + "個のスラッシュコマンドを登録しました");
	}
}