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
						GatewayIntent.MESSAGE_CONTENT,
						GatewayIntent.GUILD_MEMBERS,
						GatewayIntent.GUILD_MESSAGES,
						GatewayIntent.GUILD_PRESENCES,
						GatewayIntent.GUILD_VOICE_STATES,
						GatewayIntent.GUILD_EMOJIS_AND_STICKERS,
						GatewayIntent.SCHEDULED_EVENTS,
						GatewayIntent.DIRECT_MESSAGES,
						GatewayIntent.DIRECT_MESSAGE_REACTIONS,
						GatewayIntent.DIRECT_MESSAGE_TYPING
				);

				//設定
				JDA_BUILDER.setRawEventsEnabled(true);
				JDA_BUILDER.setEventPassthrough(true);
				JDA_BUILDER.addEventListeners(new DiscordEvent());

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
		SlashCommandData test = Commands.slash("test", "テスト用").setNameLocalization(DiscordLocale.JAPANESE, "テスト");
		SlashCommandData info_server = Commands.slash("info_server", "鯖の情報を取得").setNameLocalization(DiscordLocale.JAPANESE, "サーバーの情報");
		SlashCommandData info_user = Commands.slash("info_user", "ユーザー情報取得").setNameLocalization(DiscordLocale.JAPANESE, "ユーザー情報")
				.addOption(OptionType.USER, "user", "ユーザーを指定しろ", false);

		BOT.updateCommands().addCommands(
				test,
				info_server,
				info_user
		).queue();

		System.out.println("コマンドを全て登録しました");
	}
}
