package su.rumishistem.rumiabot.MusicPlayer;

import static su.rumishistem.rumiabot.System.FunctionModuleLoader.AddCommand;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.UUID;
import java.util.regex.Pattern;

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
import su.rumishistem.rumiabot.System.TYPE.CommandData;
import su.rumishistem.rumiabot.System.TYPE.CommandInteraction;
import su.rumishistem.rumiabot.System.TYPE.CommandOption;
import su.rumishistem.rumiabot.System.TYPE.CommandOptionType;
import su.rumishistem.rumiabot.System.TYPE.FunctionClass;
import su.rumishistem.rumiabot.System.TYPE.ReceiveMessageEvent;

public class Main implements FunctionClass {
	private static final String FUNCTION_NAME = "音楽プレイヤー";
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
		AddCommand(new CommandData("play", new CommandOption[] {
			new CommandOption("url", CommandOptionType.String, null, true)
		}, false));
	}

	@Override
	public void ReceiveMessage(ReceiveMessageEvent e) {}

	@Override
	public boolean GetAllowCommand(String Name) {
		return Name.equals("play");
	}

	@Override
	public void RunCommand(CommandInteraction CI) throws Exception {
		String url = CI.GetCommand().GetOption("url").GetValueAsString();
		if (Pattern.compile("^(https?|ftp)://[\\w.-]+(?:\\.[\\w\\.-]+)+[/#?]?.*$").matcher(url).matches() == false) {
			CI.Reply("URLが不正");
			return;
		}

		//メンバー取得
		Member member = CI.GetDiscordInteraction().getMember();
		if (member == null) throw new RuntimeException("メンバーが無い");

		//サーバーを取得
		Guild guild = member.getGuild();
		if (guild == null) throw new RuntimeException("サーバーの取得に失敗");

		//VC取得
		GuildVoiceState voice_State = member.getVoiceState();
		if (voice_State == null) throw new RuntimeException("ボイスチャンネル取得エラー");

		AudioChannel vc = voice_State.getChannel();
		if (vc == null) throw new RuntimeException("エラー");

		AudioManager am = guild.getAudioManager();

		//LavaPlayer初期化
		AudioPlayerManager apm = new DefaultAudioPlayerManager();
		AudioSourceManagers.registerRemoteSources(apm);
		AudioSourceManagers.registerLocalSource(apm);
		AudioPlayer ap = apm.createPlayer();

		//VCに参加
		am.setSendingHandler(new LavaPlayerSendHandler(ap));
		am.openAudioConnection(vc);

		new Thread(new Runnable() {
			@Override
			public void run() {
				try {
					ProcessBuilder pb = new ProcessBuilder(new String[] {
						"sh",
						"-c",
						"yt-dlp -f bestaudio --no-part -o - '"+url.replace("'", "")+"' | ffmpeg -i - -f mp3 -acodec libmp3lame -ar 44100 -ac 2 -"
					});
					Process p = pb.start();


					File temp_file = new File("/tmp/" + UUID.randomUUID().toString());
					temp_file.createNewFile();
					FileOutputStream fos = new FileOutputStream(temp_file);

					//標準出力を読む
					InputStream is = p.getInputStream();
					byte[] buffer = new byte[8024];
					int read_length = 0;
					while ((read_length = is.read(buffer)) != -1) {
						fos.write(buffer, 0, read_length);
						fos.flush();
					}
					fos.close();

					int status = p.waitFor();
					if (status != 0) {
						temp_file.delete();
						CI.Reply("Error:" + status);
						return;
					}

					apm.loadItem(temp_file.toString(), new AudioLoadResultHandler() {
						@Override
						public void trackLoaded(AudioTrack track) {
							ap.setVolume(50);
							ap.playTrack(track);

							ap.addListener(new AudioEventAdapter() {
								@Override
								public void onTrackEnd(AudioPlayer Player, AudioTrack Track, AudioTrackEndReason EndReason) {
									temp_file.delete();
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
				} catch (IOException EX) {
					EX.printStackTrace();

					try {
						CI.Reply("エラー");
					} catch (Exception e) {}
				} catch (InterruptedException ex) {
					ex.printStackTrace();
				}
			}
		}).start();
	}
}
