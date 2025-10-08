package su.rumishistem.rumiabot.MusicPlayer;

import java.nio.ByteBuffer;

import com.sedmelluq.discord.lavaplayer.player.AudioPlayer;
import com.sedmelluq.discord.lavaplayer.track.playback.AudioFrame;

import net.dv8tion.jda.api.audio.AudioSendHandler;

public class LavaPlayerSendHandler implements AudioSendHandler {
	private final AudioPlayer AP;
	private AudioFrame LastFlame;

	public LavaPlayerSendHandler(AudioPlayer Player) {
		this.AP = Player;
	}

	@Override
	public boolean canProvide() {
		LastFlame = AP.provide();
		return LastFlame != null;
	}

	@Override
	public ByteBuffer provide20MsAudio() {
		return ByteBuffer.wrap(LastFlame.getData());
	}

	@Override
	public boolean isOpus() {
		return true;
	}
}
