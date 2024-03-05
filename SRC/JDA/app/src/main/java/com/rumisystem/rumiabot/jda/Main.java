package com.rumisystem.rumiabot.jda;

import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.JDABuilder;
import net.dv8tion.jda.api.OnlineStatus;
import net.dv8tion.jda.api.entities.Activity;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.interactions.DiscordLocale;
import net.dv8tion.jda.api.interactions.commands.Command;
import net.dv8tion.jda.api.interactions.commands.OptionType;
import net.dv8tion.jda.api.interactions.commands.build.Commands;
import net.dv8tion.jda.api.interactions.commands.build.SlashCommandData;
import net.dv8tion.jda.api.requests.GatewayIntent;
import net.dv8tion.jda.api.utils.MemberCachePolicy;

public class Main {
	public static JDA BOT = null;

	public static void main(String[] args) {
		try{
			System.out.println("JDA");

			//設定ファイルをロード
			CONFIG.LOAD();

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
			}
		}catch (Exception EX){
			EX.printStackTrace();
		}
	}

	public static void REGIST_SLASHCOMMAND(){
		SlashCommandData test = Commands.slash("test", "テスト用");
		SlashCommandData ip = Commands.slash("ip", "IPアドレスを開示します");
		SlashCommandData info_server = Commands.slash("info_server", "鯖の情報を取得");
		SlashCommandData info_user = Commands.slash("info_user", "ユーザー情報取得")
				.addOption(OptionType.USER, "user", "ユーザーを指定しろ", false);
		SlashCommandData ws = Commands.slash("ws", "ヱブサイトスクショ")
				.addOption(OptionType.STRING, "url", "ウーエルエル", true);
		SlashCommandData mazokupic = Commands.slash("mazokupic", "まちカドまぞくのイラストをランダムに");

		BOT.updateCommands().addCommands(
				test,
				ip,
				info_server,
				info_user,
				ws,
				mazokupic
		).queue();

		System.out.println("コマンドを全て登録しました");
	}
}
