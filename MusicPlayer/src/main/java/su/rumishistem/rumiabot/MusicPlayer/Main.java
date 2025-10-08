package su.rumishistem.rumiabot.MusicPlayer;

import static su.rumishistem.rumiabot.System.FunctionModuleLoader.AddCommand;

import java.io.*;
import java.util.HashMap;
import java.util.UUID;
import java.util.regex.Pattern;
import net.dv8tion.jda.api.entities.*;
import net.dv8tion.jda.api.entities.channel.middleman.AudioChannel;
import net.dv8tion.jda.api.interactions.commands.SlashCommandInteraction;
import su.rumishistem.rumiabot.System.TYPE.*;

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
		return Name.equals("play");
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

	private static void send_message(SlashCommandInteraction e, String text) {
		e.getChannel().asTextChannel().sendMessage(text).queue();
	}

	private static void play(SlashCommandInteraction e, Player player) throws IOException, InterruptedException {
		send_message(e, "音声を取得しています、暫くお待ち下さい。");

		String url = e.getOption("url").getAsString();
		if (Pattern.compile("^(https?|ftp)://[\\w.-]+(?:\\.[\\w\\.-]+)+[/#?]?.*$").matcher(url).matches() == false) {
			e.getHook().editOriginal("URLが不正です").queue();
			return;
		}

		//yt-dlpから音声を落として、ffmpegにmp3化させる
		ProcessBuilder pb = new ProcessBuilder(new String[] {
			"sh",
			"-c",
			"yt-dlp -f bestaudio --no-part -o - '"+url.replace("'", "")+"' | ffmpeg -i - -f mp3 -acodec libmp3lame -ar 44100 -ac 2 -"
		});
		Process p = pb.start();

		//一時ファイルに書く
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

		//成功？
		int status = p.waitFor();
		if (status != 0) {
			temp_file.delete();
			send_message(e, "エラー！再生失敗");
			return;
		}

		send_message(e, "終了、再生します。");

		player.play(temp_file);
	}

	private static void stop(SlashCommandInteraction e, Player player, AudioChannel vc) throws IOException, InterruptedException {
		player.stop();
		player_list.remove(player.get_id());
		vc_id_table.remove(vc.getId());
	}
}
