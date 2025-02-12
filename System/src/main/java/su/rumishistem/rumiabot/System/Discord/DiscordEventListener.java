package su.rumishistem.rumiabot.System.Discord;

import static su.rumishistem.rumiabot.System.Main.FunctionModuleList;
import static su.rumishistem.rumiabot.System.Main.CONFIG_DATA;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import su.rumishistem.rumiabot.System.TYPE.FunctionClass;
import su.rumishistem.rumiabot.System.TYPE.MessageData;
import su.rumishistem.rumiabot.System.TYPE.MessageUser;
import su.rumishistem.rumiabot.System.TYPE.ReceiveMessageEvent;
import su.rumishistem.rumiabot.System.TYPE.SourceType;

public class DiscordEventListener extends ListenerAdapter {
	@Override
	public void onMessageReceived(MessageReceivedEvent E) {
		//ブロック済みのユーザーなら此処で処理を中断する
		if (CONFIG_DATA.get("BLOCK").getData("DISCORD").asString().contains(E.getAuthor().getId())) {
			return;
		}

		//イベント着火
		for (FunctionClass Function:FunctionModuleList) {
			Function.ReceiveMessage(new ReceiveMessageEvent(
				SourceType.Discord,
				new MessageUser(),
				new MessageData(
					E.getMessageId(),
					E.getMessage().getContentRaw(),
					E.getMessage()
				)
			));
		}
	}
}
