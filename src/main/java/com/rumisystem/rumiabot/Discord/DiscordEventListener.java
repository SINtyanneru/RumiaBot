package com.rumisystem.rumiabot.Discord;

import net.dv8tion.jda.api.entities.channel.concrete.PrivateChannel;
import net.dv8tion.jda.api.entities.channel.concrete.TextChannel;
import net.dv8tion.jda.api.entities.emoji.Emoji;
import net.dv8tion.jda.api.events.guild.GuildJoinEvent;
import net.dv8tion.jda.api.events.guild.GuildLeaveEvent;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.events.interaction.component.ButtonInteractionEvent;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import net.dv8tion.jda.api.requests.RestAction;

import static com.rumisystem.rumi_java_lib.LOG_PRINT.Main.LOG;
import static com.rumisystem.rumiabot.Main.CONFIG_DATA;
import static com.rumisystem.rumiabot.Main.DISCORD_BOT;

import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.time.format.DateTimeFormatter;
import java.util.Locale;

import com.rumisystem.rumi_java_lib.ArrayNode;
import com.rumisystem.rumi_java_lib.EXCEPTION_READER;
import com.rumisystem.rumi_java_lib.SQL;
import com.rumisystem.rumi_java_lib.LOG_PRINT.LOG_TYPE;
import com.rumisystem.rumiabot.Discord.COMMAND.SETTING;
import com.rumisystem.rumiabot.Discord.COMMAND.dam;
import com.rumisystem.rumiabot.Discord.COMMAND.info_server;
import com.rumisystem.rumiabot.Discord.COMMAND.info_user;
import com.rumisystem.rumiabot.Discord.COMMAND.ip;
import com.rumisystem.rumiabot.Discord.COMMAND.wh_clear;
import com.rumisystem.rumiabot.Discord.COMMAND.ws;
import com.rumisystem.rumiabot.Discord.FUNCTION.MESSAGE_INFO;
import com.rumisystem.rumiabot.Discord.FUNCTION.VERIFY_PANEL;
import com.rumisystem.rumiabot.Discord.FUNCTION.VXTWITTER;
import com.rumisystem.rumiabot.MODULE.COMMAND_INTERACTION;
import com.rumisystem.rumiabot.MODULE.DATE_FORMAT;
import com.rumisystem.rumiabot.MODULE.ISHITEGAWA.DAM_STATUS;
import com.rumisystem.rumiabot.MODULE.ISHITEGAWA.ISHITEGAWA_DAM;

public class DiscordEventListener extends ListenerAdapter{
	@Override
	public void onMessageReceived(MessageReceivedEvent E) {
		//ブロック済みのユーザーなら此処で処理を中断する
		if (CONFIG_DATA.get("BLOCK").asString("DISCORD").contains(E.getAuthor().getId())) {
			return;
		}

		if (E.getMessage().getAttachments().size() != 0) {
			//E.getMessage().addReaction(Emoji.fromUnicode("✅")).queue();
			//TODO:ロガーつけよかな
		}

		//時間
		if (E.getMessage().getContentRaw().equals("時間")) {
			E.getMessage().reply(
					"現在時刻は\n"
					+ "和暦：" + DATE_FORMAT.KOUKI(LocalDateTime.now().atOffset(ZoneOffset.ofHours(9)))
					+"\n"
					+"西暦：" + DATE_FORMAT.ZHUUNI_H(LocalDateTime.now().atOffset(ZoneOffset.ofHours(9)))
					+"\n"
					+"です"
			).queue();
		}

		//メッセージ情報
		if (E.getMessage().getContentRaw().equals("r>info")) {
			if (E.getMessage().getReferencedMessage() != null) {
				MESSAGE_INFO.Main(E.getMessage().getReferencedMessage(), E.getMessage());
			}
		}

		//VXTwitter変換
		VXTWITTER.Main(E);

		LOG(LOG_TYPE.INFO, E.getGuild().getName() + "/" + E.getChannel().getName() + "|" + E.getAuthor().getName() + "[" + E.getMessage().getContentRaw() + "]");
	}

	@Override
	public void onButtonInteraction(ButtonInteractionEvent INTERACTION) {
		try {
			//ブロック済みのユーザーなら此処で処理を中断する
			if(CONFIG_DATA.get("BLOCK").asString("DISCORD").contains(INTERACTION.getUser().getId())){
				return;
			}

			INTERACTION.deferReply(true).queue();

			switch(INTERACTION.getComponentId().split("\\?")[0]) {
				case "verify_panel": {
					VERIFY_PANEL.VERIFY_BUTTON(INTERACTION);
					break;
				}
			}
		} catch (Exception EX) {
			EX.printStackTrace();
			String EX_TEXT = EXCEPTION_READER.READ(EX);
			INTERACTION.getHook().editOriginal("システムエラー\n```\n" + EX_TEXT + "\n```").queue();
		}
	}

