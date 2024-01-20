package com.rumisystem.rumiabot.TELNET;

import com.rumisystem.rumiabot.Main;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.Socket;
import java.nio.charset.StandardCharsets;
import java.util.Objects;

import static com.rumisystem.rumiabot.Main.LOG;
import static com.rumisystem.rumiabot.TELNET.TELNET_SERVER.CONNECTIONU;

public class TELNET_HANDLER implements Runnable {
	private Socket CLIENT_SOCKET;

	private final String INFO_LOG_TAG = " INFO  | PT";
	private final String ERR_LOG_TAG = " ERR   | PT";

	public TELNET_HANDLER(Socket CLIENT_SOCKET) {
		this.CLIENT_SOCKET = CLIENT_SOCKET;
	}

	@Override
	public void run() {
		try (
				InputStream INPUT_STREAM = CLIENT_SOCKET.getInputStream();
				OutputStream OUTPUT_STREAM = CLIENT_SOCKET.getOutputStream()
		) {
			byte[] BUFFER = new byte[1024];
			int BYTES_READ;


			while ((BYTES_READ = INPUT_STREAM.read(BUFFER)) != -1) {
				String MSG = new String(BUFFER, 0, BYTES_READ, StandardCharsets.UTF_8);
				String[] CMD = MSG.split(";");

				//ログを吐く
				LOG(INFO_LOG_TAG, "Message received:" + MSG, 0);

				//コマンドに寄って処理を変える
				switch (CMD[1]){
					//認証
					case "HELLO":
						if(CMD[2].equals("JS")){//JS
							//接続一覧にOutputStreamを追加
							CONNECTIONU.put("JS", OUTPUT_STREAM);

							//成功と返す
							SEND_STRING(OUTPUT_STREAM, CMD[0] + ";HELLO;200");
							break;
						}if(CMD[2].equals("PY")){//Python
							//接続一覧にOutputStreamを追加
							CONNECTIONU.put("PY", OUTPUT_STREAM);

							//成功と返す
							SEND_STRING(OUTPUT_STREAM, CMD[0] + ";HELLO;200");
							break;
						}else{//誰やねんお前
							SEND_STRING(OUTPUT_STREAM, CMD[0] + ";WHO_IS_YOU;403");
						}
						break;

					//Discord関連の命令(JSに横ながし)
					case "DISCORD":
						if(Objects.nonNull(CONNECTIONU.get("JS"))){
							SEND_STRING(CONNECTIONU.get("JS"), MSG);

							SEND_STRING(OUTPUT_STREAM, CMD[0] + ";200");
						}else {
							SEND_STRING(OUTPUT_STREAM, CMD[0] + ";500");
						}
						break;

					default:
						SEND_STRING(OUTPUT_STREAM, CMD[0] + ";400");
				}
			}

		} catch (Exception EX) {
			LOG(ERR_LOG_TAG, "ERR", 1);
			EX.printStackTrace();
		}
	}

	private void SEND_STRING(OutputStream OS, String TEXT) throws IOException {
		byte[] messageBytes = TEXT.getBytes(StandardCharsets.UTF_8);
		OS.write(messageBytes);
		OS.flush();
	}
}
