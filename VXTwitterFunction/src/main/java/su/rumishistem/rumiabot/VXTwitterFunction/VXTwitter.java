package su.rumishistem.rumiabot.VXTwitterFunction;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.channel.concrete.TextChannel;
import net.dv8tion.jda.api.requests.restaction.WebhookMessageCreateAction;
import su.rumishistem.rumiabot.System.Discord.MODULE.DiscordWebHook;
import su.rumishistem.rumiabot.System.TYPE.ReceiveMessageEvent;

public class VXTwitter {
	public static void main(ReceiveMessageEvent e, TextChannel ch, DiscordWebHook wh) {
		//変換
		StringBuilder RESULT = new StringBuilder();
		Pattern PTN = Pattern.compile("https://(?:twitter|x)\\.com/([a-zA-Z0-9_]+/status/[0-9]+)(\\?s=[0-9]*)?");
		Matcher MTC = PTN.matcher(e.GetMessage().GetText());
		while(MTC.find()){
			String REPLACERO_TEXT = "https://vxtwitter.com/" + MTC.group(1);
			MTC.appendReplacement(RESULT, REPLACERO_TEXT);
		}
		MTC.appendTail(RESULT);

		//変更点が有れば元メッセージを消してWebHook化
		if (!RESULT.toString().equals(e.GetMessage().GetText())) {
			//送信
			WebhookMessageCreateAction<Message> MSG = wh.Send().sendMessage(RESULT.toString());
			MSG.setUsername(e.GetUser().GetName());
			MSG.setAvatarUrl(e.GetUser().GetIconURL());
			MSG.queue();

			//削除
			e.GetMessage().Delete();
		}
	}
}
