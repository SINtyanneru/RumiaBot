package com.rumisystem.rumiabot.Discord.COMMAND;

import com.rumisystem.rumi_java_lib.HTTP_REQUEST;
import com.rumisystem.rumiabot.MODULE.COMMAND_INTERACTION;

public class ip {
	public static void Main(COMMAND_INTERACTION IT) {
		try{
			HTTP_REQUEST AJAX = new HTTP_REQUEST("https://ifconfig.me/ip");
			String IP_AD = AJAX.GET();

			IT.SetTEXT("私のIPアドレスは" + IP_AD + "です");
			IT.Reply();
		} catch (Exception EX) {
			EX.printStackTrace();
			IT.SetTEXT("エラー");
			IT.Reply();
		}
	}
}