	@Override
	public void onSlashCommandInteraction(SlashCommandInteractionEvent INTERACTION){
		try {
			//ブロック済みのユーザーなら此処で処理を中断する
			if(CONFIG_DATA.get("BLOCK").asString("DISCORD").contains(INTERACTION.getUser().getId())){
				return;
			}
			
			COMMAND_INTERACTION CI = new COMMAND_INTERACTION(INTERACTION);

			CI.deferReply();
			
			switch(INTERACTION.getName()) {
				case "test":{
					CI.SetTEXT("あいうえお");
					CI.Reply();
					break;
				}
				
				case "help":{
					CI.SetTEXT("ここで見れる\nhttps://rumiserver.com/rumiabot/site/function");
					CI.Reply();
					break;
				}

				case "ip":{
					ip.Main(CI);
					break;
				}

				case "info_server":{
					info_server.Main(INTERACTION);
					break;
				}
				
				case "info_user":{
					info_user.Main(INTERACTION);
					break;
				}
				
				case "ws":{
					ws.Main(INTERACTION);
					break;
				}
				
				case "wh_clear":{
					wh_clear.Main(INTERACTION);
					break;
				}
				
				case "setting":{
					SETTING.Main(INTERACTION);
					break;
				}

				case "dam":{
					dam.Main(INTERACTION);
					break;
				}

				case "verify_panel": {
					VERIFY_PANEL.PANEL_CREATE(INTERACTION);
					break;
				}

				default:{
					INTERACTION.getHook().editOriginal("コマンドが見つからなかった").queue();
				}
			}
		} catch (Exception EX) {
			EX.printStackTrace();
			String EX_TEXT = EXCEPTION_READER.READ(EX);
			INTERACTION.getHook().editOriginal("システムエラー\n```\n" + EX_TEXT + "\n```").queue();
		}
	}

	@Override//鯖に参加
	public void onGuildJoin(GuildJoinEvent E){
		try {
			//通知
			TextChannel CH = DISCORD_BOT.getTextChannelById("1128742498194444298");
			if(CH != null){
				CH.sendMessage( E.getGuild().getName().replace("@", "AD") + "に参加しました！\n" +
						"これで" + DISCORD_BOT.getGuilds().size() + "個の鯖に参加しました。").queue();
			}

			//ブラックリスト
			ArrayNode RESULT = SQL.RUN("SELECT * FROM `GUILD_BLACKLIST` WHERE `GID` = ?", new Object[] {E.getGuild().getId()});
			if (RESULT.get(0) != null) {
				ArrayNode INFO = RESULT.get(0);
				if (E.getGuild().getOwner() != null) {
					//オーナーのDMを開く
					E.getGuild().getOwner().getUser().openPrivateChannel().queue((DM)->{
						try {
							StringBuilder TEXT = new StringBuilder();
							TEXT.append("あなたのサーバーはブラックリストに入っています\n");
							TEXT.append("理由：" + INFO.asString("RESON") + "\n");

							//DM送信
							DM.sendMessage(TEXT.toString()).queue();
						} catch (Exception EX) {
							EX.printStackTrace();
						} finally {
							//脱退
							E.getGuild().leave().queue();
						}
					});
				} else {
					//脱退
					E.getGuild().leave().queue();
				}
			}
		} catch (Exception EX) {
			EX.printStackTrace();
		}
	}

	@Override
	public void onGuildLeave(GuildLeaveEvent E){
		try {
			//ブラックリスト
			SQL.UP_RUN("INSERT INTO `GUILD_BLACKLIST` (`GID`, `RESON`) VALUES (?, ?)", new Object[] {
				E.getGuild().getId(),
				LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy年MM月dd日E曜日 ah時m分s秒", Locale.JAPANESE)) +"脱退させられた"
			});

			//通知
			TextChannel CH = DISCORD_BOT.getTextChannelById("1128742498194444298");
			if(CH != null){
				CH.sendMessage( E.getGuild().getName().replace("@", "AD") + "から叩き出されました。。。\n" +
						"これで" + DISCORD_BOT.getGuilds().size() + "個の鯖になりました").queue();
			}
		} catch (Exception EX) {
			EX.printStackTrace();
		}
	}
}
