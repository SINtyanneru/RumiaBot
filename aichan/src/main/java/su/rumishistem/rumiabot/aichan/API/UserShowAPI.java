package su.rumishistem.rumiabot.aichan.API;

import static su.rumishistem.rumiabot.System.Main.DISCORD_BOT;
import static su.rumishistem.rumiabot.System.Main.MisskeyBOT;

import java.util.LinkedHashMap;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

import su.rumishistem.rumi_java_lib.Misskey.TYPE.User;
import su.rumishistem.rumi_java_lib.SmartHTTP.HTTP_REQUEST;
import su.rumishistem.rumi_java_lib.SmartHTTP.HTTP_RESULT;
import su.rumishistem.rumi_java_lib.SmartHTTP.Type.EndpointFunction;
import su.rumishistem.rumiabot.aichan.MisskeyAPIModoki;

public class UserShowAPI implements EndpointFunction {
	@Override
	public HTTP_RESULT Run(HTTP_REQUEST r) throws Exception {
		try {
			JsonNode POST_BODY = new ObjectMapper().readTree(r.GetEVENT().getPOST_DATA());

			LinkedHashMap<String, Object> UserData = new LinkedHashMap<String, Object>();

			String UID = POST_BODY.get("userId").asText();
			System.out.println(UID);
			if (UID.startsWith("M-")) {
				User MisskeyUser = MisskeyBOT.GetUserID(UID.replace("M-", ""));
				UserData.put("id", MisskeyUser.getID());
				UserData.put("name", MisskeyUser.getNAME());
				UserData.put("username", MisskeyUser.getUID());
				UserData.put("host", null);
				UserData.put("isFollowing", true);
				UserData.put("isBot", false);
			} else if (UID.startsWith("D-")) {
				net.dv8tion.jda.api.entities.User DiscordUser = DISCORD_BOT.getUserById(UID.replace("D-", ""));
				UserData.put("id", DiscordUser.getId());
				UserData.put("name", DiscordUser.getName());
				UserData.put("username", DiscordUser.getGlobalName());
				UserData.put("host", "discord.com");
				UserData.put("isFollowing", true);
				UserData.put("isBot", DiscordUser.isBot());
			} else {
				throw new Error("MisskeyでもDiscordでもない");
			}

			return new HTTP_RESULT(200, new ObjectMapper().writeValueAsString(UserData).getBytes(), MisskeyAPIModoki.JSONMime);
		} catch (Exception EX) {
			EX.printStackTrace();
			return new HTTP_RESULT(500, "{}".getBytes(), MisskeyAPIModoki.JSONMime);
		}
	}
}
