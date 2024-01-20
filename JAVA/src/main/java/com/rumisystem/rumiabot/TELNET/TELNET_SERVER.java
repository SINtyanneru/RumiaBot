package com.rumisystem.rumiabot.TELNET;

import com.rumisystem.rumiabot.Main;

import java.io.OutputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.HashMap;

public class TELNET_SERVER {
	private static int PORT_NUMBER = 3001;
	public static HashMap<String, OutputStream> CONNECTIONU = new HashMap<String, OutputStream>();

	public static void main(){
		try{
			Main.LOG(" ***   | PT", "Starting", 0);
			ServerSocket SS = new ServerSocket(PORT_NUMBER);
			while(true){
				Socket CLIENT_SOCKET = SS.accept();
				TELNET_HANDLER TH = new TELNET_HANDLER(CLIENT_SOCKET);
				new Thread(TH).start();
			}
		}catch (Exception EX){
			Main.LOG(" ERR   | PT", "ERR", 1);
			EX.printStackTrace();
		}
	}
}
