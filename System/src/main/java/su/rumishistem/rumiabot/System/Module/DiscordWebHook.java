package su.rumishistem.rumiabot.System.Module;

import net.dv8tion.jda.api.entities.Webhook;
import net.dv8tion.jda.api.entities.channel.concrete.TextChannel;
import su.rumishistem.rumiabot.System.Main;

import java.util.List;
import java.util.concurrent.CountDownLatch;
import java.util.function.Consumer;

public class DiscordWebHook {
	private Webhook WH = null;//WebHook

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
					if(ROW.getOwner().getUser().getId().equals(Main.get_discord_bot().get_primary_bot().getSelfUser().getId())){
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
