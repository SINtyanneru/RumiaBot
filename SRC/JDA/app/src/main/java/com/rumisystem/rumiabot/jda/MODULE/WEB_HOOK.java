package com.rumisystem.rumiabot.jda.MODULE;

import net.dv8tion.jda.api.entities.User;
import net.dv8tion.jda.api.entities.Webhook;
import net.dv8tion.jda.api.entities.WebhookClient;
import net.dv8tion.jda.api.entities.channel.concrete.TextChannel;
import net.dv8tion.jda.api.managers.WebhookManager;
import net.dv8tion.jda.api.requests.Route;
import net.dv8tion.jda.api.requests.restaction.WebhookAction;

import java.util.List;
import java.util.function.Consumer;

import static com.rumisystem.rumiabot.jda.Main.BOT;

public class WEB_HOOK {
	private Webhook WH = null;//WebHook
	private String WH_AVATOR_URL = BOT.getSelfUser().getAvatarUrl();//アバターのURL(デフォルト値は自分のアイコン)
	private String WH_USER_NAME = BOT.getSelfUser().getName();//名前(デフォルト値は自分)
	private boolean OK = false;//準備完了の意

	public WEB_HOOK(TextChannel CHANNEL){
		//チャンネルに有るWHを全て取得
		CHANNEL.retrieveWebhooks().queue(new Consumer<List<Webhook>>() {
			//まじでJAVAでこういうイベント処理するなやわかりにくいし書きにくいわ死んでくれ
			@Override
			public void accept(List<Webhook> WEBHOOKU) {
				//チャンネルにあるWHをすべてチェックする
				for(Webhook ROW:WEBHOOKU){
					//WHのオーナーのIDと自分のIDが一致するか？
					if(ROW.getOwner().getUser().getId().equals(BOT.getSelfUser().getId())){
						System.out.println("あった");
						//グローバル変数にWHを入れて終了
						WH = ROW;
						OK = true;
						return;
					}
				}


				//WHが無いと此処に来る
				//WHを作る
				CHANNEL.createWebhook("るみBOT").queue(new Consumer<Webhook>() {
					@Override
					public void accept(Webhook WEBHOOK) {
						WH = WEBHOOK;
						OK = true;
					}
				});
			}
		});
	}

	//アバターを変更する
	public void SET_USERNAME(String NAME){
		WH_USER_NAME = NAME;
	}

	//アバターを変更する
	public void SET_AVATOR(User USER){
		WH_AVATOR_URL = USER.getAvatarUrl();
	}

	//メッセージを送信する
	public void SEND(String TEXT){
		//準備完了まで待つ、待ち続ける。。。
		while(true){
			if(OK){
				System.out.println("送信");
				WH.sendMessage(TEXT).setAvatarUrl(WH_AVATOR_URL).setUsername(WH_USER_NAME).queue();
				return;
			} else {
				//何らの処理が無いと動かない、たぶんJAVAの嫌がらせだと思う
				System.out.flush();
			}
		}
	}
}
