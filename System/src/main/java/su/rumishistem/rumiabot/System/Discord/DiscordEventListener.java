package su.rumishistem.rumiabot.System.Discord;

import static su.rumishistem.rumiabot.System.Main.FunctionModuleList;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Locale;

import static su.rumishistem.rumiabot.System.Main.DISCORD_BOT;

import net.dv8tion.jda.api.entities.channel.concrete.TextChannel;
import net.dv8tion.jda.api.events.guild.GuildJoinEvent;
import net.dv8tion.jda.api.events.guild.GuildLeaveEvent;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import su.rumishistem.rumi_java_lib.ArrayNode;
import su.rumishistem.rumi_java_lib.EXCEPTION_READER;
import su.rumishistem.rumi_java_lib.SQL;
import su.rumishistem.rumiabot.System.MODULE.SearchCommand;
import su.rumishistem.rumiabot.System.MODULE.UserBlockCheck;
import su.rumishistem.rumiabot.System.TYPE.CommandData;
import su.rumishistem.rumiabot.System.TYPE.CommandInteraction;
import su.rumishistem.rumiabot.System.TYPE.FunctionClass;
import su.rumishistem.rumiabot.System.TYPE.MessageData;
import su.rumishistem.rumiabot.System.TYPE.MessageUser;
import su.rumishistem.rumiabot.System.TYPE.ReceiveMessageEvent;
import su.rumishistem.rumiabot.System.TYPE.SourceType;

public class DiscordEventListener extends ListenerAdapter {
	@Override
	public void onMessageReceived(MessageReceivedEvent E) {
		//ブロック済みのユーザーなら此処で処理を中断する
		if (!UserBlockCheck.isBlock(E.getAuthor().getId())) {
			//イベント着火
			for (FunctionClass Function:FunctionModuleList) {
				Function.ReceiveMessage(new ReceiveMessageEvent(
					SourceType.Discord,
					new MessageUser(),
					new MessageData(
						E.getMessageId(),
						E.getMessage().getContentRaw(),
						E.getMessage(),
						null,
						E.getMessage().getContentRaw().contains("<@" + DISCORD_BOT.getSelfUser().getId() + ">")
					)
				));
			}
		}
	}

	@Override
	public void onSlashCommandInteraction(SlashCommandInteractionEvent INTERACTION){
		try {
			//ブロック済みのユーザーなら此処で処理を中断する
			if (!UserBlockCheck.isBlock(INTERACTION.getUser().getId())) {
				CommandData Command = SearchCommand.Command(INTERACTION.getName());
				FunctionClass Function = SearchCommand.Function(INTERACTION.getName());
				if (Command != null && Function != null) {
					if (!Command.isPrivate()) {
						INTERACTION.deferReply().queue();
					}

					Function.RunCommand(new CommandInteraction(SourceType.Discord, INTERACTION, Command));
				} else {
					INTERACTION.reply("コマンドか機能が見つかりませんでした").queue();
				}
			} else {
				INTERACTION.reply("帰れ").queue();
			}
		} catch (Exception EX) {
			String EX_TEXT = EXCEPTION_READER.READ(EX);
			INTERACTION.getHook().editOriginal("エラー\n```\n" + EX_TEXT + "\n```").queue();
			EX.printStackTrace();
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
							TEXT.append("理由：" + INFO.getData("RESON").asString() + "\n");
							TEXT.append("そのため、勝手ながら脱退させていただきます、さようなら。\n");

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

				if(CH != null){
					CH.sendMessage("残念ながらブラックリスト入りしていたサーバーでした、脱退します").queue();
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
