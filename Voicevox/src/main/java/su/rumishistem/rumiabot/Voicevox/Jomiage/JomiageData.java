package su.rumishistem.rumiabot.Voicevox.Jomiage;

import static su.rumishistem.rumiabot.Voicevox.Main.SpeakersList;
import static su.rumishistem.rumiabot.System.Main.DISCORD_BOT;
import java.util.HashMap;
import com.sedmelluq.discord.lavaplayer.player.AudioPlayer;
import com.sedmelluq.discord.lavaplayer.player.AudioPlayerManager;
import net.dv8tion.jda.api.entities.channel.concrete.TextChannel;
import net.dv8tion.jda.api.managers.AudioManager;

public class JomiageData {
	private AudioManager AM = null;
	private String KikisenID = null;
	private AudioPlayerManager APM = null;
	private AudioPlayer AP = null;
	private int UserVoiceLast = 0;
	private HashMap<String, Integer> UserVoiceTable = null;

	public JomiageData(AudioManager AM, AudioPlayerManager APM, AudioPlayer AP, String KikisenID) {
		this.AM = AM;
		this.APM = APM;
		this.AP = AP;
		this.KikisenID = KikisenID;

		UserVoiceTable = new HashMap<String, Integer>();
	}

	public AudioManager getAM() {
		return AM;
	}

	public AudioPlayerManager getAPM() {
		return APM;
	}

	public AudioPlayer getAP() {
		return AP;
	}

	public TextChannel getKikisen() {
		return DISCORD_BOT.getTextChannelById(KikisenID);
	}

	public boolean UserVoiceIsNone(String UID) {
		return UserVoiceTable.get(UID) == null;
	}

	public HashMap<String, Object> getUserVoice(String UID) {
		HashMap<String, Object> Return = new HashMap<String, Object>();

		if (UserVoiceIsNone(UID)) {
			HashMap<String, String> Speakers = SpeakersList.get(UserVoiceLast);
			int SpeakersID = UserVoiceLast;
			int SpeakersStyleID = Integer.parseInt(Speakers.get("DEFAULT_STYLE"));

			UserVoiceTable.put(UID, SpeakersStyleID);

			Return.put("SPEAKERS", SpeakersStyleID);
			Return.put("NAME", SpeakersList.get(SpeakersID).get("NAME"));

			UserVoiceLast++;
		} else {
			Return.put("SPEAKERS", UserVoiceTable.get(UID));
			//Return.put("NAME", SpeakersList.get(UserVoiceTable.get(UID)).get("NAME"));
			Return.put("NAME", "実装がだるい");
		}

		return Return;
	}
}
