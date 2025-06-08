package su.rumishistem.rumiabot.System.Discord;

import static su.rumishistem.rumiabot.System.Main.FunctionModuleList;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import static su.rumishistem.rumi_java_lib.LOG_PRINT.Main.LOG;
import static su.rumishistem.rumiabot.System.Main.DISCORD_BOT;
import static su.rumishistem.rumiabot.System.Main.CommandList;
import static su.rumishistem.rumiabot.System.Main.DiscordContextmenuList;

import net.dv8tion.jda.api.entities.User;
import net.dv8tion.jda.api.entities.channel.concrete.TextChannel;
import net.dv8tion.jda.api.events.guild.GuildJoinEvent;
import net.dv8tion.jda.api.events.guild.GuildLeaveEvent;
import net.dv8tion.jda.api.events.guild.member.GuildMemberJoinEvent;
import net.dv8tion.jda.api.events.guild.member.GuildMemberRemoveEvent;
import net.dv8tion.jda.api.events.guild.voice.GuildVoiceUpdateEvent;
import net.dv8tion.jda.api.events.interaction.ModalInteractionEvent;
import net.dv8tion.jda.api.events.interaction.command.MessageContextInteractionEvent;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.events.interaction.component.ButtonInteractionEvent;
import net.dv8tion.jda.api.events.interaction.component.EntitySelectInteractionEvent;
import net.dv8tion.jda.api.events.interaction.component.StringSelectInteractionEvent;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import net.dv8tion.jda.api.events.session.ReadyEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import net.dv8tion.jda.api.interactions.commands.OptionMapping;
import net.dv8tion.jda.api.interactions.commands.OptionType;
import net.dv8tion.jda.api.interactions.commands.build.Commands;
import net.dv8tion.jda.api.interactions.commands.build.OptionData;
import net.dv8tion.jda.api.interactions.commands.build.SlashCommandData;
import su.rumishistem.rumi_java_lib.ArrayNode;
import su.rumishistem.rumi_java_lib.EXCEPTION_READER;
import su.rumishistem.rumi_java_lib.SQL;
import su.rumishistem.rumi_java_lib.LOG_PRINT.LOG_TYPE;
import su.rumishistem.rumi_java_lib.REON4213.REON4213Parser;
import su.rumishistem.rumi_java_lib.REON4213.Type.VBlock;
import su.rumishistem.rumiabot.System.Discord.MODULE.DiscordFunctionEnable;
import su.rumishistem.rumiabot.System.Discord.MODULE.DiscordFunctionFind;
import su.rumishistem.rumiabot.System.MODULE.AdminManager;
import su.rumishistem.rumiabot.System.MODULE.BlockManager;
import su.rumishistem.rumiabot.System.MODULE.SearchCommand;
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
import su.rumishistem.rumiabot.System.TYPE.ReturnInteractionEvent;
import su.rumishistem.rumiabot.System.TYPE.RunInteractionEvent;
import su.rumishistem.rumiabot.System.TYPE.RunInteractionEvent.InteractionType;
import su.rumishistem.rumiabot.System.TYPE.SourceType;

