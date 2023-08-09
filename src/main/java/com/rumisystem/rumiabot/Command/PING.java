package com.rumisystem.rumiabot.Command;

import com.fasterxml.jackson.core.JsonProcessingException;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;

import java.io.IOException;
import java.net.InetAddress;

public class PING {
	public static void Main(SlashCommandInteractionEvent e){
		e.deferReply().queue();//ちょっとまってねをする

		String hostName = e.getOption("ip").getAsString(); // ここにPingを送りたいホスト名やIPアドレスを指定します
		try {
			StringBuilder LOG = new StringBuilder();

			InetAddress inetAddress = InetAddress.getByName(hostName);
			LOG.append("pingを送信：" + inetAddress.getHostAddress() + "\n");

			e.getHook().editOriginal(LOG.toString()).queue();

			if(inetAddress.isReachable(5000)){
				LOG.append("おｋ" + "\n");
				e.getHook().editOriginal(LOG.toString()).queue();
			}else{
				LOG.append("だめ" + "\n");
				e.getHook().editOriginal(LOG.toString()).queue();
			}
		} catch (IOException EX) {
			EX.printStackTrace();
		}
	}
}
