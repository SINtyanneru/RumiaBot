package su.rumishistem.rumiabot.VerifyPanelFunction;

import static su.rumishistem.rumiabot.System.Main.DISCORD_BOT;
import static su.rumishistem.rumiabot.System.Main.CONFIG_DATA;
import static su.rumishistem.rumiabot.System.Main.SH;
import java.util.function.Function;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.common.base.Charsets;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.Role;
import net.dv8tion.jda.api.entities.UserSnowflake;
import net.dv8tion.jda.api.events.interaction.component.ButtonInteractionEvent;
import su.rumishistem.rumi_java_lib.ArrayNode;
import su.rumishistem.rumi_java_lib.FETCH;
import su.rumishistem.rumi_java_lib.FETCH_RESULT;
import su.rumishistem.rumi_java_lib.SQL;
import su.rumishistem.rumi_java_lib.RESOURCE.RESOURCE_ENTRIE;
import su.rumishistem.rumi_java_lib.RESOURCE.RESOURCE_MANAGER;
import su.rumishistem.rumi_java_lib.SmartHTTP.HTTP_REQUEST;
import su.rumishistem.rumi_java_lib.SmartHTTP.HTTP_RESULT;
import static su.rumishistem.rumiabot.System.FunctionModuleLoader.AddCommand;
import su.rumishistem.rumiabot.System.TYPE.CommandData;
import su.rumishistem.rumiabot.System.TYPE.CommandInteraction;
import su.rumishistem.rumiabot.System.TYPE.CommandOption;
import su.rumishistem.rumiabot.System.TYPE.FunctionClass;
import su.rumishistem.rumiabot.System.TYPE.ReceiveMessageEvent;
import su.rumishistem.rumiabot.System.TYPE.SourceType;
import java.util.HashMap;
import java.util.UUID;

public class Main implements FunctionClass {
	private static final String FUNCTION_NAME = "認証パネル";
	private static final String FUNCTION_VERSION = "1.0";
	private static final String FUNCTION_AUTOR = "Rumisan";
	private static HashMap<String, HashMap<String, String>> VerifyQueue = new HashMap<>();

	@Override
	public String FUNCTION_NAME() {
		return FUNCTION_NAME;
	}
	@Override
	public String FUNCTION_VERSION() {
		return FUNCTION_VERSION;
	}
	@Override
	public String FUNCTION_AUTOR() {
		return FUNCTION_AUTOR;
	}

	@Override
	public void Init() {
		AddCommand(new CommandData("veryfy_panel", new CommandOption[] {}, false));

		//認証ページひらいた時
		SH.SetRoute("/user/discord/verify_panel", new Function<HTTP_REQUEST, HTTP_RESULT>() {
			@Override
			public HTTP_RESULT apply(HTTP_REQUEST t) {
				try {
					String BODY = new String(new RESOURCE_MANAGER(Main.class).getResourceData("/discord_verify_panel.html"));
					BODY = BODY.replace("${SITE_KEY}", CONFIG_DATA.get("CAPTCHA").getData("SITE_KEY").asString());
					return new HTTP_RESULT(200, BODY.getBytes(), "text/html; charset=UTF-8");
				} catch (Exception EX) {
					EX.printStackTrace();
					return new HTTP_RESULT(500, "Err".getBytes(), "text/plain; charset=UTF-8");
				}
			}
		});

		//認証ページで認証をした後の処理
		SH.SetRoute("/user/api/VERIFY_PANEL", new Function<HTTP_REQUEST, HTTP_RESULT>() {
			@Override
			public HTTP_RESULT apply(HTTP_REQUEST r) {
				try {
					JsonNode POST_DATA = new ObjectMapper().readTree(r.GetEVENT().getPOST_DATA());
					if (POST_DATA.get("CFT_RESULT") == null || POST_DATA.get("VERIFY_ID") == null) {
						return new HTTP_RESULT(400, "{\"STATUS\": false}".getBytes(), "application/json; charset=UTF-8");
					}

					//CFTを検証
					FETCH AJAX = new FETCH("https://challenges.cloudflare.com/turnstile/v0/siteverify");
					AJAX.SetHEADER("Content-Type", "application/json");
					//いざPOST
					HashMap<String, String> AJAX_BODY = new HashMap<String, String>();
					AJAX_BODY.put("response", POST_DATA.get("CFT_RESULT").asText());
					AJAX_BODY.put("secret", CONFIG_DATA.get("CAPTCHA").getData("SIKRET_KEY").asString());

					FETCH_RESULT CFT_RESULT = AJAX.POST(new ObjectMapper().writeValueAsString(AJAX_BODY).getBytes(Charsets.UTF_8));
					if (new ObjectMapper().readTree(CFT_RESULT.GetString()).get("success").asBoolean()) {
						//成功
						if (VerifyDone(POST_DATA.get("VERIFY_ID").asText())) {
							return new HTTP_RESULT(200, "{\"STATUS\": true}".getBytes(), "application/json; charset=UTF-8");
						} else {
							return new HTTP_RESULT(400, "{\"STATUS\": false}".getBytes(), "application/json; charset=UTF-8");
						}
					} else {
						//失敗
						return new HTTP_RESULT(400, "{\"STATUS\": false}".getBytes(), "application/json; charset=UTF-8");
					}
				} catch (Exception EX) {
					EX.printStackTrace();
					return new HTTP_RESULT(500, "{\"STATUS\": false}".getBytes(), "application/json; charset=UTF-8");
				}
			}
		});
	}

