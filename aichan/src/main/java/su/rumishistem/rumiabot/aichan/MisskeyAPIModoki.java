package su.rumishistem.rumiabot.aichan;

import static su.rumishistem.rumiabot.System.Main.CONFIG_DATA;
import java.io.IOException;
import su.rumishistem.rumi_java_lib.SmartHTTP.SmartHTTP;
import su.rumishistem.rumi_java_lib.WebSocket.Server.WebSocketSERVER;
import su.rumishistem.rumi_java_lib.WebSocket.Server.CONNECT_EVENT.CONNECT_EVENT;
import su.rumishistem.rumiabot.aichan.API.CreateFileAPI;
import su.rumishistem.rumiabot.aichan.API.CreateNoteAPI;
import su.rumishistem.rumiabot.aichan.API.CreateReactionAPI;
import su.rumishistem.rumiabot.aichan.API.GetI;
import su.rumishistem.rumiabot.aichan.API.ReversiMatchAPI;
import su.rumishistem.rumiabot.aichan.API.StreamingAPI;
import su.rumishistem.rumiabot.aichan.API.UserShowAPI;

public class MisskeyAPIModoki {
	public static final String JSONMime = "application/json; charset=UTF-8";
	public static String DOMAIN = null;
	public static String TOKEN = null;

	public static void WebSocketStart() throws InterruptedException {
		//WebSocketサーバー
		WebSocketSERVER WSS = new WebSocketSERVER();
		WSS.SET_EVENT_VOID(new StreamingAPI());
		WSS.START(CONFIG_DATA.get("AI").getData("WS").asInt());
	}

	public static void WebSocketSend(String Body) {
		for (CONNECT_EVENT SESSION:StreamingAPI.WebSocketSession) {
			SESSION.SendMessage(Body);
		}
	}

	public static void HTTPStart() throws IOException, InterruptedException {
		DOMAIN = CONFIG_DATA.get("MISSKEY").getData("DOMAIN").asString();
		TOKEN = CONFIG_DATA.get("MISSKEY").getData("TOKEN").asString();

		//HTTPサーバー
		SmartHTTP SH = new SmartHTTP(CONFIG_DATA.get("AI").getData("HTTP").asInt());

		//自分自身を習得
		SH.SetRoute("/api/i", new GetI());

		//ノート作成
		SH.SetRoute("/api/notes/create", new CreateNoteAPI());

		//ノートにリアクション
		SH.SetRoute("/api/notes/reactions/create", new CreateReactionAPI());

		//ユーザー習得
		SH.SetRoute("/api/users/show", new UserShowAPI());

		//ファイルアップロード
		SH.SetRoute("/api/drive/files/create", new CreateFileAPI());

		//オセロ対局
		SH.SetRoute("/api/reversi/match", new ReversiMatchAPI());

		SH.Start();
	}

	public static void SendWebSocket(String Text) {
		//配列にしないと送信できませんわ！
		for (CONNECT_EVENT SESSION:StreamingAPI.WebSocketSession) {
			SESSION.SendMessage(Text);
		}
	}
}
