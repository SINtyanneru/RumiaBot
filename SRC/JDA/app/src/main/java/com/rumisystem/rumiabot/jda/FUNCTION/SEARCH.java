package com.rumisystem.rumiabot.jda.FUNCTION;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.rumisystem.rumiabot.jda.CONFIG;
import com.rumisystem.rumiabot.jda.MODULE.HTTP_REQUEST;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.emoji.Emoji;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import org.asynchttpclient.util.UriEncoder;

public class SEARCH {
	public static void main(MessageReceivedEvent E) {
		try{
			E.getMessage().addReaction(Emoji.fromUnicode("✅")).queue();

			String SEARCH_Q = E.getMessage().getContentRaw().replace("検索 ", "");

			String AJAX = new HTTP_REQUEST("https://www.googleapis.com/customsearch/v1" +
					"?key=" + CONFIG.CONFIG_DATA.get("GOOGLE_SEARCH").get("GOOGLE_API_KEY").asText() +
					"&cx=" + CONFIG.CONFIG_DATA.get("GOOGLE_SEARCH").get("GOOGLE_API_ENGINE_ID").asText() +
					"&q=" + SEARCH_Q
			).GET();

			ObjectMapper OM = new ObjectMapper();
			JsonNode RESULT = OM.readTree(AJAX.toString());

			//埋め込み作成
			EmbedBuilder EB = new EmbedBuilder();
			EB.setTitle(SEARCH_Q);
			EB.setDescription("トータル：" + RESULT.get("queries").get("request").get(0).get("totalResults").asText());

			for(int I = 0; I < RESULT.get("items").size(); I++){
				JsonNode ROW = RESULT.get("items").get(I);
				EB.addField(ROW.get("title").asText(), ROW.get("snippet").asText() + "\n\n[見る](" + ROW.get("link").asText() + ")", false);
			}

			//返答
			E.getMessage().replyEmbeds(EB.build()).queue();
		}catch (Exception EX){
			EX.printStackTrace();
			E.getMessage().reply("エラー：" + EX.getMessage()).queue();
		}
	}
}
