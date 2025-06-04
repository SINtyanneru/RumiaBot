package su.rumishistem.rumiabot.Voicevox.Jomiage;

import static su.rumishistem.rumiabot.Voicevox.Main.SpeakersList;
import static su.rumishistem.rumiabot.System.Main.DISCORD_BOT;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CountDownLatch;
import java.util.function.Consumer;

import com.sedmelluq.discord.lavaplayer.player.AudioLoadResultHandler;
import com.sedmelluq.discord.lavaplayer.player.AudioPlayer;
import com.sedmelluq.discord.lavaplayer.player.AudioPlayerManager;
import com.sedmelluq.discord.lavaplayer.player.event.AudioEventAdapter;
import com.sedmelluq.discord.lavaplayer.tools.FriendlyException;
import com.sedmelluq.discord.lavaplayer.track.AudioPlaylist;
import com.sedmelluq.discord.lavaplayer.track.AudioTrack;
import com.sedmelluq.discord.lavaplayer.track.AudioTrackEndReason;

import net.dv8tion.jda.api.entities.channel.concrete.TextChannel;
import net.dv8tion.jda.api.managers.AudioManager;
import su.rumishistem.rumiabot.Voicevox.VOICEVOX;

public class JomiageData {
	private AudioManager AM = null;
	private String KikisenID = null;
	private AudioPlayerManager APM = null;
	private AudioPlayer AP = null;
	private int UserVoiceLast = 0;
	private HashMap<String, Integer> UserVoiceTable = null;
	private QueueList<HashMap<String, String>> MessageQueue = null;

	public JomiageData(AudioManager AM, AudioPlayerManager APM, AudioPlayer AP, String KikisenID) {
		this.AM = AM;
		this.APM = APM;
		this.AP = AP;
		this.KikisenID = KikisenID;

		UserVoiceTable = new HashMap<String, Integer>();
		MessageQueue = new QueueList<HashMap<String,String>>();

		MessageQueue.setOnAdd(new Consumer<HashMap<String,String>>() {
			@Override
			public void accept(HashMap<String, String> a) {
				try {
					for (HashMap<String, String> Row:MessageQueue) {
						CountDownLatch Latch = new CountDownLatch(1);

						String UID = Row.get("UID");
						String Text = Row.get("TEXT");
						int VoiceSpeakers = (int)getUserVoice(UID).get("SPEAKERS");

						//ファイルを錬成して再生
						File F = VOICEVOX.genAudioFile(VoiceSpeakers, VOICEVOX.genAudioQuery(VoiceSpeakers, Text));
						APM.loadItem(F.toString(), new AudioLoadResultHandler() {
							@Override
							public void trackLoaded(AudioTrack Track) {
								//再生
								AP.playTrack(Track);

								//再生終わったら削除
								AP.addListener(new AudioEventAdapter() {
									@Override
									public void onTrackEnd(AudioPlayer Player, AudioTrack Track, AudioTrackEndReason EndReason) {
										F.delete();
										Latch.countDown();
									}
								});
							}
							@Override
							public void playlistLoaded(AudioPlaylist Playlist) {}
							@Override
							public void noMatches() {}
							@Override
							public void loadFailed(FriendlyException EX) {}
						});

						Latch.await();
						MessageQueue.RemoveFirst();
					}
				} catch (Exception EX) {
					EX.printStackTrace();
				}
			}
		});
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
			Return.put("NAME", "実装がだるい");
		}

		return Return;
	}

	public void addMessage(String UID, String Text) {
		HashMap<String, String> Data = new HashMap<String, String>();
		Data.put("UID", UID);
		Data.put("TEXT", Text);
		MessageQueue.add(Data);
	}
}
