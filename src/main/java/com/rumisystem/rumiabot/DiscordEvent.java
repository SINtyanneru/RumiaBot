package com.rumisystem.rumiabot;

import com.rumisystem.rumiabot.Command.*;
import com.rumisystem.rumiabot.GAME.NATION_FLAG;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.User;
import net.dv8tion.jda.api.entities.channel.concrete.TextChannel;
import net.dv8tion.jda.api.events.guild.GuildJoinEvent;
import net.dv8tion.jda.api.events.guild.member.GuildMemberJoinEvent;
import net.dv8tion.jda.api.events.guild.member.GuildMemberRemoveEvent;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.events.message.MessageDeleteEvent;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import net.dv8tion.jda.api.events.message.MessageUpdateEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;

import static com.rumisystem.rumiabot.Main.*;

public class DiscordEvent extends ListenerAdapter {
	@Override
	public void onMessageReceived(MessageReceivedEvent e) {
		try {
			String msg = e.getMessage().getContentRaw(); //入力されたメッセージを取得
			if(!e.getAuthor().equals(jda.getSelfUser())) {  //送信されたメッセージがBOTによるものではないか


				//Main.LOG_OUT(e.getGuild().getName() + "/" + e.getChannel().getName() + "\nSent msg:" + msg);

				if(msg.startsWith("r.setch")){
					setch.main(e);
				}
				/*
				if(msg.startsWith("r.msg")){
					msgc.Main(e);
				}
				 */

				if(msg.startsWith("r.shell")){
					SHELL_LINUX.Main(e);
				}

				if(msg.startsWith("検索 ")){
					SEARCH.Main(e);
				}

				if(msg.startsWith("r.role")){
					if(e.getAuthor().getId().equals("564772363950882816")){
						User selfUser = e.getJDA().getSelfUser();
						Member selfMember = e.getGuild().getMember(selfUser);

						//自分自身の権限を取得
						if(selfMember != null){
							e.getMessage().reply(selfMember.getPermissions().toString()).queue();
						}
					}
				}


				for(User MU : e.getMessage().getMentions().getUsers()){
					if(MU.getId().equals(Main.BOT_ID)){
						e.getChannel().sendTyping().queue();
						int RND = (int)Math.ceil(Math.random() * 4);
						switch(RND){
							case 1:
								e.getMessage().reply("はい").queue();
								break;
							case 2:
								e.getMessage().reply("ふーん。。。。").queue();
								break;
							case 3:
								e.getMessage().reply("そうですか。").queue();
								break;
							case 4:
								e.getMessage().reply("へー").queue();
								break;
						}
					}
				}

				//ゲーム
				if(msg.startsWith("r.国旗当てゲーム")){
					NATION_FLAG.Main(e);
				}

				//URL_GET.Main(e);
			}
		}catch (Exception ex){
			LOG_OUT("[ ERR ]" + ex.getMessage());
		}
	}

	@Override
	public void onSlashCommandInteraction(SlashCommandInteractionEvent e) {
		switch(e.getName()){
			case "test":
				test.Main(e);
				break;
			case "ping":
				PING.Main(e);
				break;
			case "invite":
				e.getInteraction().reply("[招待する](https://discord.com/api/oauth2/authorize?client_id=869887786491183125&permissions=8&scope=bot)").queue();
				break;
			case "help":
				help.Main(e);
				break;
			case "ws":
				ws.Main(e);
				break;
			case "update":
				update.Main(e);
				break;
			case "tanzania":
				tanzania.Main(e);
				break;
			case "shell":
				SHELL.Main(e);
				break;
			case "spam":
				if(e.getUser().getId().equals("564772363950882816")){
					SPAM.Main(e);
				}else {
					e.getInteraction().reply("るみさんしか使えません").queue();
				}
				break;
			case "spam_stop":
				if(e.getUser().getId().equals("564772363950882816")) {
					SPAM.EMAJEN_STOP(e);
				}else{
					e.getInteraction().reply("るみさんしか使えません").queue();
				}
				break;
			case "info":
				info.Main(e);
				break;
			case "rsget":
				RS_GET.Main(e);
				break;
			case "backup":
				backup.main(e);
				break;
		}
	}

	@Override
	public void onGuildMemberJoin(GuildMemberJoinEvent e) {
		try{
			// ユーザーが参加したときに実行されるコード
			System.out.println(e.getMember().getUser().getName() + " joined the server.");
		}catch (Exception ex){
			LOG_OUT("[ ERR ]" + ex.getMessage());
		}

	}

	//※GuildMemberLeaveEventは古いので使えない
	@Override
	public void onGuildMemberRemove(GuildMemberRemoveEvent e) {
		try{
			//ユーザーが離脱したときに実行されるコード(実験中)
			TextChannel TC = jda.getTextChannelById("894185240728322058");
			if(TC != null){
				if(e.getGuild().getId().equals("836142496563068929")){
					EmbedBuilder EB = new EmbedBuilder();
					EB.setColor(RUND_COLOR());
					EB.setTitle(e.getUser().getName() + "が鯖から抜けたわ");
					EB.setThumbnail(e.getUser().getAvatarUrl());
					EB.setDescription("残念だ、彼は自分に私生活が有ることを証明してしまった");
					TC.sendMessageEmbeds(EB.build()).queue();
				}else{
					LOG_OUT("[ G_LEAVE ]しらん鯖だなあ(" + e.getGuild().getName() + ")");
				}
			}else {
				LOG_OUT("[ ERR ]チャンネルが無いと");
			}
		}catch (Exception ex){
			LOG_OUT("[ ERR ]" + ex.getMessage());
		}
	}

	@Override
	public void onMessageDelete(MessageDeleteEvent E){
		try{
			TextChannel TC = jda.getTextChannelById("1140511350620168192");
			Guild SERVER = E.getGuild();

			if(TC != null){
				EmbedBuilder EB = new EmbedBuilder();
				EB.setColor(RUND_COLOR());
				EB.setTitle("メッセージが消された");
				EB.setDescription(E.getRawData().toString());
				if(SERVER != null){
					EB.addField("鯖名", SERVER.getName(), false);
				}
				TC.sendMessageEmbeds(EB.build()).queue();
			}else{
				LOG_OUT("メッセージの削除を検知:" + E.getRawData());
			}
		}catch (Exception ex){
			LOG_OUT("[ ERR ]" + ex.getMessage());
		}
	}

	@Override
	public void onGuildJoin(GuildJoinEvent e){
		TextChannel defaultChannel = e.getGuild().getSystemChannel();
		if (defaultChannel != null) {
			defaultChannel.sendMessage("にゃー\n(スラッシュコマンドを使用できます)\n(るみ鯖アカウントと連携させることができます)").queue();
		}
	}

	@Override
	public void onMessageUpdate(MessageUpdateEvent E){
		// メッセージが編集されたときの処理
		//LOG_OUT(E.getRawData().toString());
	}
}
