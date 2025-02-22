package su.rumishistem.rumiabot.VXTwitterFunction;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.channel.concrete.TextChannel;
import net.dv8tion.jda.api.requests.restaction.WebhookMessageCreateAction;
import su.rumishistem.rumiabot.System.Discord.MODULE.DiscordWebHook;
import su.rumishistem.rumiabot.System.TYPE.CommandInteraction;
import su.rumishistem.rumiabot.System.TYPE.DiscordFunction;
import su.rumishistem.rumiabot.System.TYPE.FunctionClass;
import su.rumishistem.rumiabot.System.TYPE.ReceiveMessageEvent;
import su.rumishistem.rumiabot.System.TYPE.SourceType;

public class Main implements FunctionClass {
	private static final String FUNCTION_NAME = "VXTwitter変換";
	private static final String FUNCTION_VERSION = "1.0";
	private static final String FUNCTION_AUTOR = "Rumisan";

	@Override
	public String FUNCTION_NAME() {
		return FUNCTION_NAME;
	}
	@Override
	public String FUNCTION_VERSION() {
		return FUNCTION_VERSION;
	}
	@Override
	public String FUNCTION_AUTOR() {
		return FUNCTION_AUTOR;
	}
	@Override
	public void Init() {
	}

	@Override
	public void ReceiveMessage(ReceiveMessageEvent e) {
		if (e.GetSource() == SourceType.Discord && !e.GetUser().isMe()) {
			if (e.GetMessage().CheckDiscordGuildFunctionEnabled(DiscordFunction.vxtwitter)) {
				//WebHook用意
				TextChannel Channel = (TextChannel) e.GetMessage().GetDiscordChannel();
				DiscordWebHook WH = new DiscordWebHook(Channel);

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
					WebhookMessageCreateAction<Message> MSG = WH.Send().sendMessage(RESULT.toString());
					MSG.setUsername(e.GetUser().GetName());
					MSG.setAvatarUrl(e.GetUser().GetIconURL());
					MSG.queue();

					//削除
					e.GetMessage().Delete();
				}
			}
		}
	}

	@Override
	public boolean GetAllowCommand(String Name) {
		return false;
	}

	@Override
	public void RunCommand(CommandInteraction CI) throws Exception {
	}

}
