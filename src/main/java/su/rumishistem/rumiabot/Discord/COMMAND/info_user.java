package su.rumishistem.rumiabot.Discord.COMMAND;

import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.User;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import su.rumishistem.rumiabot.MODULE.DATE_FORMAT;

public class info_user {
	public static void Main(SlashCommandInteractionEvent IT) {
		User USER = IT.getUser();

		//ユーザーが指定されているなら、変数に其のユーザーを入れる
		if(IT.getOption("user") != null){
			USER = IT.getOption("user").getAsUser();
		}

		//Nullチェック
		if(USER != null){
			EmbedBuilder EB = new EmbedBuilder();
			EB.setTitle(USER.getGlobalName());
			EB.setDescription(USER.getName());
			EB.setThumbnail(USER.getAvatarUrl());

			EB.addField("ID", USER.getId(), true);

			//アカウント作成日
			EB.addField("アカウント作成日", DATE_FORMAT.ZHUUNI_H(USER.getTimeCreated()), true);

			//鯖で実行されたか
			if(IT.getGuild() != null){
				Member MEMBER = IT.getGuild().getMember(USER);
				if(MEMBER != null){
					//ちなみにJDAのバグで取得できないよ、カスだね
					EB.addField("鯖に参加した日付", DATE_FORMAT.ZHUUNI_H(MEMBER.getTimeJoined()), true);
				} else {
					EB.addField("鯖に参加した日付", "JDAのバグで取得できませんでした", true);
				}

				if(MEMBER.isBoosting()){
					EB.addField("鯖のブースト日付", DATE_FORMAT.ZHUUNI_H(MEMBER.getTimeBoosted()), true);
				} else {
					EB.addField("鯖のブースト日付", "そもそもブーストしてない", true);
				}

				if(MEMBER.getNickname() != null){
					EB.addField("ニックネーム", MEMBER.getNickname(), true);
				} else {
					EB.addField("ニックネーム", "無い", true);
				}
			}

			//BOTか
			if(USER.isBot()){
				EB.addField("BOTか", "はい", true);
			} else {
				EB.addField("BOTか", "いいえ", true);
			}

			IT.getHook().editOriginalEmbeds(EB.build()).queue();
		} else {
			IT.getHook().editOriginal("Nullです").queue();
		}
	}
}
