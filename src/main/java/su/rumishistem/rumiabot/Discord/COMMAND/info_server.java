package su.rumishistem.rumiabot.Discord.COMMAND;

import java.awt.Color;
import java.time.format.DateTimeFormatter;

import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.emoji.RichCustomEmoji;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import su.rumishistem.rumiabot.MODULE.DATE_FORMAT;

public class info_server {
	public static void Main(SlashCommandInteractionEvent IT) {
		Guild GUILD = IT.getGuild();

		if(GUILD != null){
			EmbedBuilder EB = new EmbedBuilder();
			EB.setTitle(GUILD.getName());
			EB.setDescription(GUILD.getDescription());
			EB.setColor(Color.CYAN);
			EB.setThumbnail(GUILD.getIconUrl());

			EB.addField("ID", GUILD.getId(), true);
			EB.addField("認証レベル", GUILD.getVerificationLevel().name(), true);
			EB.addField("所有者", "<@" + GUILD.getOwner().getId() + ">", true);
			if(GUILD.getAfkChannel() != null){
				EB.addField("AFKチャンネル", "<#" + GUILD.getAfkChannel().getId() + ">", true);
			}
			EB.addField("鯖作成日", DATE_FORMAT.ZHUUNI_H(GUILD.getTimeCreated()), true);
			EB.addField("ユーザー数", String.valueOf(GUILD.getMembers().toArray().length), true);
			EB.addField("ブースト回数", String.valueOf(GUILD.getBoostCount()), true);

			//絵文字一覧を作る
			EmbedBuilder EMOJI_EB = new EmbedBuilder();
			StringBuilder EMOJI_TEXT = new StringBuilder();

			//埋め込みの設定
			EMOJI_EB.setTitle("絵文字");
			EMOJI_EB.setColor(Color.CYAN);

			//鯖内の全ての絵文字を回す
			for(RichCustomEmoji EMOJI:GUILD.getEmojis()){
				//絵文字のタグを書く変数
				String EMOJI_TAG = "";

				//絵文字が生きているか
				if(EMOJI.isAvailable()){
					//絵文字はアニメーション絵文字か
					if(!EMOJI.isAnimated()){
						//普通の絵文字として追加
						EMOJI_TAG = "<:" + EMOJI.getName() + ":" + EMOJI.getId() + ">";
					} else {
						//アニメーション絵文字として追加
						EMOJI_TAG = "<a:" + EMOJI.getName() + ":" + EMOJI.getId() + ">";
					}

					//今までに追加された「絵文字一覧文章 + 絵文字のタグ」の長さが1000を超えていないか
					if((EMOJI_TEXT + EMOJI_TAG).length() <= 1000){
						//超えていないので絵文字一覧文章に追加
						EMOJI_TEXT.append(EMOJI_TAG);
					} else {
						//超えているので、一旦埋め込みに追加
						EMOJI_EB.addField("絵文字", EMOJI_TEXT.toString(), false);

						//絵文字一覧文章を初期化
						EMOJI_TEXT = new StringBuilder();
					}
				}
			}

			//返答する
			IT.getHook().editOriginalEmbeds(EB.build(), EMOJI_EB.build()).queue();
		} else {
			//エラー
			IT.getHook().editOriginal("サーバーが見つかりませんでした！！Бля！").queue();
		}
	}
}
