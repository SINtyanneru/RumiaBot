package su.rumishistem.rumiabot.MusicPlayer;

import static su.rumishistem.rumiabot.System.FunctionModuleLoader.AddCommand;

import java.io.*;
import java.util.HashMap;
import java.util.UUID;
import java.util.regex.Pattern;

import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

import net.dv8tion.jda.api.entities.*;
import net.dv8tion.jda.api.entities.channel.middleman.AudioChannel;
import net.dv8tion.jda.api.events.guild.voice.GuildVoiceUpdateEvent;
import net.dv8tion.jda.api.interactions.commands.SlashCommandInteraction;
import su.rumishistem.rumiabot.System.TYPE.*;
import su.rumishistem.rumiabot.System.TYPE.DiscordEvent.EventType;

public class Main implements FunctionClass {
	private static final String FUNCTION_NAME = "音楽プレイヤー";
	private static final String FUNCTION_VERSION = "1.0";
	private static final String FUNCTION_AUTOR = "Rumisan";

	private static HashMap<String, String> vc_id_table = new HashMap<>();
	private static HashMap<String, Player> player_list = new HashMap<>();

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

		AddCommand(new CommandData("stop", new CommandOption[] {}, false));
		AddCommand(new CommandData("skip", new CommandOption[] {}, false));
	}

	@Override
	public void ReceiveMessage(ReceiveMessageEvent e) {}

	@Override
	public boolean GetAllowCommand(String Name) {
		return (Name.equals("play") || Name.equals("stop") || Name.equals("skip"));
	}

	@Override
	public void RunCommand(CommandInteraction CI) throws Exception {
		if (CI.GetSource() != SourceType.Discord) {
			CI.Reply("Discordでのみ使用可能なコマンドです");
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

		//オーディオチャンネルを取得
		AudioChannel vc = voice_State.getChannel();
		if (vc == null) throw new RuntimeException("エラー");

		Player player;

		if (vc_id_table.get(vc.getId()) == null) {
			//プレイヤー作成
			player = new Player(guild, vc);
			player_list.put(player.get_id(), player);
			vc_id_table.put(vc.getId(), player.get_id());

			CI.Reply("プレイヤー["+player.get_id()+"]を作成しました！");
		} else {
			player = player_list.get(vc_id_table.get(vc.getId()));
			CI.Reply("プレイヤー["+player.get_id()+"]に命令...");
		}

		switch (CI.GetCommand().GetName()) {
			case "play":
				play(CI.GetDiscordInteraction(), player);
				return;
			case "stop":
				stop(CI.GetDiscordInteraction(), player, vc);
				return;
		}
	}

	@Override
	public void DiscordEventReceive(DiscordEvent e) throws Exception {
		if (e.GetType() == EventType.VCMemberUpdate) {
			GuildVoiceUpdateEvent event = (GuildVoiceUpdateEvent)e.GetEventClass();

			String Ch = null;
			if (event.getChannelJoined() != null) {
				Ch = event.getChannelJoined().getId();
			} else if (event.getChannelLeft() != null) {
				Ch = event.getChannelLeft().getId();
			}

			//繋いでいるVCで誰も居なくなったら切断
			if (vc_id_table.get(Ch) == null) return;
			Player player = player_list.get(vc_id_table.get(Ch));
			if (player.get_person_count() == 0) {
				player.stop();
			}
		}
	}

	private static void play(SlashCommandInteraction e, Player player) throws IOException, InterruptedException {
		Message progress_msg = e.getChannel().asTextChannel().sendMessage("準備中...").complete();

		String url = e.getOption("url").getAsString();
		if (Pattern.compile("^(https?|ftp)://[\\w.-]+(?:\\.[\\w\\.-]+)+[/#?]?.*$").matcher(url).matches() == false) {
			e.getHook().editOriginal("URLが不正です").queue();
			return;
		}

		//一時ファイルに書く
		File ytdlp_temp_file = new File("/tmp/" + UUID.randomUUID().toString());
		ytdlp_temp_file.createNewFile();

		//yt-dlp経由で落とす
		ProcessBuilder ytdlp_pb = new ProcessBuilder(
			"yt-dlp",
			"-f", "bestaudio",
			"--no-part",
			"-o", ytdlp_temp_file.toString(),
			"--newline",
			//"--print", "{\"title\": %(title)j}",
			"--progress-template", "{\"downloaded\": %(progress.downloaded_bytes)s, \"total\": %(progress.total_bytes_estimate)s, \"speed\": %(progress.speed)s, \"eta\": %(progress.eta)s}",
			url
		);
		ytdlp_pb.redirectErrorStream(true);
		Process ytdlp = ytdlp_pb.start();

		//ログ
		BufferedReader ytdlp_br = new BufferedReader(new InputStreamReader(ytdlp.getInputStream()));
		long last_time = 0;
		String ytdlp_line;
		while ((ytdlp_line = ytdlp_br.readLine()) != null) {
			//yt-dlpがNAとかいうふざけたことを抜かすので置換する
			ytdlp_line = ytdlp_line.replace(": NA", ": null");

			try {
				JsonNode body = new ObjectMapper().readTree(ytdlp_line);
				if (body.get("eta").isNull() || body.get("downloaded").isNull()) {
					continue;
				}

				//1秒ごとに出したい
				if (System.currentTimeMillis() - last_time >= 1000) {
					progress_msg.editMessage("残り秒数：" + body.get("eta").asInt() + "秒\n取得済み：" + body.get("downloaded").asInt() + "バイト").queue();
					last_time = System.currentTimeMillis();
				}
			} catch (JsonParseException EX) {
				//無視
			}
		}
		progress_msg.editMessage("取得完了。").queue();

		//mp3化するファイル
		File ffmpeg_temp_file = new File("/tmp/" + UUID.randomUUID().toString());

		//ffmpegでmp3に変換する
		progress_msg = e.getChannel().asTextChannel().sendMessage("変換中").complete();
		ProcessBuilder ffmpeg_pb = new ProcessBuilder("ffmpeg", "-i", ytdlp_temp_file.toString(), "-f", "mp3", "-acodec", "libmp3lame", "-ar", "44100", "-ac", "2", "-progress", "pipe:1", "-nostats", ffmpeg_temp_file.toString());
		Process ffmpeg = ffmpeg_pb.start();
		ffmpeg.waitFor();
		ytdlp_temp_file.delete();

		progress_msg.editMessage("変換完了、再生します").queue();
		player.play(ffmpeg_temp_file);
	}

	private static void stop(SlashCommandInteraction e, Player player, AudioChannel vc) throws IOException, InterruptedException {
		player.stop();
		player_list.remove(player.get_id());
		vc_id_table.remove(vc.getId());
	}
}
