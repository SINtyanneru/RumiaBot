package su.rumishistem.rumiabot.System.Discord.MODULE;

import static su.rumishistem.rumiabot.System.Main.DISCORD_BOT;
import net.dv8tion.jda.api.entities.Webhook;
import net.dv8tion.jda.api.entities.channel.concrete.TextChannel;
import java.util.List;
import java.util.concurrent.CountDownLatch;
import java.util.function.Consumer;

public class DiscordWebHook {
	private Webhook WH = null;//WebHook
	private String WH_AVATOR_URL = DISCORD_BOT.getSelfUser().getAvatarUrl();//アバターのURL(デフォルト値は自分のアイコン)
	private String WH_USER_NAME = DISCORD_BOT.getSelfUser().getName();//名前(デフォルト値は自分)

	public DiscordWebHook(TextChannel CHANNEL) throws InterruptedException{
		CountDownLatch CDL = new CountDownLatch(1);

		//チャンネルに有るWHを全て取得
		CHANNEL.retrieveWebhooks().queue(new Consumer<List<Webhook>>() {
			//まじでJAVAでこういうイベント処理するなやわかりにくいし書きにくいわ死んでくれ
			@Override
			public void accept(List<Webhook> WEBHOOKU) {
				//チャンネルにあるWHをすべてチェックする
				for(Webhook ROW:WEBHOOKU){
					//WHのオーナーのIDと自分のIDが一致するか？
					if(ROW.getOwner().getUser().getId().equals(DISCORD_BOT.getSelfUser().getId())){
						//グローバル変数にWHを入れて終了
						WH = ROW;
						CDL.countDown();
						return;
					}
				}

				//WHが無いと此処に来る
				//WHを作る
				CHANNEL.createWebhook("るみBOT").queue(new Consumer<Webhook>() {
					@Override
					public void accept(Webhook WEBHOOK) {
						WH = WEBHOOK;
						CDL.countDown();
					}
				});
			}
		});

		CDL.await();
	}

	public String getURL() {
		return WH.getUrl();
	}

	public String getToken() {
		return WH.getToken();
	}

	//メッセージを送信する
	public Webhook Send(){
		return WH;
	}
}
