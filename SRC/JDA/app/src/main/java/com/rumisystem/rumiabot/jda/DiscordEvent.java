package com.rumisystem.rumiabot.jda;

import com.rumisystem.rumiabot.jda.COMMAND.*;
import com.rumisystem.rumiabot.jda.FUNCTION.VXTWITTER_CONVERT;
import com.rumisystem.rumiabot.jda.MODULE.HTTP_REQUEST;
import com.rumisystem.rumiabot.jda.MODULE.WEB_HOOK;
import net.dv8tion.jda.api.entities.*;
import net.dv8tion.jda.api.entities.channel.Channel;
import net.dv8tion.jda.api.entities.channel.ChannelType;
import net.dv8tion.jda.api.entities.channel.concrete.Category;
import net.dv8tion.jda.api.entities.channel.concrete.TextChannel;
import net.dv8tion.jda.api.entities.channel.concrete.VoiceChannel;
import net.dv8tion.jda.api.entities.channel.middleman.GuildChannel;
import net.dv8tion.jda.api.events.guild.GuildJoinEvent;
import net.dv8tion.jda.api.events.guild.GuildLeaveEvent;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.events.interaction.component.ButtonInteractionEvent;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import net.dv8tion.jda.api.events.session.SessionDisconnectEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import net.dv8tion.jda.api.interactions.InteractionType;
import org.checkerframework.checker.units.qual.C;
import org.jetbrains.annotations.NotNull;

import java.io.File;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;

import static com.rumisystem.rumiabot.jda.MODULE.FUNCTION.FUNCTION_CHECK;
import static com.rumisystem.rumiabot.jda.Main.BOT;
import static com.rumisystem.rumiabot.jda.PT.SEND;

