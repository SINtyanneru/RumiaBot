package su.rumishistem.rumiabot.Auth;

import static su.rumishistem.rumiabot.System.FunctionModuleLoader.AddCommand;
import static su.rumishistem.rumiabot.System.Main.SH;

import java.nio.charset.Charset;
import java.util.UUID;

import com.google.common.base.Charsets;

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
				document = document.replace("$URL", "https://account.rumiserver.com/Auth/?ID=9108802106801147393&SESSION="+UUID.randomUUID().toString()+"&PERMISSION=account%3Aread&CALLBACK=https%3A%2F%2Fbot.rumi-room.net%2Fauth_callback");

				return new HTTP_RESULT(200, document.getBytes(Charsets.UTF_8), "text/html; charset=UTF-8");
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
