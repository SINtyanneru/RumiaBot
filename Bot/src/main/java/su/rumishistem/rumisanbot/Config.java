package su.rumishistem.rumisanbot;

import java.io.BufferedReader;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.HashMap;

public class Config {
	public static void load() throws IOException {
		HashMap<String, Object> config = new HashMap<>();

		Path path = Path.of("./Config.ini");
		BufferedReader br = Files.newBufferedReader(path);

		try {
			String field = null;
			String line;
			while ((line = br.readLine()) != null) {
				if (line.equals("")) continue;

				if (line.startsWith("[") && line.endsWith("]")) {
					field = line.substring(1, line.length() - 1);
				} else {
					int index = line.indexOf('=');
					String key = field + "." + line.substring(0, index);
					String value = line.substring(index + 1);

					if (value.startsWith("\"") && value.endsWith("\"")) {
						config.put(key, value.substring(1, value.length() - 1));
					} else if (value.equals("true") || value.equals("false")) {
						config.put(key, value.equals("true"));
					} else {
						config.put(key, Integer.parseInt(value));
					}
				}
			}
		} finally {
			br.close();
		}

		Discord.token = (String)config.get("DISCORD.TOKEN");

		Misskey.host = (String)config.get("MISSKEY.DOMAIN");
		Misskey.token = (String)config.get("MISSKEY.TOKEN");
		Misskey.admin_token = (String)config.get("MISSKEY.ADMINTOKEN");
	}

	public class Discord {
		public static String token = null;
	}

	public class Misskey {
		public static String host = null;
		public static String token = null;
		public static String admin_token = null;
	}
}