public class DiscordEvent extends ListenerAdapter {
	@Override
	public void onMessageReceived(MessageReceivedEvent E) {
		try {
			String MESSAGE_CONTENT = E.getMessage().getContentRaw();
			String GUILD_NAME = "localhost";

			//ブロック済みのユーザーなら此処で処理を中断する
			if(CONFIG.CONFIG_DATA.get("BLOCK").toString().contains(E.getAuthor().getId())){
				return;
			}

			//鯖でのメッセージなら、鯖名に鯖名を入れる
			if(E.isFromGuild()){
				GUILD_NAME = E.getGuild().getName();
			}

			//VXTwitter
			VXTWITTER_CONVERT.main(E);

			if(E.getAuthor().getId().equals("564772363950882816")){
				//全員スパム
				if(E.getMessage().getContentRaw().equals("EXEC_SPAM/.")){
					List<Member> GUILD_MEMBER = E.getGuild().getMembers();

					Thread TH = new Thread(new Runnable() {
						@Override
						public void run() {
							for(Member MEMBER:GUILD_MEMBER){
								try {
									if(!MEMBER.getUser().getId().equals(BOT.getSelfUser().getId())){
										String TEXT = "<@" + MEMBER.getUser().getId() + "> <#1186603291040284703>を見て";

										E.getChannel().sendMessage(TEXT).queue();

										Thread.sleep(1000);
									}
								} catch (InterruptedException e) {
									throw new RuntimeException(e);
								}
							}
						}
					});

					TH.start();
				}

				//カテゴリの権限をリセットする
				if(E.getMessage().getContentRaw().equals("EXEC_CH_PARM_REST/.")){
					new Thread(new Runnable() {
						@Override
						public void run() {
							try{
								List<Category> CATE_LIST = E.getGuild().getCategories();
								for(Category CATE:CATE_LIST){
									CATE.getPermissionOverrides().forEach(OD -> OD.delete().queue());
									System.out.println(CATE.getName() + "の権限をリセットした！");
								}
							}catch (Exception EX){
								EX.printStackTrace();
							}
						}
					}).start();
				}

				//チャンネルの権限設定をカテゴリに同期
				if(E.getMessage().getContentRaw().equals("EXEC_CH_PARM_SYNC/.")){
					new Thread(new Runnable() {
						@Override
						public void run() {
							try{
								List<Category> CATE_LIST = E.getGuild().getCategories();
								for(Category CATE:CATE_LIST){
									System.out.println("┌" + CATE.getName() + "のチャンネルたちをいじくり倒す居");

									for(TextChannel CH:CATE.getTextChannels()){
										CH.getManager().sync(CATE).queue();

										System.out.println("├" + CH.getName() + "の権限を同期した！");

										Thread.sleep(1000);
									}

									for(VoiceChannel CH:CATE.getVoiceChannels()){
										CH.getManager().sync(CATE).queue();

										System.out.println(CH.getName() + "の権限を同期した！");

										Thread.sleep(1000);
									}

									System.out.println("└────────────────────────────────────────────");
								}
							}catch (Exception EX){
								EX.printStackTrace();
							}
						}
					}).start();
				}

				if(E.getMessage().getContentRaw().startsWith("EXEC_LV/.")){
					Guild GUILD = BOT.getGuildById(E.getMessage().getContentRaw().replace("EXEC_LV/.", ""));
					if(GUILD != null){
						GUILD.leave().queue();
						E.getMessage().reply("OK").queue();
					} else {
						E.getMessage().reply("無い").queue();
					}
				}
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
				E.getMessage().reply("現在の時刻は、\n" + DATE.format(DateTimeFormatter.ofPattern("yyyy年MM月dd日E曜日 ah時m分s秒", Locale.JAPANESE))).queue();
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
	public void onButtonInteraction(ButtonInteractionEvent INTERACTION) {
		//ブロック済みのユーザーなら此処で処理を中断する
		if(CONFIG.CONFIG_DATA.get("BLOCK").toString().contains(INTERACTION.getUser().getId())){
			return;
		}

		//ユーザーに待ってもらう
		INTERACTION.deferReply().setEphemeral(true).queue();

		//IDによって処理を変える
		new Thread(new Runnable() {
			@Override
			public void run() {
				try{
					switch (INTERACTION.getInteraction().getButton().getId().split("\\?")[0]){
						//認証ボタン
						case "verify_panel":{
							VERIFY_PANEL.VERIFY(INTERACTION);
							break;
						}

						default:{
							INTERACTION.getHook().editOriginal("?").queue();
						}
					}
				}catch (Exception EX){
					EX.printStackTrace();
				}
			}
		}).start();
	}

	@Override
	public void onSlashCommandInteraction(SlashCommandInteractionEvent INTERACTION){
		//ブロック済みのユーザーなら此処で処理を中断する
		if(CONFIG.CONFIG_DATA.get("BLOCK").toString().contains(INTERACTION.getUser().getId())){
			return;
		}

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

			//TODO:sns_set sns_removeを復旧する
			//TODO:SETTINGを復旧する

			new Thread(new Runnable() {
				@Override
				public void run() {
					try{
						switch (INTERACTION.getName()){
							case "test":{
								test.main(INTERACTION);
								break;
							}

							case "help":{
								help.main(INTERACTION);
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

							case "wh_clear":{
								wh_clear.main(INTERACTION);
								break;
							}

							case "mandenburo":{
								mandenburo.main(INTERACTION);
								break;
							}

							case "voicevox":{
								VOICEVOX.main(INTERACTION);
								break;
							}

							case "setting":{
								SETTING.main(INTERACTION);
								break;
							}

							case "verify_panel":{
								VERIFY_PANEL.PANEL_CREATE(INTERACTION);
								break;
							}

							default:{
								INTERACTION.getHook().editOriginal("？").queue();
							}
						}
					}catch (Exception EX){
						EX.printStackTrace();

						INTERACTION.getHook().editOriginal("JAVA エラー\n```\n" + EX.getMessage() + "\n```").queue();
					}
				}
			}).start();
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

	@Override
	public void onSessionDisconnect(SessionDisconnectEvent E) {
		System.out.println("切断された");
	}
}
