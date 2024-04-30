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
				LOG(INFO_LOG_TAG, "TELNET受信" + MSG, 0);

				//返信を受信したら
				if(CMD[0].startsWith("REPLY_")){
					String ID = CMD[0].replace("REPLY_", "");

					LOG(INFO_LOG_TAG, ID + "でPTからリプライが来たので横流しします", 0);

					for(String KEY:CONNECTIONU.keySet()){
						if(CONNECTIONU.get(KEY) != null){
							LOG(INFO_LOG_TAG, "SV -> " + KEY, 0);
							SEND_STRING(CONNECTIONU.get(KEY), ID + ";" + CMD_TO_STRING(CMD));
						} else {
							LOG(INFO_LOG_TAG, "ERR:SV -> " + KEY, 1);
						}
					}
				} else {
					//コマンドに寄って処理を変える
					switch (CMD[1]){
						//認証
						case "HELLO":
							switch (CMD[2]) {
								case "JS": //JS
									//接続一覧にOutputStreamを追加
									CONNECTIONU.put("JS", OUTPUT_STREAM);

									//成功と返す
									SEND_STRING(OUTPUT_STREAM, CMD[0] + ";HELLO;200");

									LOG(INFO_LOG_TAG, "SV -> JS", 0);
									break;
								case "PY": //Python
									//接続一覧にOutputStreamを追加
									CONNECTIONU.put("PY", OUTPUT_STREAM);

									//成功と返す
									SEND_STRING(OUTPUT_STREAM, CMD[0] + ";HELLO;200");

									LOG(INFO_LOG_TAG, "SV -> PY", 0);
									break;
								case "JAVA": //JAVA
									//接続一覧にOutputStreamを追加
									CONNECTIONU.put("JAVA", OUTPUT_STREAM);

									//成功と返す
									SEND_STRING(OUTPUT_STREAM, CMD[0] + ";HELLO;200");

									LOG(INFO_LOG_TAG, "SV -> JAVA", 0);
									break;
								case "JDA": //JDA
									//接続一覧にOutputStreamを追加
									CONNECTIONU.put("JDA", OUTPUT_STREAM);

									//成功と返す
									SEND_STRING(OUTPUT_STREAM, CMD[0] + ";HELLO;200");
									LOG(INFO_LOG_TAG, "SV -> JDA", 0);
									break;
								default: //誰やねんお前
									SEND_STRING(OUTPUT_STREAM, CMD[0] + ";WHO_IS_YOU;403");
									break;
							}
							break;

						//Discord関連の命令(JSに横ながし)
						case "DISCORD":
							if(Objects.nonNull(CONNECTIONU.get("JS"))){
								SEND_STRING(CONNECTIONU.get("JS"), CMD_TO_STRING(CMD));

								//SEND_STRING(OUTPUT_STREAM, CMD[0] + ";200");
							} else {
								SEND_STRING(OUTPUT_STREAM, CMD[0] + ";500");
							}
							break;

						//SQL
						case "SQL_UP":
						case "SQL":
							if(Objects.nonNull(CONNECTIONU.get("JS"))){
								SEND_STRING(CONNECTIONU.get("JAVA"), "REPLY_" + CMD[0] + ";" + CMD_TO_STRING(CMD));
							} else {
								SEND_STRING(OUTPUT_STREAM, CMD[0] + ";500");
							}
							break;
						default:
							LOG(INFO_LOG_TAG,"不明なコマンドを受信しました", 0);
							SEND_STRING(OUTPUT_STREAM, CMD[0] + ";SIGNAL_NOT_FOUND;400");
					}
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

	private String CMD_TO_STRING(String[] CMD){
		StringBuilder SB = new StringBuilder();
		for (int I = 1; I < CMD.length; I++){
			SB.append(CMD[I] + ";");
		}

		return SB.toString();
	}
}