	@Override
	public void ReceiveMessage(ReceiveMessageEvent e) {
	}

	@Override
	public boolean GetAllowCommand(String Name) {
		return (Name.equals("verify_panel") || Name.equals("Button:verify_panel"));
	}

	@Override
	public void RunCommand(CommandInteraction CI) throws Exception {
		if (CI.GetSource() == SourceType.Discord) {
			//NOTE:過去の実装→https://github.com/SINtyanneru/RumiaBot/blob/7be947e8ae0d77065e793826d06eabea754538c2/src/main/java/su/rumishistem/rumiabot/Discord/FUNCTION/VERIFY_PANEL.java
			CI.Reply("新規作成は未実装です");
		} else {
			CI.Reply("Discordでのみ使用できます!");
		}
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

	@Override
	public void RunButton(ButtonInteractionEvent BI) {
		HashMap<String, String> PARAM = URI_PARAM_PARSE(BI.getInteraction().getButton().getId().toString());

		String ID = UUID.randomUUID().toString();

		//認証データを作る
		HashMap<String, String> VERIFY_DATA = new HashMap<>();
		VERIFY_DATA.put("PANEL_ID", PARAM.get("id"));
		VERIFY_DATA.put("UID", BI.getUser().getId());

		//認証の受付リストに入れる
		VerifyQueue.put(ID, VERIFY_DATA);

		//URLを返す
		BI.reply("[ここをクリックして認証してください](https://rumiserver.com/rumiabot/discord/verify_panel?ID=" + ID + ")").setEphemeral(true).queue();
	}

	public static boolean VerifyDone(String ID) {
		try{
			HashMap<String, String> VERIFY_DATA = VerifyQueue.get(ID);
			if(VERIFY_DATA != null){
				ArrayNode SQL_RESULT = SQL.RUN("SELECT * FROM `VERIFY_PANEL` WHERE `ID` = ?;", new Object[] {VERIFY_DATA.get("PANEL_ID")});

				if(SQL_RESULT.asArrayList().size() == 1) {
					ArrayNode RESULT = (ArrayNode)SQL_RESULT.asArrayList().get(0);

					if(RESULT != null){
						//鯖を探す
						for(Guild GUILD:DISCORD_BOT.getGuilds()){
							if(GUILD.getId().equals(RESULT.getData("GID").asString())){
								//鯖を見つけたので、メンバーとロールを探す
								Member MEMBER = GUILD.getMemberById(VERIFY_DATA.get("UID"));
								Role ROLE = GUILD.getRoleById(RESULT.getData("ROLE").asString());
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
