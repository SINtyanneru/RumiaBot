package su.rumishistem.rumiabot.Voicevox.Jomiage;

import static su.rumishistem.rumiabot.Voicevox.Main.SpeakersList;
import static su.rumishistem.rumi_java_lib.LOG_PRINT.Main.LOG;
import static su.rumishistem.rumiabot.System.Main.DISCORD_BOT;

import java.io.File;
import java.util.*;
import java.util.concurrent.*;

import com.sedmelluq.discord.lavaplayer.player.*;
import com.sedmelluq.discord.lavaplayer.player.event.AudioEventAdapter;
import com.sedmelluq.discord.lavaplayer.track.*;
import com.sedmelluq.discord.lavaplayer.tools.FriendlyException;

import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.channel.concrete.TextChannel;
import net.dv8tion.jda.api.managers.AudioManager;
import su.rumishistem.rumi_java_lib.LOG_PRINT.LOG_TYPE;
import su.rumishistem.rumiabot.Voicevox.VOICEVOX;

public class JomiageData {
	private AudioManager am = null;
	private String KikisenID = null;
	private AudioPlayerManager apm = null;
	private AudioPlayer player = null;
	private int UserVoiceLast = 0;
	private HashMap<String, Integer> UserVoiceTable = null;
	private List<File> messgae_queue = new ArrayList<>();
	private ThreadPoolExecutor playing_pool = new ThreadPoolExecutor(
		1, 1, 0L, TimeUnit.MILLISECONDS,
		new LinkedBlockingQueue<>()
	);

	public JomiageData(Guild guild, AudioManager am, AudioPlayerManager apm, AudioPlayer player, String KikisenID) {
		this.am = am;
		this.apm = apm;
		this.player = player;
		this.KikisenID = KikisenID;
		UserVoiceTable = new HashMap<String, Integer>();

		//終了時に終了処理を行う
		JomiageData ctx = this;
		Runtime.getRuntime().addShutdownHook(new Thread(new Runnable() {
			@Override
			public void run() {
				ctx.close();
				LOG(LOG_TYPE.OK, "読み上げBOTの終了処理した:" + guild.getId());
			}
		}));
	}

	public AudioManager get_am() {
		return am;
	}

	public AudioPlayerManager get_apm() {
		return apm;
	}

	public AudioPlayer getAP() {
		return player;
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

	public void addMessage(String uid, String text) {
		int VoiceSpeakers = (int)getUserVoice(uid).get("SPEAKERS");
		File f = VOICEVOX.genAudioFile(VoiceSpeakers, VOICEVOX.genAudioQuery(VoiceSpeakers, text));
		messgae_queue.add(f);

		playing_pool.submit(new Runnable() {
			@Override
			public void run() {
				CountDownLatch cdl = new CountDownLatch(1);
				File f = messgae_queue.getFirst();
				messgae_queue.removeFirst();

				apm.loadItem(f.toString(), new AudioLoadResultHandler() {
					@Override
					public void trackLoaded(AudioTrack track) {
						//再生
						player.playTrack(track);

						//再生終わったら削除
						player.addListener(new AudioEventAdapter() {
							@Override
							public void onTrackEnd(AudioPlayer Player, AudioTrack Track, AudioTrackEndReason EndReason) {
								f.delete();
								cdl.countDown();
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

				try {
					cdl.await();
				} catch (InterruptedException EX) {
					//いつこれ起きるん？
				}
			}
		});
	}

	public void close() {
		am.closeAudioConnection();
		playing_pool.getQueue().clear();
		playing_pool.shutdown();

		//残ったファイルを削除
		for (File f:messgae_queue) {
			f.delete();
		}
		messgae_queue.clear();
	}
}
