package su.rumishistem.rumiabot.AM4;

import java.io.File;
import java.io.IOException;
import java.time.LocalDateTime;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import su.rumishistem.rumi_java_lib.MisskeyBot.Builder.NoteBuilder;
import su.rumishistem.rumiabot.System.Type.FunctionClass;

public class Main implements FunctionClass{
	private int last_run_day = 0;

	@Override
	public String function_name() {
		return "おはよう！朝4時になにをしてるんだい？";
	}
	@Override
	public String function_version() {
		return "1.0";
	}
	@Override
	public String function_author() {
		return "るみ";
	}

	@Override
	public void init() {
		ScheduledExecutorService scheduler = Executors.newSingleThreadScheduledExecutor();
		scheduler.scheduleAtFixedRate(new Runnable() {
			@Override
			public void run() {
				LocalDateTime now = LocalDateTime.now();

				if (now.getHour() == 4 && (now.getDayOfMonth() != last_run_day)) {
					last_run_day = now.getDayOfMonth();

					NoteBuilder nb = new NoteBuilder();
					nb.set_text("#おはよう！朝4時に何してるんだい？");
					nb.add_file(new File("./ohayou.png"));

					su.rumishistem.rumiabot.System.Main.get_misskey_bot().get_client().create_note(nb);
				}
			}
		}, 0, 1, TimeUnit.SECONDS);
	}
}
