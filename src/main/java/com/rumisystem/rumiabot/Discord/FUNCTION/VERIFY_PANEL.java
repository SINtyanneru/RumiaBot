package com.rumisystem.rumiabot.Discord.FUNCTION;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.rumisystem.rumi_java_lib.ArrayNode;
import com.rumisystem.rumi_java_lib.SQL;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.Role;
import net.dv8tion.jda.api.entities.UserSnowflake;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.events.interaction.component.ButtonInteractionEvent;
import net.dv8tion.jda.api.interactions.components.buttons.Button;

import java.awt.Color;
import java.io.IOException;
import java.util.HashMap;
import java.util.UUID;

import static com.rumisystem.rumiabot.Main.DISCORD_BOT;

public class VERIFY_PANEL {
	private static HashMap<String, HashMap<String, String>> VERIFY_LIST = new HashMap<>();

	public static void PANEL_CREATE(SlashCommandInteractionEvent INTERACTION) throws IOException {
	}

	public static HashMap<String, String> URI_PARAM_PARSE(String URI){
		HashMap<String, String> RESULT = new HashMap<>();

		String[] SPLIT_URI = URI.split("\\?")[1].split("&");

		for(int I = 0; I < SPLIT_URI.length; I++){
			String KEY = SPLIT_URI[I].split("=")[0];
			String VAL = SPLIT_URI[I].split("=")[1];

			RESULT.put(KEY, VAL);
		}

		return RESULT;
	}

	//認証
	public static void VERIFY_BUTTON(ButtonInteractionEvent INTERACTION) throws IOException {
		HashMap<String, String> PARAM = URI_PARAM_PARSE(INTERACTION.getInteraction().getButton().getId().toString());

		String ID = UUID.randomUUID().toString();

		//認証データを作る
		HashMap<String, String> VERIFY_DATA = new HashMap<>();
		VERIFY_DATA.put("PANEL_ID", PARAM.get("id"));
		VERIFY_DATA.put("UID", INTERACTION.getUser().getId());

		//認証の受付リストに入れる
		VERIFY_LIST.put(ID, VERIFY_DATA);

		//URLを返す
		INTERACTION.getHook().editOriginal("[ここをクリックして認証してください](https://rumiserver.com/rumiabot/discord/verify_panel?ID=" + ID + ")").queue();
		System.out.println("a");
	}

	public static boolean VERIFY(String ID) {
		try{
			HashMap<String, String> VERIFY_DATA = VERIFY_LIST.get(ID);
			if(VERIFY_DATA != null){
				ArrayNode SQL_RESULT = SQL.RUN("SELECT * FROM `VERIFY_PANEL` WHERE `ID` = ?;", new Object[] {VERIFY_DATA.get("PANEL_ID")});

				if(SQL_RESULT.asArrayList().size() == 1) {
					ArrayNode RESULT = (ArrayNode)SQL_RESULT.asArrayList().get(0);

					if(RESULT != null){
						//鯖を探す
						for(Guild GUILD:DISCORD_BOT.getGuilds()){
							if(GUILD.getId().equals(RESULT.asString("GID"))){
								//鯖を見つけたので、メンバーとロールを探す
								Member MEMBER = GUILD.getMemberById(VERIFY_DATA.get("UID"));
								Role ROLE = GUILD.getRoleById(RESULT.asString("ROLE"));
								if(MEMBER != null && ROLE != null){
									//メンバーとロールを見つけたので、ロールを付与する
									GUILD.addRoleToMember(UserSnowflake.fromId(MEMBER.getId()), ROLE).queue();
									return true;
								}
							}
						}
					}
				}
			}

			//失敗したらここに来る
			return false;
		}catch (Exception EX){
			EX.printStackTrace();
			return false;
		}
	}
}
