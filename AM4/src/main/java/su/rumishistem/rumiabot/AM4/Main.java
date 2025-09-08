package su.rumishistem.rumiabot.AM4;

import static su.rumishistem.rumiabot.System.Main.MisskeyBOT;

import java.io.File;
import java.io.IOException;
import java.time.LocalDateTime;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import su.rumishistem.rumi_java_lib.Misskey.Builder.NoteBuilder;
import su.rumishistem.rumiabot.System.TYPE.CommandInteraction;
import su.rumishistem.rumiabot.System.TYPE.FunctionClass;
import su.rumishistem.rumiabot.System.TYPE.ReceiveMessageEvent;

public class Main implements FunctionClass{
	private static final String FUNCTION_NAME = "おはよう！朝4時になにをしてるんだい？";
	private static final String FUNCTION_VERSION = "1.0";
	private static final String FUNCTION_AUTOR = "Rumisan";

	private int last_run_day = 0;

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
		ScheduledExecutorService scheduler = Executors.newSingleThreadScheduledExecutor();
		scheduler.scheduleAtFixedRate(new Runnable() {
			@Override
			public void run() {
				LocalDateTime now = LocalDateTime.now();

				if (now.getHour() == 4 && (now.getDayOfMonth() != last_run_day)) {
					last_run_day = now.getDayOfMonth();

					NoteBuilder nb = new NoteBuilder();
					nb.setTEXT("#おはよう！朝4時に何してるんだい？");
					nb.AddFile(new File("./ohayou.png"));

					try {
						MisskeyBOT.PostNote(nb.Build());
					} catch (IOException EX) {
						EX.printStackTrace();
					}
				}
			}
		}, 0, 1, TimeUnit.SECONDS);
	}

	@Override
	public void ReceiveMessage(ReceiveMessageEvent e) {}

	@Override
	public boolean GetAllowCommand(String Name) { return false; }

	@Override
	public void RunCommand(CommandInteraction CI) throws Exception {}
}
