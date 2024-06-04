package com.rumisystem.rumiabot.jda.FUNCTION;

import com.rumisystem.rumiabot.jda.MODULE.FUNCTION_CHECK_RESULT;
import com.rumisystem.rumiabot.jda.MODULE.WEB_HOOK;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;

import java.io.IOException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static com.rumisystem.rumiabot.jda.MODULE.FUNCTION.FUNCTION_CHECK;
import static com.rumisystem.rumiabot.jda.Main.BOT;

public class VXTWITTER_CONVERT {
	public static void main(MessageReceivedEvent E) throws IOException {
		//自分の投稿じゃない＆BOTじゃない
		if(!E.getAuthor().getId().equals(BOT.getSelfUser().getId()) && !E.getAuthor().isBot()){
			//設定されている
			if(FUNCTION_CHECK(E.getGuild().getId(), E.getChannel().getId(), "vxtwitter") == FUNCTION_CHECK_RESULT.ANY ||
					FUNCTION_CHECK(E.getGuild().getId(), E.getChannel().getId(), "vxtwitter") == FUNCTION_CHECK_RESULT.GUILD_ONLY ||
					FUNCTION_CHECK(E.getGuild().getId(), E.getChannel().getId(), "vxtwitter") == FUNCTION_CHECK_RESULT.CHANNEL_ONLY
			){
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
					WEB_HOOK WH = new WEB_HOOK(E.getChannel().asTextChannel());
					WH.SEND().sendMessage(RESULT.toString()).setUsername(NAME).setAvatarUrl(E.getAuthor().getAvatarUrl()).queue();

					//元メッセージを削除
					E.getMessage().delete().queue();
				}
			}
		}
	}
}
