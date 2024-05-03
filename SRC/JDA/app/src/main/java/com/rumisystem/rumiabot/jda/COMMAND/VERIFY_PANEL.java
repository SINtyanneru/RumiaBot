package com.rumisystem.rumiabot.jda.COMMAND;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.rumisystem.rumiabot.jda.MODULE.RND_COLOR;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.Role;
import net.dv8tion.jda.api.entities.UserSnowflake;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.events.interaction.component.ButtonInteractionEvent;
import net.dv8tion.jda.api.interactions.components.buttons.Button;

import java.io.IOException;
import java.util.HashMap;
import java.util.UUID;

import static com.rumisystem.rumiabot.jda.Main.BOT;
import static com.rumisystem.rumiabot.jda.Main.URI_PARAM_PARSE;
import static com.rumisystem.rumiabot.jda.PT.SEND;

public class VERIFY_PANEL {
	private static HashMap<String, HashMap<String, String>> VERIFY_LIST = new HashMap<>();

	public static void PANEL_CREATE(SlashCommandInteractionEvent INTERACTION) throws IOException {
		//実行者は、ロールを操作する権限を持っているか
		if(INTERACTION.getMember().getPermissions().contains(Permission.MANAGE_ROLES)){
			String ID = UUID.randomUUID().toString();
			Role ROLE = INTERACTION.getOption("role").getAsRole();
			String GID = INTERACTION.getGuild().getId();
			String CID = INTERACTION.getChannel().getId();

			//そのロールをさわれるか(そのロールをロールを付与出来るか & 自分の一番最高権限でそのロールを付与できるか)
			if(INTERACTION.getGuild().getSelfMember().canInteract(ROLE) && INTERACTION.getGuild().getSelfMember().getRoles().get(0).canInteract(ROLE)){

				//すでにパネルが存在するかチェックするやつ
				String CHECK_SQL_RESULT = SEND("SQL;SELECT * FROM `VERIFY_PANEL` WHERE `GID` = ? AND `CID` = ?;[\"" + GID + "\", \"" + CID + "\"]");

				if(CHECK_SQL_RESULT.split(";")[CHECK_SQL_RESULT.split(";").length - 1].equals("200")) {
					//存在しない
					JsonNode RESULT = new ObjectMapper().readTree(CHECK_SQL_RESULT.split(";")[0]);
					if(RESULT.size() == 0){
						String SQL_RESULT = SEND("SQL_UP;INSERT INTO `VERIFY_PANEL` (`ID`, `ROLE`, `GID`, `CID`) VALUES (?, ?, ?, ?);[\"" + ID + "\", \"" + ROLE.getId() + "\", \"" + GID + "\", \"" + CID + "\"]");

						//応答は200か？
						if(SQL_RESULT.split(";")[SQL_RESULT.split(";").length - 1].equals("200")) {
							//200
							EmbedBuilder EB = new EmbedBuilder();
							EB.setColor(new RND_COLOR().GEN_COLOR());
							EB.setTitle("認証 ATESTADO");

							INTERACTION.getChannel().sendMessageEmbeds(EB.build()).addActionRow(Button.primary("verify_panel?id=" + ID, "認証")).queue();
							INTERACTION.getHook().deleteOriginal().queue();
						} else {//其れ以外
							INTERACTION.getHook().editOriginal("エラー" + SQL_RESULT.split(";")[0]).queue();
						}
					} else {//存在するので、もっかい出す
						JsonNode PANEL_DATA = RESULT.get(0);

						EmbedBuilder EB = new EmbedBuilder();
						EB.setColor(new RND_COLOR().GEN_COLOR());
						EB.setTitle("認証 ATESTADO");

						INTERACTION.getChannel().sendMessageEmbeds(EB.build()).addActionRow(Button.primary("verify_panel?id=" + PANEL_DATA.get("ID").asText(), "認証")).queue();

						INTERACTION.getHook().editOriginal("すでにあるので、呼び出しました").queue();
					}
				} else {
					INTERACTION.getHook().editOriginal("エラー：SQL_CHECK").queue();
				}
			} else {
				INTERACTION.getHook().editOriginal("ごめん、私そのロールを操作できないわ").queue();
			}
		} else {
			INTERACTION.getHook().editOriginal("お前は権限を持っていません").queue();
		}
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
		INTERACTION.getHook().editOriginal("[ここをクリックして認証してください](https://rumiserver.com/rumiabot/verify_panel?ID=" + ID + ")").queue();
	}

	public static boolean VERIFY(String ID) {
		try{
			HashMap<String, String> VERIFY_DATA = VERIFY_LIST.get(ID);
			if(VERIFY_DATA != null){
				String SQL_RESULT = SEND("SQL;SELECT * FROM `VERIFY_PANEL` WHERE `ID` = ?;[\"" + VERIFY_DATA.get("PANEL_ID") + "\"]");

				if(SQL_RESULT.split(";")[SQL_RESULT.split(";").length - 1].equals("200")) {
					JsonNode RESULT = new ObjectMapper().readTree(SQL_RESULT.split(";")[0]).get(0);
					if(RESULT != null){
						//鯖を探す
						for(Guild GUILD:BOT.getGuilds()){
							if(GUILD.getId().equals(RESULT.get("GID").asText())){
								//鯖を見つけたので、メンバーとロールを探す
								Member MEMBER = GUILD.getMemberById(VERIFY_DATA.get("UID"));
								Role ROLE = GUILD.getRoleById(RESULT.get("ROLE").asText());
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
