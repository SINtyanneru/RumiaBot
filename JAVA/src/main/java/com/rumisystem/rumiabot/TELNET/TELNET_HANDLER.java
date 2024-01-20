package com.rumisystem.rumiabot.TELNET;

import com.rumisystem.rumiabot.Main;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.Socket;
import java.nio.charset.StandardCharsets;

public class TELNET_HANDLER implements Runnable {
	private Socket CLIENT_SOCKET;

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
				String DATA = new String(BUFFER, 0, BYTES_READ, StandardCharsets.UTF_8);
				System.out.println("受信データ: " + DATA);
				SEND_STRING(OUTPUT_STREAM, DATA + ";200");
			}

		} catch (Exception EX) {
			Main.LOG(" ERR   | PT", "ERR", 1);
			EX.printStackTrace();
		}
	}

	private void SEND_STRING(OutputStream OS, String TEXT) throws IOException {
		byte[] messageBytes = TEXT.getBytes(StandardCharsets.UTF_8);
		OS.write(messageBytes);
		OS.flush();
	}
}
