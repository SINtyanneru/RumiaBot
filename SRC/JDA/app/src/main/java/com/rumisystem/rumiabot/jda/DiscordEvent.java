package com.rumisystem.rumiabot.jda;

import com.rumisystem.rumiabot.jda.COMMAND.*;
import com.rumisystem.rumiabot.jda.MODULE.HTTP_REQUEST;
import net.dv8tion.jda.api.entities.IMentionable;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.MessageType;
import net.dv8tion.jda.api.entities.channel.concrete.TextChannel;
import net.dv8tion.jda.api.events.guild.GuildJoinEvent;
import net.dv8tion.jda.api.events.guild.GuildLeaveEvent;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;

import java.io.File;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Date;
import java.util.Objects;

import static com.rumisystem.rumiabot.jda.Main.BOT;

public class DiscordEvent extends ListenerAdapter {
	@Override
	public void onMessageReceived(MessageReceivedEvent E) {
		try {
			String MESSAGE_CONTENT = E.getMessage().getContentRaw();
			String GUILD_NAME = "localhost";

			//鯖でのメッセージなら、鯖名に鯖名を入れる
			if(E.isFromGuild()){
				GUILD_NAME = E.getGuild().getName();
			}

			//ログを出す部分
			StringBuilder LOG_TEXT = new StringBuilder("┌[" + E.getAuthor().getName() + "@" + GUILD_NAME + "/" + E.getChannel().getName() + "]\n");
			String[] TEXT_SPLIT = MESSAGE_CONTENT.split("\n");
			for(int I = 0; TEXT_SPLIT.length > I; I++){
				String TEXT = TEXT_SPLIT[I];
				if(TEXT_SPLIT.length > I + 1){
					LOG_TEXT.append("├" + TEXT + "\n");
				} else {
					LOG_TEXT.append("└" + TEXT + "\n");
				}
			}
			System.out.println(LOG_TEXT.toString());

			//検索機能
			if(MESSAGE_CONTENT.startsWith("検索 ")){
				com.rumisystem.rumiabot.jda.FUNCTION.SEARCH.main(E);
			}

			//日付
			if(MESSAGE_CONTENT.equals("時間")){
				LocalDateTime DATE = LocalDateTime.now();
				E.getMessage().reply("現在の時刻は、\n" + DATE.format(DateTimeFormatter.ofPattern("yyyy年MM月dd日E曜日 h時m分s秒"))).queue();
			}

			//メンション時(everyoneじゃないなら実行)
			if(!E.getMessage().getMentions().mentionsEveryone()){
				for(IMentionable MENTION:E.getMessage().getMentions().getMentions()){
					//自分にメンションされたか
					if(MENTION.getId().equals(BOT.getSelfUser().getId())){
						if(E.getMessage().getReferencedMessage() != null){
							E.getMessage().reply("おんこ").queue();
						} else {
							if(MESSAGE_CONTENT.endsWith("お") || MESSAGE_CONTENT.endsWith("オ")){
								E.getMessage().reply("...").queue();
								return;
							}
							E.getMessage().reply("なに？").queue();
						}
					}
				}
			}

			if(E.isFromGuild()){
				int I = 0;
				for(Message.Attachment FILE:E.getMessage().getAttachments()){
					File AUTHOR_FOLDER = new File("./DOWNLOAD/MSG_FILES/" + E.getAuthor().getId());
					if(!AUTHOR_FOLDER.exists()){
						AUTHOR_FOLDER.mkdir();
					}

					File GUILD_FOLDER = new File("./DOWNLOAD/MSG_FILES/" + E.getAuthor().getId() + "/" + E.getGuild().getId());
					if(!GUILD_FOLDER.exists()){
						GUILD_FOLDER.mkdir();
					}

					File CHANNEL_FOLDER = new File("./DOWNLOAD/MSG_FILES/" + E.getAuthor().getId() + "/" + E.getGuild().getId() + "/" + E.getChannel().getId());
					if(!CHANNEL_FOLDER.exists()){
						CHANNEL_FOLDER.mkdir();
					}

					new HTTP_REQUEST(FILE.getUrl()).DOWNLOAD("./DOWNLOAD/MSG_FILES/" + E.getAuthor().getId() + "/" + E.getGuild().getId() + "/" + E.getChannel().getId() + "/" + E.getMessage().getId() + "_" + I + "." + FILE.getFileExtension());

					I++;
				}
			}
		} catch (Exception EX) {
			EX.printStackTrace();
		}
	}

	@Override
	public void onSlashCommandInteraction(SlashCommandInteractionEvent INTERACTION){
		try{
			System.out.println(
					"[ INFO ][ SL ]┌Interaction create:" +
							INTERACTION.getName() +
							"\n             ├in " +
							INTERACTION.getGuild().getName() +
							"\n             ├in " +
							INTERACTION.getChannel().getName() +
							INTERACTION.getCommandId() +
							"\n             └in " +
							INTERACTION.getMember().getUser().getName() +
							"(" +
							INTERACTION.getMember().getId() +
							")"
			);

			//ユーザーに待ってもらう
			INTERACTION.deferReply().queue();

			//TODO:ヘルプコマンドを復旧する
			//TODO:sns_set sns_removeを復旧する
			//TODO:SETTINGを復旧する
			//TODO:WH_CLEARを復旧する
			//TODO:VOICEVOXを復旧する

			switch (INTERACTION.getName()){
				case "test":{
					test.main(INTERACTION);
					break;
				}

				case "ip":{
					ip.main(INTERACTION);
					break;
				}

				case "info_server":{
					info_server.main(INTERACTION);
					break;
				}

				case "info_user":{
					info_user.main(INTERACTION);
					break;
				}

				case "ws":{
					ws.main(INTERACTION);
					break;
				}

				case "mazokupic":{
					mazokupic.main(INTERACTION);
					break;
				}

				case "ping":{
					ping.main(INTERACTION);
					break;
				}

				default:{
					INTERACTION.getHook().editOriginal("？").queue();
				}
			}
		}catch (Exception EX){
			EX.printStackTrace();
		}
	}

	@Override//鯖に参加
	public void onGuildJoin(GuildJoinEvent E){
		TextChannel CH = BOT.getTextChannelById("1128742498194444298");
		if(CH != null){
			CH.sendMessage( E.getGuild().getName().replace("@", "AD") + "に参加しました！\n" +
					"これで" + BOT.getGuilds().size() + "個の鯖に参加しました。").queue();
		}
	}

	@Override
	public void onGuildLeave(GuildLeaveEvent E){
		TextChannel CH = BOT.getTextChannelById("1128742498194444298");
		if(CH != null){
			CH.sendMessage( E.getGuild().getName().replace("@", "AD") + "から叩き出されました。。。\n" +
					"これで" + BOT.getGuilds().size() + "個の鯖になりました").queue();
		}
	}
}
