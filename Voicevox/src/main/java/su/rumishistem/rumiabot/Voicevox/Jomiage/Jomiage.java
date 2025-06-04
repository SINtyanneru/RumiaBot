package su.rumishistem.rumiabot.Voicevox.Jomiage;

import java.io.File;
import java.util.HashMap;
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
import net.dv8tion.jda.api.entities.GuildVoiceState;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.channel.middleman.AudioChannel;
import net.dv8tion.jda.api.managers.AudioManager;
import su.rumishistem.rumiabot.System.TYPE.CommandInteraction;
import su.rumishistem.rumiabot.System.TYPE.MessageData;
import su.rumishistem.rumiabot.Voicevox.VOICEVOX;

public class Jomiage {
	private static final HashMap<String, String> ConvertDict = new HashMap<String, String>(){
		{
			put("\\@everyone", "全体メンション、");
			put("\\@here", "全体メンション、");
		}
	};

	private static HashMap<String, AudioManager> AMTable = new HashMap<String, AudioManager>();								//ID→AM
	public static HashMap<String, String> TextChannelTable = new HashMap<String, String>();										//チャンネルID→AMID
	public static HashMap<String, AudioPlayerManager> AudioPlayerManagerTable = new HashMap<String, AudioPlayerManager>();	//AMID→AudioPlayerManager
	public static HashMap<String, AudioPlayer> AudioPlayerTable = new HashMap<String, AudioPlayer>();							//AMID→AudioPlayer

	public static void RunCommand(CommandInteraction CI) throws Exception {
		Member M = CI.GetDiscordInteraction().getMember();
		if (M == null) {
			CI.Reply("エラー");
			return;
		}

		GuildVoiceState VoiceState = M.getVoiceState();
		if (VoiceState == null || !VoiceState.inAudioChannel()) {
			CI.Reply("貴様がどこのVCに参加しているのか分かりませんでした。");
			return;
		}

		AudioChannel VC = VoiceState.getChannel();
		if (VC == null) {
			CI.Reply("VCを取得できませんでした。");
			return;
		}

		Guild G = CI.GetDiscordInteraction().getGuild();
		if (G == null) {
			CI.Reply("サーバーを取得できませんでした。");
			return;
		}

		String ID = UUID.randomUUID().toString();
		AudioManager AM = G.getAudioManager();

		//LavaPlayer初期化
		AudioPlayerManager APM = new DefaultAudioPlayerManager();
		AudioSourceManagers.registerRemoteSources(APM);
		AudioSourceManagers.registerLocalSource(APM);
		AudioPlayer AP = APM.createPlayer();

		//VCに参加
		AM.setSendingHandler(new LavaPlayerSendHandler(AP));
		AM.openAudioConnection(VC);

		AMTable.put(ID, AM);
		TextChannelTable.put(CI.GetDiscordInteraction().getChannelId(), ID);
		AudioPlayerManagerTable.put(ID, APM);
		AudioPlayerTable.put(ID, AP);

		CI.Reply("おけ");
	}

	public static void ReceiveMessage(MessageData M) {
		if (TextChannelTable.get(M.GetDiscordChannel().getId()) == null) {
			return;
		}

		String AMID = TextChannelTable.get(M.GetDiscordChannel().getId());
		AudioPlayerManager APM = AudioPlayerManagerTable.get(AMID);
		AudioPlayer AP = AudioPlayerTable.get(AMID);

		//NullPointerException
		if (APM == null || AP == null) {
			return;
		}

		String Text = M .GetText();

		for (String K:ConvertDict.keySet()) {
			String To = ConvertDict.get(K);
			Text = Text.replaceAll(K, To);
		}

		//ファイルを錬成して再生
		File F = VOICEVOX.genAudioFile(0, VOICEVOX.genAudioQuery(0, Text));
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
	}
}
