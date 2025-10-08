package su.rumishistem.rumiabot.MusicPlayer;

import java.io.File;
import java.util.UUID;

import com.sedmelluq.discord.lavaplayer.player.AudioLoadResultHandler;
import com.sedmelluq.discord.lavaplayer.player.AudioPlayer;
import com.sedmelluq.discord.lavaplayer.player.AudioPlayerManager;
import com.sedmelluq.discord.lavaplayer.player.DefaultAudioPlayerManager;
import com.sedmelluq.discord.lavaplayer.player.event.AudioEventAdapter;
import com.sedmelluq.discord.lavaplayer.source.AudioSourceManagers;
import com.sedmelluq.discord.lavaplayer.tools.FriendlyException;
import com.sedmelluq.discord.lavaplayer.track.AudioPlaylist;
import com.sedmelluq.discord.lavaplayer.track.AudioTrack;
import com.sedmelluq.discord.lavaplayer.track.AudioTrackEndReason;

import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.channel.middleman.AudioChannel;
import net.dv8tion.jda.api.managers.AudioManager;

public class Player {
	private String id = UUID.randomUUID().toString();
	private AudioManager am;
	private AudioPlayerManager apm;
	private AudioPlayer player;

	public Player(Guild guild, AudioChannel vc) {
		//LavaPlayer初期化
		am = guild.getAudioManager();
		apm = new DefaultAudioPlayerManager();
		AudioSourceManagers.registerRemoteSources(apm);
		AudioSourceManagers.registerLocalSource(apm);
		player = apm.createPlayer();
		set_volume(50);

		//VCに参加
		am.setSendingHandler(new LavaPlayerSendHandler(player));
		am.openAudioConnection(vc);
	}

	public String get_id() {
		return id;
	}

	public int get_person_count() {
		return am.getConnectedChannel().getMembers().size();
	}

	public void set_volume(int vol) {
		player.setVolume(vol);
	}

	public void stop() {
		player.stopTrack();
		player.destroy();
		am.closeAudioConnection();
	}

	public void pause() {
		player.setPaused(!player.isPaused());
	}

	public void play(File f) {
		apm.loadItem(f.toString(), new AudioLoadResultHandler() {
			@Override
			public void trackLoaded(AudioTrack track) {
				player.playTrack(track);

				player.addListener(new AudioEventAdapter() {
					@Override
					public void onTrackEnd(AudioPlayer Player, AudioTrack Track, AudioTrackEndReason EndReason) {
						f.delete();
					}
				});
			}
			@Override
			public void playlistLoaded(AudioPlaylist playlist) {}
			@Override
			public void noMatches() {}
			@Override
			public void loadFailed(FriendlyException exception) {}
		});
	}
}
