package su.rumishistem.rumiabot.aichan.MODULE;

import static su.rumishistem.rumiabot.System.Main.get_discord_bot;
import static su.rumishistem.rumiabot.System.Main.get_misskey_bot;

import java.time.Instant;
import java.time.OffsetDateTime;
import java.time.ZoneOffset;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.User;
import su.rumishistem.rumi_java_lib.MisskeyBot.Type.Note;
import su.rumishistem.rumiabot.System.Module.DateFormat;

public class ConvertType {
	private static OffsetDateTime ParseSnowFlake(User U) {
		long userId = Long.parseUnsignedLong(U.getId());
		long timestamp = (userId >> 22) + 1420070400000L;
		return Instant.ofEpochMilli(timestamp).atOffset(ZoneOffset.UTC);
	}

	public static JsonNode DiscordMessageToNote(Message M) throws JsonMappingException, JsonProcessingException {
		ObjectMapper OM = new ObjectMapper();
		LinkedHashMap<String, Object> Body = new LinkedHashMap<String, Object>();

		String Text = M.getContentRaw();
		Text = Text.replace("<@" + get_discord_bot().get_primary_bot().getSelfUser().getId() + ">", "@" + get_misskey_bot().get_client().get_self().get_user().get_username());

		Body.put("id", "D-" + M.getChannelId() + "_" + M.getId());
		Body.put("createdAt", DateFormat._12H(M.getTimeCreated()));
		Body.put("deletedAt", null);
		Body.put("text", Text);
		Body.put("cw", null);
		Body.put("userId", "D-" + M.getAuthor().getId());
		Body.put("user", DiscordUserToRemoteUser(M.getAuthor()));
		if (M.getReferencedMessage() != null) {
			Message Reply = M.getReferencedMessage();
			Body.put("replyId", M.getId());
			Body.put("reply", DiscordMessageToNote(Reply));
		} else {
			Body.put("replyId", null);
			Body.put("reply", null);
		}
		Body.put("renoteId", null);
		Body.put("renote", null);
		Body.put("isHidden", false);
		Body.put("visibility", "public");
		List<String> MentionList = new ArrayList<String>();
		for (int I = 0; I < M.getMentions().getUsers().size(); I++) {
			User MentionUser = M.getMentions().getUsers().get(I);
			MentionList.add(MentionUser.getId());
		}
		Body.put("mentions", MentionList);
		Body.put("files", new ArrayList<String>());
		Body.put("tags", new ArrayList<String>());
		Body.put("poll", new ArrayList<String>());
		Body.put("emojis", new ArrayList<String>());

		return OM.readTree(OM.writeValueAsString(Body));
	}

	public static JsonNode DiscordUserToRemoteUser(User U) throws JsonMappingException, JsonProcessingException {
		ObjectMapper OM = new ObjectMapper();
		LinkedHashMap<String, Object> Body = new LinkedHashMap<String, Object>();

		Body.put("id", "D-" + U.getId());
		Body.put("name", U.getGlobalName());
		Body.put("username", U.getName());
		Body.put("host", "discord.com");
		Body.put("avatarUrl", U.getEffectiveAvatarUrl());
		Body.put("avatarBlurhash", null);
		Body.put("avatarDecorations", new ArrayList<String>());
		Body.put("isBot", U.isBot());
		Body.put("isCat", false);
		Body.put("requireSigninToViewContents", false);
		Body.put("makeNotesFollowersOnlyBefore", null);
		Body.put("makeNotesHiddenBefore", null);
		Body.put("instance", new LinkedHashMap<String, Object>(){
			{
				put("name", "Discord");
				put("softwareName", "discord");
				put("softwareVersion", "1.0");
				put("iconUrl", null);
				put("faviconUrl", null);
				put("themeColor", "#5865F2");
			}
		});
		Body.put("onlineStatus", "unknown");
		Body.put("badgeRoles", new ArrayList<String>());
		Body.put("url", null);
		Body.put("uri", null);
		Body.put("movedTo", null);
		Body.put("createdAt", DateFormat._12H(ParseSnowFlake(U)));
		Body.put("description", "Discord user");
		Body.put("birthday", DateFormat._12H(ParseSnowFlake(U)));
		Body.put("lang", "ja-JP");
		Body.put("isFollowing", true);
		Body.put("isFollowed", true);

		return OM.readTree(OM.writeValueAsString(Body));
	}

	public static JsonNode NoteToNote(Note N) throws JsonMappingException, JsonProcessingException {
		ObjectMapper OM = new ObjectMapper();
		LinkedHashMap<String, Object> Body = new LinkedHashMap<String, Object>();

		Body.put("id", "M-" + N.get_id());
		Body.put("createdAt", "");
		Body.put("deletedAt", null);
		Body.put("text", N.get_text());
		Body.put("cw", null);
		Body.put("userId", "M-" + N.get_user().get_id());
		Body.put("user", NoteUserToUser(N.get_user()));
		try {
			Note ReplyNote = N.get_reply();
			Body.put("replyId", ReplyNote.get_id());
			Body.put("reply", NoteToNote(ReplyNote));
		} catch (Exception EX) {
			Body.put("replyId", null);
			Body.put("reply", null);
		}
		Body.put("renoteId", null);
		Body.put("renote", null);
		Body.put("isHidden", false);
		Body.put("visibility", "public");
		List<String> MentionList = new ArrayList<String>();
		/*for (int I = 0; I < M.getMentions().getUsers().size(); I++) {
			User MentionUser = M.getMentions().getUsers().get(I);
			MentionList.add(MentionUser.getId());
		}*/
		Body.put("mentions", MentionList);
		Body.put("files", new ArrayList<String>());
		Body.put("tags", new ArrayList<String>());
		Body.put("poll", new ArrayList<String>());
		Body.put("emojis", new ArrayList<String>());

		return OM.readTree(OM.writeValueAsString(Body));
	}

	public static JsonNode NoteUserToUser(su.rumishistem.rumi_java_lib.MisskeyBot.Type.User U) throws JsonMappingException, JsonProcessingException {
		ObjectMapper OM = new ObjectMapper();
		LinkedHashMap<String, Object> Body = new LinkedHashMap<String, Object>();

		Body.put("id", "M-" + U.get_id());
		Body.put("name", U.get_name());
		Body.put("username", U.get_username());
		if (U.is_local()) {
			Body.put("host", null);
		} else {
			Body.put("host", U.get_host());
		}
		Body.put("avatarUrl", U.get_icon_url());
		Body.put("avatarBlurhash", null);
		Body.put("avatarDecorations", new ArrayList<String>());
		Body.put("isBot", false);
		Body.put("isCat", false);
		Body.put("requireSigninToViewContents", false);
		Body.put("makeNotesFollowersOnlyBefore", null);
		Body.put("makeNotesHiddenBefore", null);
		Body.put("instance", new LinkedHashMap<String, Object>());
		Body.put("onlineStatus", "unknown");
		Body.put("badgeRoles", new ArrayList<String>());
		Body.put("url", null);
		Body.put("uri", null);
		Body.put("movedTo", null);
		Body.put("createdAt", "");
		Body.put("description", "Discord user");
		Body.put("birthday", "");
		Body.put("lang", "ja-JP");
		Body.put("isFollowing", true);
		Body.put("isFollowed", true);

		return OM.readTree(OM.writeValueAsString(Body));
	}
}
