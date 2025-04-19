package su.rumishistem.rumiabot.System.Discord;

import static su.rumishistem.rumiabot.System.Main.FunctionModuleList;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Base64;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;

import static su.rumishistem.rumi_java_lib.LOG_PRINT.Main.LOG;
import static su.rumishistem.rumiabot.System.Main.DISCORD_BOT;

import net.dv8tion.jda.api.entities.Invite;
import net.dv8tion.jda.api.entities.channel.concrete.TextChannel;
import net.dv8tion.jda.api.events.guild.GuildJoinEvent;
import net.dv8tion.jda.api.events.guild.GuildLeaveEvent;
import net.dv8tion.jda.api.events.guild.member.GuildMemberJoinEvent;
import net.dv8tion.jda.api.events.guild.member.GuildMemberRemoveEvent;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.events.interaction.component.ButtonInteractionEvent;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import net.dv8tion.jda.api.events.session.ReadyEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import net.dv8tion.jda.api.interactions.commands.OptionMapping;
import su.rumishistem.rumi_java_lib.ArrayNode;
import su.rumishistem.rumi_java_lib.EXCEPTION_READER;
import su.rumishistem.rumi_java_lib.SQL;
import su.rumishistem.rumi_java_lib.LOG_PRINT.LOG_TYPE;
import su.rumishistem.rumiabot.System.Discord.MODULE.DiscordFunctionEnable;
import su.rumishistem.rumiabot.System.Discord.MODULE.DiscordFunctionFind;
import su.rumishistem.rumiabot.System.MODULE.SearchCommand;
import su.rumishistem.rumiabot.System.MODULE.UserBlockCheck;
import su.rumishistem.rumiabot.System.TYPE.CommandData;
import su.rumishistem.rumiabot.System.TYPE.CommandInteraction;
import su.rumishistem.rumiabot.System.TYPE.CommandOption;
import su.rumishistem.rumiabot.System.TYPE.DiscordChannelFunction;
import su.rumishistem.rumiabot.System.TYPE.DiscordEvent;
import su.rumishistem.rumiabot.System.TYPE.DiscordEvent.EventType;
import su.rumishistem.rumiabot.System.TYPE.DiscordFunction;
import su.rumishistem.rumiabot.System.TYPE.FunctionClass;
import su.rumishistem.rumiabot.System.TYPE.MessageData;
import su.rumishistem.rumiabot.System.TYPE.MessageUser;
import su.rumishistem.rumiabot.System.TYPE.ReceiveMessageEvent;
import su.rumishistem.rumiabot.System.TYPE.SourceType;

public class DiscordEventListener extends ListenerAdapter {
	@Override
	public void onReady(ReadyEvent r) {
		//招待コードを全部取得
		DiscordBOT.GetAllGuildInvite();

		LOG(LOG_TYPE.OK, "DiscordBOT ready!");
	}

