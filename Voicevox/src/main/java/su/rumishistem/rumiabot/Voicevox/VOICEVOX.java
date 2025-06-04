package su.rumishistem.rumiabot.Voicevox;

import static su.rumishistem.rumiabot.System.Main.CONFIG_DATA;

import java.io.File;
import java.io.FileOutputStream;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.UUID;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

import su.rumishistem.rumi_java_lib.FETCH;
import su.rumishistem.rumi_java_lib.FETCH_RESULT;

public class VOICEVOX {
	private static String genURL() {
		String Host = CONFIG_DATA.get("VOICEVOX").getData("HOST").asString();
		String Port = CONFIG_DATA.get("VOICEVOX").getData("PORT").asString();
		return "http://" + Host + ":" + Port + "/";
	}

	public static List<HashMap<String, String>> getSpeakers() {
		List<HashMap<String, String>> SpeakersList = new ArrayList<HashMap<String,String>>();

		try {
			FETCH Ajax = new FETCH(genURL()+"speakers");
			JsonNode Body = new ObjectMapper().readTree(Ajax.GET().GetString());

			for (int I = 0; I < Body.size(); I++) {
				JsonNode Row = Body.get(I);

				HashMap<String, String> Speakers = new HashMap<String, String>();
				Speakers.put("ID", Row.get("speaker_uuid").asText());
				Speakers.put("NAME", Row.get("name").asText());
				Speakers.put("VERSION", Row.get("version").asText());
				Speakers.put("DEFAULT_STYLE", Row.get("styles").get(0).get("id").asText());
				SpeakersList.add(Speakers);
			}
		} catch (Exception EX) {
			EX.printStackTrace();
		}

		return SpeakersList;
	}

	public static String genAudioQuery(int SpeakersIndex, String Text) {
		try {
			FETCH Ajax = new FETCH(genURL()+"audio_query?text=" + URLEncoder.encode(Text) + "&speaker=" + SpeakersIndex);
			FETCH_RESULT Result = Ajax.POST(new byte[] {});

			if (Result.GetSTATUS_CODE() == 200) {
				return Result.GetString().replace("\"text\":\"オ\"", "\"text\":\"ウォ\"");
			} else {
				throw new Error("AudioQueryエラー:" + Result.GetString());
			}
		} catch (Exception EX) {
			EX.printStackTrace();
			throw new Error("AudioQueryエラー");
		}
	}

	public static File genAudio(int SpeakersIndex, String AudioQuery) {
		try {
			FETCH Ajax = new FETCH(genURL()+"synthesis?speaker=" + SpeakersIndex);
			Ajax.SetHEADER("Content-Type", "application/json; charset=UTF-8");
			FETCH_RESULT Result = Ajax.POST(AudioQuery.getBytes());

			if (Result.GetSTATUS_CODE() == 200) {
				File F = new File("/tmp/" + UUID.randomUUID().toString());

				FileOutputStream FOS = new FileOutputStream(F);
				FOS.write(Result.GetRAW());
				FOS.flush();
				FOS.close();

				return F;
			} else {
				throw new Error("AudioQueryエラー:" + Result.GetString());
			}
		} catch (Exception EX) {
			EX.printStackTrace();
			throw new Error("AudioQueryエラー");
		}
	}
}
