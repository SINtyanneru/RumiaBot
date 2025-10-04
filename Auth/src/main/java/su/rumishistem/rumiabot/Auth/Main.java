package su.rumishistem.rumiabot.Auth;

import static su.rumishistem.rumiabot.System.FunctionModuleLoader.AddCommand;
import static su.rumishistem.rumiabot.System.Main.SH;
import static su.rumishistem.rumiabot.System.Main.CONFIG_DATA;

import java.util.HashMap;
import java.util.UUID;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.common.base.Charsets;

import su.rumishistem.rumi_java_lib.FETCH;
import su.rumishistem.rumi_java_lib.FETCH_RESULT;
import su.rumishistem.rumi_java_lib.SQL;
import su.rumishistem.rumi_java_lib.SnowFlake;
import su.rumishistem.rumi_java_lib.RESOURCE.RESOURCE_MANAGER;
import su.rumishistem.rumi_java_lib.SmartHTTP.*;
import su.rumishistem.rumi_java_lib.SmartHTTP.Type.EndpointFunction;
import su.rumishistem.rumi_java_lib.SmartHTTP.Type.EndpointEntrie.Method;
import su.rumishistem.rumiabot.System.TYPE.*;

public class Main implements FunctionClass{
	private static final String FUNCTION_NAME = "認証";
	private static final String FUNCTION_VERSION = "1.0";
	private static final String FUNCTION_AUTOR = "Rumisan";

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
		AddCommand(new CommandData("auth", new CommandOption[] {}, true));

		SH.SetRoute("/user/auth", Method.GET, new EndpointFunction() {
			@Override
			public HTTP_RESULT Run(HTTP_REQUEST r) throws Exception {
				String document = new String(new RESOURCE_MANAGER(Main.class).getResourceData("/auth.html"), Charsets.UTF_8);
				document = document.replace("$URL", "https://account.rumiserver.com/Auth/?ID="+CONFIG_DATA.get("RSV").getData("ID").asString()+"&SESSION="+UUID.randomUUID().toString()+"&PERMISSION=account%3Aread&CALLBACK=https%3A%2F%2Fbot.rumi-room.net%2Fauth_callback");

				return new HTTP_RESULT(200, document.getBytes(Charsets.UTF_8), "text/html; charset=UTF-8");
			}
		});

		SH.SetRoute("/user/auth_callback", Method.GET, new EndpointFunction() {
			@Override
			public HTTP_RESULT Run(HTTP_REQUEST r) throws Exception {
				try {
					if (r.GetEVENT().getURI_PARAM().get("SESSION") == null) throw new RuntimeException("400");
					String session_id = r.GetEVENT().getURI_PARAM().get("SESSION");
					String app_id = CONFIG_DATA.get("RSV").getData("ID").asString();
					String app_token = CONFIG_DATA.get("RSV").getData("TOKEN").asString();

					//POSTする内容
					HashMap<String, String> post_body = new HashMap<String, String>();
					post_body.put("APP", app_id);
					post_body.put("TOKEN", app_token);
					post_body.put("SESSION", session_id);
					FETCH check_ajax = new FETCH("https://account.rumiserver.com/api/AUTH/Check");
					FETCH_RESULT check_result = check_ajax.POST(new ObjectMapper().writeValueAsString(post_body).getBytes());
					JsonNode check = new ObjectMapper().readTree(check_result.getString());
					if (check.get("STATUS").asBoolean() == false) throw new RuntimeException("ERR");

					String token = check.get("TOKEN").asText();
					FETCH ajax = new FETCH("https://account.rumiserver.com/api/Session?ID="+token);
					FETCH_RESULT result = ajax.GET();
					JsonNode body = new ObjectMapper().readTree(result.getString());
					if (body.get("STATUS").asBoolean() == false) throw new RuntimeException("a");
					String rsv_uid = body.get("ACCOUNT_DATA").get("ID").asText();

					if (SQL.RUN("SELECT * FROM `USER` WHERE `RUMISERVER_ID` = ?;", new Object[] {rsv_uid}).length() == 0) {
						SQL.UP_RUN("""
							INSERT
								INTO `USER` (`ID`, `RUMISERVER_ID`, `DISCORD_ID`, `FEDIVERSE_ID`)
							VALUES
								(?, ?, NULL, NULL)
						""", new Object[] {
							String.valueOf(SnowFlake.GEN()),
							rsv_uid
						});
					}

					//くっきー
					r.GetEVENT().setCookie("RB-TOKEN", token, 999999999999l, "bot.rumi-room.net", "/", true, false);

					r.GetEVENT().setHEADER("Location", "/dashboard");

					return new HTTP_RESULT(302, "OK".getBytes(), "text/plain; charset=UTF-8");
				} catch (RuntimeException EX) {
					return new HTTP_RESULT(400, "ERROR".getBytes(), "text/plain; charset=UTF-8");
				}
			}
		});
	}

	@Override
	public void ReceiveMessage(ReceiveMessageEvent e) {}

	@Override
	public boolean GetAllowCommand(String Name) {
		return Name.equals("auth");
	}

	@Override
	public void RunCommand(CommandInteraction CI) throws Exception {
		StringBuilder sb = new StringBuilder();
		sb.append("るみさんBOTに、るみ鯖アカウントとDiscordアカウントを使用してユーザー登録が出来ます。").append("\n");
		sb.append("・手順").append("\n");
		sb.append("1：[るみ鯖アカウント](https://account.rumiserver.com/)を用意します").append("\n");
		sb.append("2：[ここ](https://bot.rumi-room.net/auth)でるみ鯖アカウントでログインします").append("\n");
		sb.append("※これにより、るみさんBOTにユーザー登録が出来ます。").append("\n");
		sb.append("3：るみさんBOTのダッシュボードで、Discordアカウントでログインします").append("\n");

		CI.Reply(sb.toString());
	}
}
