package com.rumisystem.rumiabot.jda.COMMAND;

import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.User;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;

import java.awt.*;
import java.time.format.DateTimeFormatter;

public class info_user {
	public static void main(SlashCommandInteractionEvent INTERACTION) {
		User USER = INTERACTION.getUser();

		//ユーザーが指定されているなら、変数に其のユーザーを入れる
		if(INTERACTION.getOption("user") != null){
			USER = INTERACTION.getOption("user").getAsUser();
		}

		//Nullチェック
		if(USER != null){
			EmbedBuilder EB = new EmbedBuilder();
			EB.setTitle(USER.getGlobalName());
			EB.setDescription(USER.getName());
			EB.setThumbnail(USER.getAvatarUrl());

			EB.addField("ID", USER.getId(), true);

			//アカウント作成日
			EB.addField("アカウント作成日", USER.getTimeCreated().format(DateTimeFormatter.ofPattern("yyyy年MM月dd日E曜日 h時m分s秒")), true);

			//鯖で実行されたか
			if(INTERACTION.getGuild() != null){
				Member MEMBER = INTERACTION.getGuild().getMember(USER);
				if(MEMBER != null){
					//ちなみにJDAのバグで取得できないよ、カスだね
					EB.addField("鯖に参加した日付", MEMBER.getTimeJoined().format(DateTimeFormatter.ofPattern("yyyy年MM月dd日E曜日 h時m分s秒")), true);
				} else {
					EB.addField("鯖に参加した日付", "JDAのバグで取得できませんでした", true);
				}

				if(MEMBER.isBoosting()){
					EB.addField("鯖のブースト日付", MEMBER.getTimeBoosted().format(DateTimeFormatter.ofPattern("yyyy年MM月dd日E曜日 h時m分s秒")), true);
				} else {
					EB.addField("鯖のブースト日付", "そもそもブーストしてない", true);
				}

				if(MEMBER.getNickname() != null){
					EB.addField("ニックネーム", MEMBER.getNickname(), true);
				} else {
					EB.addField("ニックネーム", "無い", true);
				}
			}

			//BOTか
			if(USER.isBot()){
				EB.addField("BOTか", "はい", true);
			} else {
				EB.addField("BOTか", "いいえ", true);
			}

			INTERACTION.getHook().editOriginalEmbeds(EB.build()).queue();
		} else {
			INTERACTION.getHook().editOriginal("Nullです").queue();
		}
	}
}