public class DiscordEventListener extends ListenerAdapter {
	@Override
	public void onReady(ReadyEvent r) {
		//招待コードを全部取得
		DiscordBOT.GetAllGuildInvite();

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

					case Role: {
						Type = OptionType.ROLE;
						break;
					}

					case User: {
						Type = OptionType.USER;
						break;
					}

					case File: {
						Type = OptionType.ATTACHMENT;
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

		//機能設定用コマンド
		SlashCommandList.add(GenFunctionSettingCommand());

		//スラッシュコマンド登録
		DISCORD_BOT.updateCommands().addCommands(SlashCommandList).addCommands(DiscordContextmenuList).queue();
		LOG(LOG_TYPE.OK, "DiscordBOT:" + SlashCommandList.size() + "個のスラッシュコマンドを登録しました");

		for (FunctionClass Function:FunctionModuleList) {
			try {
				Function.DiscordEventReceive(new DiscordEvent(r, EventType.BOTReady, null, null));
			} catch (Exception EX) {
				EX.printStackTrace();
			}
		}

		LOG(LOG_TYPE.OK, "DiscordBOT ready!");
	}

	private static SlashCommandData GenFunctionSettingCommand() {
		SlashCommandData Command = Commands.slash("setting", "機能を設定します");

		//機能一覧
		OptionData FunctionOption = new OptionData(OptionType.STRING, "function", "機能", true);
		for (DiscordFunction Function:DiscordFunction.values()) {
			FunctionOption.addChoice(Function.name(), Function.name());
		}
		for (DiscordChannelFunction Function:DiscordChannelFunction.values()) {
			FunctionOption.addChoice(Function.name(), Function.name());
		}
		Command.addOptions(FunctionOption);

		//有効化無効化
		OptionData EnableOption = new OptionData(OptionType.BOOLEAN, "enable", "有効化無効化", true);
		Command.addOptions(EnableOption);

		return Command;
	}

	@Override
	public void onMessageReceived(MessageReceivedEvent E) {
		//ブロック済みのユーザーなら此処で処理を中断する
		if (BlockManager.IsBlocked(SourceType.Discord, E.getAuthor().getId())) {
			return;
		}

		try {
			//管理者コマンド
			if (AdminManager.IsAdmin(SourceType.Discord, E.getMember().getUser().getId())) {
				String Content = E.getMessage().getContentRaw();
				REON4213Parser P = new REON4213Parser(Content);
				if (P.GetHacudouShi() != null) {
					if (P.GetCls().get("RB") != null) {
						for (VBlock V:P.GetCls().get("RB")) {
							switch (V.GetVerb()) {
								case "Block": {
									User U = DISCORD_BOT.getUserById(V.GetObject());
									if (U == null) {
										E.getMessage().reply(V.GetObject() + "というユーザーが見つからなかった").queue();
										return;
									}

									BlockManager.addBlock(SourceType.Discord, U.getId());

									E.getMessage().reply(V.GetObject() + "をブロックした").queue();
									return;
								}

								default: {
									E.getMessage().reply("未定義動作").queue();
									return;
								}
							}
						}
					}
				}

				//help用
				if (E.getMessage().getContentRaw().equals("adminhelp")) {
					StringBuilder SB = new StringBuilder();
					SB.append("Queli->{Cls(主語){EX[動詞]->{目的語};};}->ExeC->{発動子};").append("\n");
					SB.append("\n");
					SB.append("# 動詞").append("\n");
					SB.append("Block：ユーザーをブロックします(目的語にはユーザーIDを)").append("\n");

					E.getMessage().reply(SB.toString()).queue();
				}
			}

			//イベント着火
			for (FunctionClass Function:FunctionModuleList) {
				Function.ReceiveMessage(new ReceiveMessageEvent(
					SourceType.Discord,
					new MessageUser(
						E.getMember(),
						null
					),
					E.getMember(),
					E.getChannel().asTextChannel(),
					new MessageData(
						E.getMessageId(),
						E.getMessage().getContentRaw(),
						E.getMessage(),
						null,
						E.getMessage().getContentRaw().contains("<@" + DISCORD_BOT.getSelfUser().getId() + ">")
					)
				));
			}
		} catch (Exception EX) {
			EX.printStackTrace();
		}
	}

	//スラッシュコマンド
	@Override
	public void onSlashCommandInteraction(SlashCommandInteractionEvent INTERACTION){
		//ブロック済みのユーザーなら此処で処理を中断する
		if (BlockManager.IsBlocked(SourceType.Discord, INTERACTION.getUser().getId())) {
			INTERACTION.reply("帰れ").setEphemeral(true).queue();
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
		//ブロック済みのユーザーなら此処で処理を中断する
		if (BlockManager.IsBlocked(SourceType.Discord, INTERACTION.getUser().getId())) {
			INTERACTION.reply("帰れ").setEphemeral(true).queue();
			return;
		}

		FunctionClass Function = SearchCommand.Function("Button:" + INTERACTION.getComponentId().split("\\?")[0]);
		if (Function != null) {
			new Thread(new Runnable() {
				@Override
				public void run() {
					try {
						Function.RunInteraction(new RunInteractionEvent(INTERACTION, InteractionType.Button));
					} catch (Exception EX) {
						EX.printStackTrace();
					}
				}
			}).start();
		} else {
			INTERACTION.reply("このボタンの応答に対応する機能が存在しません").queue();
		}
	}

	@Override
	public void onMessageContextInteraction(MessageContextInteractionEvent INTERACTION) {
		//ブロック済みのユーザーなら此処で処理を中断する
		if (BlockManager.IsBlocked(SourceType.Discord, INTERACTION.getUser().getId())) {
			INTERACTION.reply("帰れ").setEphemeral(true).queue();
			return;
		}

		FunctionClass Function = SearchCommand.Function("Message:" + INTERACTION.getName().split("\\?")[0]);
		if (Function != null) {
			new Thread(new Runnable() {
				@Override
				public void run() {
					try {
						Function.RunInteraction(new RunInteractionEvent(INTERACTION, InteractionType.MessageContext));
					} catch (Exception EX) {
						EX.printStackTrace();
					}
				}
			}).start();
		} else {
			INTERACTION.reply("このボタンの応答に対応する機能が存在しません").queue();
		}
	}

	@Override
	public void onModalInteraction(ModalInteractionEvent INTERACTION) {
		//ブロック済みのユーザーなら此処で処理を中断する
		if (BlockManager.IsBlocked(SourceType.Discord, INTERACTION.getUser().getId())) {
			INTERACTION.reply("帰れ").setEphemeral(true).queue();
			return;
		}

		FunctionClass Function = SearchCommand.Function("Modal:" + INTERACTION.getModalId().split("\\?")[0]);
		if (Function != null) {
			new Thread(new Runnable() {
				@Override
				public void run() {
					try {
						Function.ReturnInteraction(new ReturnInteractionEvent(INTERACTION, su.rumishistem.rumiabot.System.TYPE.ReturnInteractionEvent.InteractionType.Modal));
					} catch (Exception EX) {
						EX.printStackTrace();
					}
				}
			}).start();
		} else {
			INTERACTION.reply("このボタンの応答に対応する機能が存在しません").queue();
		}
	}

	@Override
	public void onEntitySelectInteraction(EntitySelectInteractionEvent INTERACTION) {
		//ブロック済みのユーザーなら此処で処理を中断する
		if (BlockManager.IsBlocked(SourceType.Discord, INTERACTION.getUser().getId())) {
			INTERACTION.reply("帰れ").setEphemeral(true).queue();
			return;
		}

		FunctionClass Function = SearchCommand.Function("EntitySelect:" + INTERACTION.getComponentId().split("\\?")[0]);
		if (Function != null) {
			new Thread(new Runnable() {
				@Override
				public void run() {
					try {
						Function.ReturnInteraction(new ReturnInteractionEvent(INTERACTION, su.rumishistem.rumiabot.System.TYPE.ReturnInteractionEvent.InteractionType.EntitySelector));
					} catch (Exception EX) {
						EX.printStackTrace();
					}
				}
			}).start();
		} else {
			INTERACTION.reply("このボタンの応答に対応する機能が存在しません").queue();
		}
	}

	@Override
	public void onStringSelectInteraction(StringSelectInteractionEvent INTERACTION) {
		//ブロック済みのユーザーなら此処で処理を中断する
		if (BlockManager.IsBlocked(SourceType.Discord, INTERACTION.getUser().getId())) {
			INTERACTION.reply("帰れ").setEphemeral(true).queue();
			return;
		}

		FunctionClass Function = SearchCommand.Function("StringSelect:" + INTERACTION.getComponentId().split("\\?")[0]);
		if (Function != null) {
			new Thread(new Runnable() {
				@Override
				public void run() {
					try {
						Function.ReturnInteraction(new ReturnInteractionEvent(INTERACTION, su.rumishistem.rumiabot.System.TYPE.ReturnInteractionEvent.InteractionType.StringSelector));
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

	//VCから出入りした
	@Override
	public void onGuildVoiceUpdate(GuildVoiceUpdateEvent e) {
		for (FunctionClass Function:FunctionModuleList) {
			try {
				Function.DiscordEventReceive(new DiscordEvent(e, EventType.VCMemberUpdate, e.getGuild(), null));
			} catch (Exception EX) {
				EX.printStackTrace();
			}
		}
	}
}
