package com.rumisystem.rumiabot.Discord.FUNCTION;

import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import net.dv8tion.jda.api.interactions.components.buttons.Button;

import static com.rumisystem.rumiabot.Main.DISCORD_BOT;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.rumisystem.rumiabot.Discord.MODULE.WEBHOOK;

public class VXTWITTER {
	public static void Main(MessageReceivedEvent E) {
		//自分の投稿ではない＆BOTの投稿じゃない
		if (!E.getAuthor().getId().endsWith(DISCORD_BOT.getSelfUser().getId()) && !E.getAuthor().isBot()) {
			if (FUNCTION_MANAGER.FUNCTION_CHECK(E.getGuild().getId(), "", "vxtwitter")) {
				String TEXT = E.getMessage().getContentRaw();
				StringBuilder RESULT = new StringBuilder();
				Pattern PTN = Pattern.compile("https://(?:twitter|x)\\.com/([a-zA-Z0-9_]+/status/[0-9]+)(\\?s=[0-9]*)?");
				Matcher  MTC = PTN.matcher(TEXT);
				while(MTC.find()){
					String REPLACERO_TEXT = "https://vxtwitter.com/" + MTC.group(1);
					MTC.appendReplacement(RESULT, REPLACERO_TEXT);
				}

				MTC.appendTail(RESULT);

				//もし変更点があるなら、置換する
				if(!RESULT.toString().equals(TEXT)){
					//ユーザー名もしくはニックネームを取得
					String NAME = E.getMember().getUser().getGlobalName();
					if(E.getMember().getNickname() != null){
						NAME = E.getMember().getNickname();
					}

					//送信
					WEBHOOK WH = new WEBHOOK(E.getChannel().asTextChannel());
					WH.SEND().sendMessage(RESULT.toString())
							.setUsername(NAME)
							.setAvatarUrl(E.getAuthor().getAvatarUrl())
							.setActionRow(Button.primary("MEDIA_DOWNLOAD?ID=", "メディアをダウンロード"))
							.queue();

					//元メッセージを削除
					E.getMessage().delete().queue();
				}
			}
		}
	}
}
