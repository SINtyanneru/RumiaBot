package su.rumishistem.rumiabot.VXTwitterFunction;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.channel.concrete.TextChannel;
import net.dv8tion.jda.api.requests.restaction.WebhookMessageCreateAction;
import su.rumishistem.rumiabot.System.Module.DiscordWebHook;
import su.rumishistem.rumiabot.System.Type.ReceiveMessageEvent;

public class FXTwitter {
	public static void main(ReceiveMessageEvent e, TextChannel ch, DiscordWebHook wh) {
		//変換
		StringBuilder RESULT = new StringBuilder();
		Pattern PTN = Pattern.compile("https://(?:twitter|x)\\.com/([a-zA-Z0-9_]+/status/[0-9]+)(\\?s=[0-9]*)?");
		Matcher MTC = PTN.matcher(e.get_discord().getMessage().getContentRaw());
		while(MTC.find()){
			String REPLACERO_TEXT = "https://fxtwitter.com/" + MTC.group(1);
			MTC.appendReplacement(RESULT, REPLACERO_TEXT);
		}
		MTC.appendTail(RESULT);

		//変更点が有れば元メッセージを消してWebHook化
		if (!RESULT.toString().equals(e.get_discord().getMessage().getContentRaw())) {
			//送信
			WebhookMessageCreateAction<Message> MSG = wh.Send().sendMessage(RESULT.toString());
			MSG.setUsername(e.get_discord().getAuthor().getEffectiveName());
			MSG.setAvatarUrl(e.get_discord().getAuthor().getEffectiveAvatarUrl());
			MSG.queue();

			//削除
			e.get_discord().getMessage().delete().queue();
		}
	}
}
