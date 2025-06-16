package su.rumishistem.rumiabot.VXTwitterFunction;

import java.awt.Color;
import java.net.MalformedURLException;
import java.time.OffsetDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Locale;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.channel.concrete.TextChannel;
import net.dv8tion.jda.api.requests.restaction.WebhookMessageCreateAction;
import su.rumishistem.rumi_java_lib.FETCH;
import su.rumishistem.rumi_java_lib.FETCH_RESULT;
import su.rumishistem.rumiabot.System.Discord.MODULE.DiscordWebHook;
import su.rumishistem.rumiabot.System.TYPE.CommandInteraction;
import su.rumishistem.rumiabot.System.TYPE.DiscordFunction;
import su.rumishistem.rumiabot.System.TYPE.FunctionClass;
import su.rumishistem.rumiabot.System.TYPE.ReceiveMessageEvent;
import su.rumishistem.rumiabot.System.TYPE.SourceType;

public class Main implements FunctionClass {
	private static final String FUNCTION_NAME = "VXTwitter変換";
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
	}

	@Override
	public void ReceiveMessage(ReceiveMessageEvent e) {
		try {
			if (e.GetSource() == SourceType.Discord && !e.GetUser().isMe()) {
				if (e.GetMessage().CheckDiscordGuildFunctionEnabled(DiscordFunction.vxtwitter)) {
					//WebHook用意
					TextChannel Channel = (TextChannel) e.GetMessage().GetDiscordChannel();
					DiscordWebHook WH = new DiscordWebHook(Channel);
					StringBuilder Text = new StringBuilder();

					//TwitterのURLがあればAPIから持ってくる
					JsonNode Tweet = null;
					Matcher MTC = Pattern.compile("https://(?:twitter|x)\\.com/([a-zA-Z0-9_]+/status/[0-9]+)(\\?s=[0-9]*)?").matcher(e.GetMessage().GetText());
					if (MTC.find()) {
						FETCH Ajax = new FETCH("https://api.vxtwitter.com/" + MTC.group(1));
						FETCH_RESULT Result = Ajax.GET();
						if (Result.GetSTATUS_CODE() == 200) {
							Tweet = new ObjectMapper().readTree(Result.GetString());
							MTC.appendReplacement(Text, "[ツイート]("+MTC.group(0)+")");
						}
					}
					MTC.appendTail(Text);

					//ツイートのURLが無いなら終わり
					if (Tweet == null) return;

					//埋め込みを作る
					EmbedBuilder EB = new EmbedBuilder();
					EB.setUrl(Tweet.get("tweetURL").asText());
					EB.setColor(new Color(0x00ACEE));
					EB.setFooter("vxTwitter API 💖 "+Tweet.get("likes").asInt()+" 🔁 "+Tweet.get("retweets").asInt());
					EB.setTimestamp(OffsetDateTime.parse(Tweet.get("date").asText(), DateTimeFormatter.ofPattern("EEE MMM dd HH:mm:ss Z yyyy", Locale.ENGLISH)));

					EB.setAuthor(
						Tweet.get("user_name").asText(),
						"https://twitter.com/"+Tweet.get("user_screen_name").asText(),
						Tweet.get("user_profile_image_url").asText()
					);

					EB.setTitle(Tweet.get("text").asText());

					if (Tweet.get("hasMedia").asBoolean()) {
						for (int I = 0; I < Tweet.get("media_extended").size(); I++) {
							JsonNode Row = Tweet.get("media_extended").get(I);
							if (Row.get("type").asText().equals("video")) {
								Text.append("\n");
								Text.append("[動画"+(I+1)+"]("+Row.get("url").asText()+")");
							}
						}

						if (!Tweet.get("combinedMediaUrl").isNull()) {
							String CombinedMediaURL = Tweet.get("combinedMediaUrl").asText();
							EB.setImage(CombinedMediaURL);
						}
					}

					//送信
					WebhookMessageCreateAction<Message> MSG = WH.Send().sendMessage(Text.toString());
					MSG.setUsername(e.GetUser().GetName());
					MSG.setAvatarUrl(e.GetUser().GetIconURL());
					MSG.setEmbeds(EB.build());
					MSG.queue();

					//削除
					e.GetMessage().Delete();
				}
			}
		} catch (Exception EX) {
			EX.printStackTrace();
		}
	}
	@Override
	public boolean GetAllowCommand(String Name) {
		return false;
	}

	@Override
	public void RunCommand(CommandInteraction CI) throws Exception {
	}

}
