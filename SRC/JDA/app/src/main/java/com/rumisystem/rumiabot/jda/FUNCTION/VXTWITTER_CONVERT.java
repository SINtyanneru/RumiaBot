package com.rumisystem.rumiabot.jda.FUNCTION;

import com.beust.ah.A;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.rumisystem.rumiabot.jda.MODULE.FUNCTION_CHECK_RESULT;
import com.rumisystem.rumiabot.jda.MODULE.HTTP_REQUEST;
import com.rumisystem.rumiabot.jda.MODULE.WEB_HOOK;
import net.dv8tion.jda.api.events.interaction.component.ButtonInteractionEvent;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import net.dv8tion.jda.api.interactions.components.buttons.Button;
import net.dv8tion.jda.api.utils.FileUpload;

import java.io.File;
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

	public static void DOWNLOAD_MEDIA(ButtonInteractionEvent INTERACTION){
		try{
			String TEXT = INTERACTION.getMessage().getContentRaw();
			StringBuilder RESULT = new StringBuilder();
			Pattern PTN = Pattern.compile("https://(?:vxtwitter|x)\\.com/([a-zA-Z0-9_]+/status/[0-9]+)(\\?s=[0-9]*)?");
			Matcher  MTC = PTN.matcher(TEXT);

			//URLの1番目を探す
			MTC.find();
			String URL = "https://api.vxtwitter.com/" + MTC.group(1);

			//AJAXする
			String AJAX = new HTTP_REQUEST(URL).GET();
			JsonNode AJAX_RESULT = new ObjectMapper().readTree(AJAX);

			String PATH = "./DOWNLOAD/VXTWITTER/" + INTERACTION.getId();
			String MEDIA_URL = AJAX_RESULT.get("mediaURLs").get(0).asText();
			String EXT = MEDIA_URL.split("\\.")[MEDIA_URL.split("\\.").length - 1];

			if(EXT.equals("png") || EXT.equals("jpg") || EXT.equals("jpeg")){
				MEDIA_URL = MEDIA_URL + "?format=png&name=4096x4096";
			}

			new HTTP_REQUEST(MEDIA_URL).DOWNLOAD(PATH + "." + EXT);

			INTERACTION.getHook().editOriginal("どうぞ").setAttachments(FileUpload.fromData(new File(PATH + "." + EXT))).queue();
		} catch (Exception EX) {
			EX.printStackTrace();
			INTERACTION.getHook().editOriginal("エラー").queue();
		}
	}
}
