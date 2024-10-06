package com.rumisystem.rumiabot.Discord;

import net.dv8tion.jda.api.entities.channel.concrete.TextChannel;
import net.dv8tion.jda.api.entities.emoji.Emoji;
import net.dv8tion.jda.api.events.guild.GuildJoinEvent;
import net.dv8tion.jda.api.events.guild.GuildLeaveEvent;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.events.interaction.component.ButtonInteractionEvent;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;

import static com.rumisystem.rumi_java_lib.LOG_PRINT.Main.LOG;
import static com.rumisystem.rumiabot.Main.CONFIG_DATA;
import static com.rumisystem.rumiabot.Main.DISCORD_BOT;

import java.time.LocalDateTime;
import java.time.ZoneOffset;

import com.rumisystem.rumi_java_lib.LOG_PRINT.LOG_TYPE;
import com.rumisystem.rumiabot.Discord.COMMAND.SETTING;
import com.rumisystem.rumiabot.Discord.COMMAND.info_server;
import com.rumisystem.rumiabot.Discord.COMMAND.info_user;
import com.rumisystem.rumiabot.Discord.COMMAND.ip;
import com.rumisystem.rumiabot.Discord.COMMAND.wh_clear;
import com.rumisystem.rumiabot.Discord.COMMAND.ws;
import com.rumisystem.rumiabot.Discord.FUNCTION.MESSAGE_INFO;
import com.rumisystem.rumiabot.Discord.FUNCTION.VXTWITTER;
import com.rumisystem.rumiabot.MODULE.DATE_FORMAT;

public class DiscordEventListener extends ListenerAdapter{
	@Override
	public void onMessageReceived(MessageReceivedEvent E) {
		//ブロック済みのユーザーなら此処で処理を中断する
		if (CONFIG_DATA.get("BLOCK").asString("DISCORD").contains(E.getAuthor().getId())) {
			return;
		}

		if (E.getMessage().getAttachments().size() != 0) {
			E.getMessage().addReaction(Emoji.fromUnicode("✅")).queue();
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
		//ブロック済みのユーザーなら此処で処理を中断する
		if(CONFIG_DATA.get("BLOCK").asString("DISCORD").contains(INTERACTION.getUser().getId())){
			return;
		}
	}

	@Override
	public void onSlashCommandInteraction(SlashCommandInteractionEvent INTERACTION){
		//ブロック済みのユーザーなら此処で処理を中断する
		if(CONFIG_DATA.get("BLOCK").asString("DISCORD").contains(INTERACTION.getUser().getId())){
			return;
		}

		INTERACTION.deferReply().queue();
		
		switch(INTERACTION.getName()) {
			case "test":{
				INTERACTION.getHook().editOriginal("あいうえお").queue();
				break;
			}
			
			case "help":{
				INTERACTION.getHook().editOriginal("ここで見れる\nhttps://rumiserver.com/rumiabot/site/function").queue();
				break;
			}

			case "ip":{
				ip.Main(INTERACTION);
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
		
			default:{
				INTERACTION.getHook().editOriginal("コマンドが見つからなかった").queue();
			}
		}
	}

	@Override//鯖に参加
	public void onGuildJoin(GuildJoinEvent E){
		TextChannel CH = DISCORD_BOT.getTextChannelById("1128742498194444298");
		if(CH != null){
			CH.sendMessage( E.getGuild().getName().replace("@", "AD") + "に参加しました！\n" +
					"これで" + DISCORD_BOT.getGuilds().size() + "個の鯖に参加しました。").queue();
		}
	}

	@Override
	public void onGuildLeave(GuildLeaveEvent E){
		TextChannel CH = DISCORD_BOT.getTextChannelById("1128742498194444298");
		if(CH != null){
			CH.sendMessage( E.getGuild().getName().replace("@", "AD") + "から叩き出されました。。。\n" +
					"これで" + DISCORD_BOT.getGuilds().size() + "個の鯖になりました").queue();
		}
	}
}
