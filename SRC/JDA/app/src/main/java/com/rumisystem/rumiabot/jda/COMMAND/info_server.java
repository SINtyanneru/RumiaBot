package com.rumisystem.rumiabot.jda.COMMAND;

import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;

import java.awt.*;
import java.time.format.DateTimeFormatter;

public class info_server {
	public static void main(SlashCommandInteractionEvent INTERACTION) {
		Guild GUILD = INTERACTION.getGuild();

		if(GUILD != null){
			EmbedBuilder EB = new EmbedBuilder();
			EB.setTitle(GUILD.getName());
			EB.setDescription(GUILD.getDescription());
			EB.setColor(Color.CYAN);
			EB.setThumbnail(GUILD.getIconUrl());

			EB.addField("ID", GUILD.getId(), true);
			EB.addField("認証レベル", GUILD.getVerificationLevel().name(), true);
			EB.addField("所有者", "<@" + GUILD.getOwner().getId() + ">", true);
			EB.addField("AFKチャンネル", "<#" + GUILD.getAfkChannel().getId() + ">", true);
			EB.addField("ID", GUILD.getTimeCreated().format(DateTimeFormatter.ofPattern("yyyy年MM月dd日E曜日 h時m分s秒")), true);
			EB.addField("ユーザー数", String.valueOf(GUILD.getMembers().toArray().length), true);

			INTERACTION.getHook().editOriginalEmbeds(EB.build()).queue();
		} else {
			INTERACTION.getHook().editOriginal("サーバーが見つかりませんでした！！Бля！").queue();
		}
	}
}
