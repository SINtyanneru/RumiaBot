package su.rumishistem.rumiabot.Voicevox.Jomiage;

import java.util.*;
import com.sedmelluq.discord.lavaplayer.player.*;
import com.sedmelluq.discord.lavaplayer.source.AudioSourceManagers;

import net.dv8tion.jda.api.entities.*;
import net.dv8tion.jda.api.entities.channel.middleman.AudioChannel;
import net.dv8tion.jda.api.managers.AudioManager;
import su.rumishistem.rumiabot.System.Module.NameParse;
import su.rumishistem.rumiabot.System.Type.CommandInteraction;
import su.rumishistem.rumiabot.System.Type.ReceiveMessageEvent;
import su.rumishistem.rumiabot.System.Type.SourceType;

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

	public static void RunCommand(CommandInteraction e) throws Exception {
		Member M = e.get_discprd_event().getMember();
		if (M == null) {
			e.reply("エラー");
			return;
		}

		GuildVoiceState VoiceState = M.getVoiceState();
		if (VoiceState == null || !VoiceState.inAudioChannel()) {
			e.reply("貴様がどこのVCに参加しているのか分かりませんでした。");
			return;
		}

		AudioChannel VC = VoiceState.getChannel();
		if (VC == null) {
			e.reply("VCを取得できませんでした。");
			return;
		}

		Guild G = e.get_discprd_event().getGuild();
		if (G == null) {
			e.reply("サーバーを取得できませんでした。");
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
			e.get_discprd_event().getGuild(),
			AM,
			APM,
			AP,
			e.get_discprd_event().getChannelId()
		));

		e.reply("おけ");
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
		if (e.get_source() != SourceType.Discord) return;
		String JomiageID = TextChannelIDToJomiageID(e.get_discord().getChannel().getId());
		JomiageData J = JomiageDataTable.get(JomiageID);

		if (JomiageID == null || J == null) {
			return;
		}

		//BOTではないことを確認する
		if (e.get_discord().getAuthor().isBot()) {
			return;
		}

		//取得
		AudioPlayerManager APM = J.get_apm();
		AudioPlayer AP = J.getAP();
		int VoiceSpeakers = 0;
		String Text = e.get_discord().getMessage().getContentRaw();

		//話者を選ぶ
		if (J.UserVoiceIsNone(e.get_discord().getMember().getUser().getId())) {
			HashMap<String, Object> SelectedSpeakers = J.getUserVoice(e.get_discord().getMember().getUser().getId());
			VoiceSpeakers = (int)SelectedSpeakers.get("SPEAKERS");

			Text = "「" + new NameParse(e.get_discord().getMember()).getDisplayName() + "」が会話に参加しました。" + Text;
			e.get_discord().getMessage().reply("貴様の声：" + (String)SelectedSpeakers.get("NAME"));
		} else {
			VoiceSpeakers = (int)J.getUserVoice(e.get_discord().getMember().getUser().getId()).get("SPEAKERS");
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

		//追加
		J.addMessage(e.get_discord().getMember().getId(), Text);
	}

	public static void DisconnectVC(String ID) {
		String JomiageID = TextChannelIDToJomiageID(ID);
		if (JomiageID != null) {
			JomiageDataTable.get(JomiageID).close();
			JomiageDataTable.remove(JomiageID);
		}
	}

	public static void VCMemberUpdate(String Ch) {
		for (JomiageData J:JomiageDataTable.values()) {
			AudioManager am = J.get_am();
			if (am.getConnectedChannel() != null && am.getConnectedChannel().getId().equals(Ch)) {
				if (am.getConnectedChannel().getMembers().size() == 1) {
					DisconnectVC(Ch);
					return;
				}
			}
		}
	}
}
