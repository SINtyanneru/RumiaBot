package su.rumishistem.rumisanbot.Misskey;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.HashMap;
import java.util.Map;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

public class API {
	private static final ObjectMapper om = new ObjectMapper();
	private static final HttpClient http = HttpClient.newHttpClient();
	private static String host = null;
	private static String token = null;
	private static String admin_token = null;

	public static void set_host(String host) {
		API.host = host;
	}

	public static void set_token(String token) {
		API.token = token;
	}

	public static void set_admin_token(String admin_token) {
		API.admin_token = admin_token;
	}

	public static JsonNode run(String path, Map<String, Object> body) throws IOException, InterruptedException {
		return api_run(path, token, body);
	}

	public static JsonNode admin_run(String path, Map<String, Object> body) throws IOException, InterruptedException {
		return api_run(path, admin_token, body);
	}

	private static JsonNode api_run(String path, String token, Map<String, Object> body) throws IOException, InterruptedException {
		if (body == null) body = new HashMap<>();
		body.put("i", token);

		try {
			HttpRequest.Builder builder = HttpRequest.newBuilder();
			builder.POST(HttpRequest.BodyPublishers.ofString(om.writeValueAsString(body)));
			builder.uri(URI.create("https://"+host+"/api/"+path));
			builder.header("Content-Type", "application/json; charset=UTF-8");
			builder.header("Accept", "application/json; charset=UTF-8");

			HttpResponse<String> response = http.send(builder.build(), HttpResponse.BodyHandlers.ofString());
			return om.readTree(response.body());
		} catch (JsonProcessingException ex) {
			return null;
		}
	}

	public static String create_note(String text, String reply, String quote, String public_setting, boolean local_only) throws IOException, InterruptedException {
		JsonNode r = run("notes/create", new HashMap<>(){{
			put("text", text);
			put("cw", null);
			put("localOnly", local_only);
			put("visibility", public_setting.toLowerCase());

			if (reply != null) put("replyId", reply);
			if (quote != null) put("renoteId", quote);
		}});

		if (r.get("error") == null) {
			return r.get("createdNote").get("id").asText();
		} else {
			throw new RuntimeException("エラー: " + r);
		}
	}

	public static void create_reaction(String note_id, String reaction_id) throws IOException, InterruptedException {
		JsonNode r = run("notes/reactions/create", new HashMap<>(){{
			put("noteId", note_id);
			put("reaction", reaction_id);
		}});

		if (r.get("error") != null) {
			throw new RuntimeException("エラー: " + r);
		}
	}

	public static void follow(String user_id) throws IOException, InterruptedException {
		JsonNode r = run("following/create", new HashMap<>(){{
			put("userId", user_id);
		}});

		if (r.get("error") != null) {
			throw new RuntimeException("エラー: " + r);
		}
	}

	public static void unfollow(String user_id) throws IOException, InterruptedException {
		JsonNode r = run("following/delete", new HashMap<>(){{
			put("userId", user_id);
		}});

		if (r.get("error") != null) {
			throw new RuntimeException("エラー: " + r);
		}
	}

	public static void block(String user_id) throws IOException, InterruptedException {
		JsonNode r = run("blocking/create", new HashMap<>(){{
			put("userId", user_id);
		}});

		if (r.get("error") != null) {
			throw new RuntimeException("エラー: " + r);
		}
	}

	public static void unblock(String user_id) throws IOException, InterruptedException {
		JsonNode r = run("blocking/delete", new HashMap<>(){{
			put("userId", user_id);
		}});

		if (r.get("error") != null) {
			throw new RuntimeException("エラー: " + r);
		}
	}
}