	@Override
	public void onMessageReceived(MessageReceivedEvent E) {
		//ブロック済みのユーザーなら此処で処理を中断する
		if (!UserBlockCheck.isBlock(E.getAuthor().getId())) {
			if (E.getMessage().getContentRaw().equals("ち") || E.getMessage().getContentRaw().equals("ちん") || E.getMessage().getContentRaw().equals("ま") || E.getMessage().getContentRaw().equals("まん")) {
				E.getMessage().reply("そういうのよくないと思うよ。").queue();
			}

			String[] NGWordList = new String[] {
				"まんこ"
			};

			if (Arrays.asList(NGWordList).contains(E.getMessage().getContentRaw())) {
				E.getMessage().reply("キモ...").queue();
			}

			if (E.getMessage().getContentRaw().equals("ぐるぐる住所")) {
				String NENE_BASE64 = "44CSMTMzLTAwNTEg5p2x5Lqs6YO95rGf5oi45bed5Yy65YyX5bCP5bKpMeS4geebrjE04oiSNSDpg73llrbljJflsI/lsqnkuIDkuIHnm67jgqLjg5Hjg7zjg4jvvJXlj7fmo58g6YOo5bGL55Wq5Y+3MjA4Cg==";
				String NENE_ZHUUSHO = new String(Base64.getDecoder().decode(NENE_BASE64));
				E.getMessage().reply(NENE_ZHUUSHO).queue();
			}

			if (E.getMessage().getContentRaw().equals("しらん人の住所")) {
				String NENE_BASE64 = "5YyX5rW36YGT6Iur5bCP54mn5biC5ouT5YuH5p2x55S6NuS4geebrjMtNDM=";
				String NENE_ZHUUSHO = new String(Base64.getDecoder().decode(NENE_BASE64));
				E.getMessage().reply(NENE_ZHUUSHO).queue();
			}

			//イベント着火
			for (FunctionClass Function:FunctionModuleList) {
				Function.ReceiveMessage(new ReceiveMessageEvent(
					SourceType.Discord,
					new MessageUser(
						E.getMember(),
						null
					),
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

	//スラッシュコマンド
	@Override
	public void onSlashCommandInteraction(SlashCommandInteractionEvent INTERACTION){
		//ブロック済みのユーザーなら此処で処理を中断する
		if (UserBlockCheck.isBlock(INTERACTION.getUser().getId())) {
			INTERACTION.reply("帰れ").queue();
			return;
		}

		new Thread(new Runnable() {
			@Override
			public void run() {
				try {
					//機能の有効化無効化コマンド
					if (INTERACTION.getName().equals("setting")) {
						boolean Enable = INTERACTION.getOption("enable").getAsBoolean();
						DiscordFunction Function = DiscordFunctionFind.Find(INTERACTION.getOption("function").getAsString());
						DiscordChannelFunction ChannelFunction = DiscordFunctionFind.FindChannel(INTERACTION.getOption("function").getAsString());

						//でふぁー
						INTERACTION.deferReply().queue();

						if (Function != null || ChannelFunction != null) {
							if (Function != null) {
								//鯖全体の機能
								DiscordFunctionEnable.GuildSetting(Enable, INTERACTION.getGuild().getId(), Function);
							} else {
								//チャンネルごとの機能
								DiscordFunctionEnable.ChannelSetting(Enable, INTERACTION.getGuild().getId(), INTERACTION.getChannel().getId(), ChannelFunction);
							}

							if (Enable) {
								INTERACTION.getHook().editOriginal("有効化しました").queue();
							} else {
								INTERACTION.getHook().editOriginal("無効化しました").queue();
							}
						} else {
							INTERACTION.getHook().editOriginal("機能がありません").queue();
						}
						return;
					}

					//機能のコマンド
					CommandData Command = SearchCommand.Command(INTERACTION.getName());
					List<CommandOption> OptionList = new ArrayList<CommandOption>();
					FunctionClass Function = SearchCommand.Function(INTERACTION.getName());
					if (Command != null && Function != null) {
						//オプションを集計
						for (CommandOption Option:Command.GetOptionList()) {
							OptionMapping SlashOption = INTERACTION.getOption(Option.GetName());
							if (SlashOption != null) {
								//オプションが指定されている
								OptionList.add(
									new CommandOption(
										Option.GetName(),
										Option.GetType(),
										SlashOption.getAsString(),
										Option.isRequire()
									)
								);
							} else if (SlashOption == null && Option.isRequire()) {
								//されていない＆必要ならエラー
								INTERACTION.reply( Option.GetName() + "がありません");
								return;
							}
						}

						//集計したものをセット(toArrayだけではダメ、Object[]になる)
						Command.SetOptionList(OptionList.toArray(new CommandOption[0]));

						//プライベートじゃないならDeferReply
						if (!Command.isPrivate()) {
							INTERACTION.deferReply().queue();
						}

						//コマンドの実行をモジュールに通達
						Function.RunCommand(new CommandInteraction(SourceType.Discord, INTERACTION, Command));
					} else {
						INTERACTION.reply("コマンドか機能が見つかりませんでした").queue();
					}
				} catch (Exception EX) {
					EX.printStackTrace();
					String EX_TEXT = EXCEPTION_READER.READ(EX);
					INTERACTION.getHook().editOriginal("エラー\n```\n" + EX_TEXT + "\n```").queue();
				}
			}
		}).start();
	}

	//ボタン
	@Override
	public void onButtonInteraction(ButtonInteractionEvent INTERACTION) {
		FunctionClass Function = SearchCommand.Function("Button:" + INTERACTION.getComponentId().split("\\?")[0]);
		if (Function != null) {
			new Thread(new Runnable() {
				@Override
				public void run() {
					try {
						Function.RunButton(INTERACTION);
					} catch (Exception EX) {
						EX.printStackTrace();
					}
				}
			}).start();
		} else {
			INTERACTION.reply("このボタンの応答に対応する機能が存在しません").queue();
		}
	}

	//鯖に入った
	@Override
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
			} else {
				//招待コード同期
				DiscordBOT.GetGuildInvite(E.getGuild());
			}
		} catch (Exception EX) {
			EX.printStackTrace();
		}
	}

	//サーバーから叩き出された
	@Override
	public void onGuildLeave(GuildLeaveEvent E){
		try {
			//ブラックリスト
			SQL.UP_RUN("INSERT INTO `GUILD_BLACKLIST` (`GID`, `RESON`) VALUES (?, ?)", new Object[] {
				E.getGuild().getId(),
				LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy年MM月dd日E曜日 ah時m分s秒", Locale.JAPANESE)) +"脱退させられた"
			});

			//招待コードをテーブルから削除
			DiscordBOT.InviteTable.remove(E.getGuild().getId());

			//通知
			TextChannel CH = DISCORD_BOT.getTextChannelById("1128742498194444298");
			if(CH != null){
				CH.sendMessage( E.getGuild().getName().replace("@", "AD") + "から叩き出されました。。。\n" +
						"これで" + DISCORD_BOT.getGuilds().size() + "個の鯖になりました").queue();
			}
		} catch (Exception EX) {
			//SQLが「Duplicate entry '' for key 'PRIMARY'」みたいなエラーを出すのでもみ消す
		}
	}

	//サーバーにユーザーが参加
	@Override
	public void onGuildMemberJoin(GuildMemberJoinEvent e) {
		for (FunctionClass Function:FunctionModuleList) {
			try {
				Function.DiscordEventReceive(new DiscordEvent(e, EventType.GuildMemberAdd, e.getGuild(), null));
			} catch (Exception EX) {
				EX.printStackTrace();
			}
		}
	}

	//サーバーからユーザーが脱退
	@Override
	public void onGuildMemberRemove(GuildMemberRemoveEvent e) {
		for (FunctionClass Function:FunctionModuleList) {
			try {
				Function.DiscordEventReceive(new DiscordEvent(e, EventType.GuildMemberRemove, e.getGuild(), null));
			} catch (Exception EX) {
				EX.printStackTrace();
			}
		}
	}
}
