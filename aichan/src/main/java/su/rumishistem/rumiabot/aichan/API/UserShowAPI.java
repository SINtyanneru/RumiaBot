package su.rumishistem.rumiabot.aichan.API;

import static su.rumishistem.rumiabot.System.Main.get_discord_bot;
import static su.rumishistem.rumiabot.System.Main.get_misskey_bot;

import java.util.LinkedHashMap;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

import su.rumishistem.rumi_java_lib.MisskeyBot.Type.User;
import su.rumishistem.rumi_java_lib.SmartHTTP.HTTP_REQUEST;
import su.rumishistem.rumi_java_lib.SmartHTTP.HTTP_RESULT;
import su.rumishistem.rumi_java_lib.SmartHTTP.Type.EndpointFunction;
import su.rumishistem.rumiabot.aichan.MisskeyAPIModoki;
import su.rumishistem.rumiabot.aichan.MODULE.ConvertType;

public class UserShowAPI implements EndpointFunction {
	@Override
	public HTTP_RESULT Run(HTTP_REQUEST r) throws Exception {
		try {
			JsonNode POST_BODY = new ObjectMapper().readTree(r.GetEVENT().getPOST_DATA());

			String UID = POST_BODY.get("userId").asText();

			if (UID.startsWith("M-")) {
				User MisskeyUser = get_misskey_bot().get_client().get_user_from_id(UID.replace("M-", ""));

				LinkedHashMap<String, Object> UserData = new LinkedHashMap<String, Object>();
				UserData.put("id", MisskeyUser.get_id());
				UserData.put("name", MisskeyUser.get_name());
				UserData.put("username", MisskeyUser.get_username());
				UserData.put("host", null);
				UserData.put("isFollowing", true);
				UserData.put("isBot", false);
				return new HTTP_RESULT(200, new ObjectMapper().writeValueAsString(UserData).getBytes(), MisskeyAPIModoki.JSONMime);
			} else if (UID.startsWith("D-")) {
				net.dv8tion.jda.api.entities.User DiscordUser = get_discord_bot().get_primary_bot().getUserById(UID.replace("D-", ""));

				return new HTTP_RESULT(200, new ObjectMapper().writeValueAsString(ConvertType.DiscordUserToRemoteUser(DiscordUser)).getBytes(), MisskeyAPIModoki.JSONMime);
			} else {
				return new HTTP_RESULT(200, "{}".getBytes(), MisskeyAPIModoki.JSONMime);
			}
		} catch (Exception EX) {
			EX.printStackTrace();
			return new HTTP_RESULT(500, "{}".getBytes(), MisskeyAPIModoki.JSONMime);
		}
	}
}
