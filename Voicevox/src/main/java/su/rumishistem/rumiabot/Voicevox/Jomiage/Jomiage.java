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
import su.rumishistem.rumiabot.System.Discord.MODULE.NameParse;
import su.rumishistem.rumiabot.System.TYPE.CommandInteraction;
import su.rumishistem.rumiabot.System.TYPE.ReceiveMessageEvent;
import su.rumishistem.rumiabot.Voicevox.VOICEVOX;

public class Jomiage {
	private static final HashMap<String, String> ConvertDict = new HashMap<String, String>(){
		{
			put("\\@everyone", "全体メンション、");
			put("\\@here", "全体メンション、");
			put("\\b([a-zA-Z][a-zA-Z0-9+\\-.]*):\\/\\/[^\\s\"'<>]+", "ユーアールエル");
			put("🛬🏙️", "アメリカ同時多発テロ事件");
		}
	};

	private static HashMap<String, JomiageData> JomiageDataTable = new HashMap<String, JomiageData>();

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

		JomiageDataTable.put(ID, new JomiageData(
			AM,
			APM,
			AP,
			CI.GetDiscordInteraction().getChannelId()
		));

		CI.Reply("おけ");
	}

	private static String TextChannelIDToJomiageID(String ChID) {
		for (String JomiageID:JomiageDataTable.keySet()) {
			JomiageData J = JomiageDataTable.get(JomiageID);
			if (J.getKikisen().getId().equals(ChID)) {
				return JomiageID;
			}
		}

		return null;
	}

	public static void ReceiveMessage(ReceiveMessageEvent e) {
		String JomiageID = TextChannelIDToJomiageID(e.GetDiscordChannel().getId());
		JomiageData J = JomiageDataTable.get(JomiageID);

		if (JomiageID == null || J == null) {
			return;
		}

		//BOTではないことを確認する
		if (e.GetDiscordMember().getUser().isBot()) {
			return;
		}

		//取得
		AudioPlayerManager APM = J.getAPM();
		AudioPlayer AP = J.getAP();
		int VoiceSpeakers = 0;
		String Text = e.GetMessage().GetText();

		//話者を選ぶ
		if (J.UserVoiceIsNone(e.GetDiscordMember().getUser().getId())) {
			HashMap<String, Object> SelectedSpeakers = J.getUserVoice(e.GetDiscordMember().getUser().getId());
			VoiceSpeakers = (int)SelectedSpeakers.get("SPEAKERS");

			Text = "「" + new NameParse(e.GetDiscordMember()).getDisplayName() + "」が会話に参加しました。" + Text;
			e.GetMessage().Reply("貴様の声：" + (String)SelectedSpeakers.get("NAME"));
		} else {
			VoiceSpeakers = (int)J.getUserVoice(e.GetDiscordMember().getUser().getId()).get("SPEAKERS");
		}

		//NullPointerException
		if (APM == null || AP == null) {
			return;
		}

		//置換
		for (String K:ConvertDict.keySet()) {
			String To = ConvertDict.get(K);
			Text = Text.replaceAll(K, To);
		}

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

	public static void DisconnectVC(String ID) {
		String JomiageID = TextChannelIDToJomiageID(ID);
		if (JomiageID != null) {
			JomiageDataTable.get(JomiageID).getAM().closeAudioConnection();
			JomiageDataTable.remove(JomiageID);
		}
	}

	public static void VCMemberUpdate(String Ch) {
		for (JomiageData J:JomiageDataTable.values()) {
			AudioManager AM = J.getAM();
			if (AM.getConnectedChannel() != null && AM.getConnectedChannel().getId().equals(Ch)) {
				if (AM.getConnectedChannel().getMembers().size() == 1) {
					DisconnectVC(Ch);
					return;
				}
			}
		}
	}
}
