package su.rumishistem.rumiabot.System;

import static su.rumishistem.rumiabot.System.Main.CommandList;
import static su.rumishistem.rumiabot.System.Main.DiscordContextmenuList;
import static su.rumishistem.rumiabot.System.Main.FunctionModuleList;
import static su.rumishistem.rumi_java_lib.LOG_PRINT.Main.LOG;
import java.io.File;
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.net.URL;
import java.net.URLClassLoader;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.jar.JarFile;
import java.util.stream.Collectors;
import su.rumishistem.rumi_java_lib.LOG_PRINT.LOG_TYPE;
import su.rumishistem.rumiabot.System.TYPE.CommandData;
import su.rumishistem.rumiabot.System.TYPE.FunctionClass;

public class FunctionModuleLoader {
	public void Load() throws IOException, ClassNotFoundException, NoSuchFieldException, SecurityException, IllegalArgumentException, IllegalAccessException, InstantiationException, InvocationTargetException, NoSuchMethodException {
		List<Path> JAR_LIST = Files.list(Path.of(Paths.get("").toAbsolutePath().toString(), "FUNCTION"))
								.filter(PATH->PATH.toString().endsWith(".jar"))
								.collect(Collectors.toList());

		for (Path JAR:JAR_LIST) {
			LOG(LOG_TYPE.PROCESS, "Loading:" + JAR.getFileName().toString());
			JarFile JARFILE = new JarFile(new File(JAR.toString()));
			URL JARURL = new File(JAR.toString()).toURI().toURL();
			URLClassLoader CL = new URLClassLoader(new URL[] {JARURL}, FunctionModuleLoader.class.getClassLoader());
			if (JARFILE.getManifest() != null) {
				String MainClass = JARFILE.getManifest().getMainAttributes().getValue("Main-Class");
				if (MainClass != null) {
					Class<?> FunctionMainClass = CL.loadClass(MainClass);
					Object FunctionMainInstance = FunctionMainClass.getDeclaredConstructor().newInstance();

					//型チェック(合致してないと死ぬ)
					if (FunctionMainInstance instanceof FunctionClass) {
						FunctionClass Function = (FunctionClass) FunctionMainInstance;
						FunctionModuleList.add(Function);

						LOG(LOG_TYPE.PROCESS_END_OK, "");
						LOG(LOG_TYPE.OK, "読み込みました:" + Function.FUNCTION_NAME() + " V" + Function.FUNCTION_VERSION() + " by " + Function.FUNCTION_AUTOR());
					} else {
						LOG(LOG_TYPE.PROCESS_END_FAILED, "");
						LOG(LOG_TYPE.FAILED, "メインクラスはFunctionClassではありませんでした、そのためモジュールを読み込めませんでした");
					}
				} else {
					LOG(LOG_TYPE.PROCESS_END_FAILED, "");
					LOG(LOG_TYPE.FAILED, "MainClassが設定されていない為、モジュールを読み込めませんでした");
				}
			} else {
				LOG(LOG_TYPE.PROCESS_END_FAILED, "");
				LOG(LOG_TYPE.FAILED, "MANIFESTが無い為、モジュールを読み込めませんでした");
			}
			JARFILE.close();
		}

		LOG(LOG_TYPE.OK, FunctionModuleList.size() + "個の機能を読み込みました");
		LOG(LOG_TYPE.PROCESS, "機能を初期化しています");
		for (FunctionClass Function:FunctionModuleList) {
			Function.Init();
			LOG(LOG_TYPE.OK, Function.FUNCTION_NAME() + "初期化Ok");
		}
		LOG(LOG_TYPE.OK, "機能を初期化しました");
	}

	public static void AddCommand(CommandData Command) {
		CommandList.add(Command);
	}

	public static void AddDiscordContextMenu(net.dv8tion.jda.api.interactions.commands.build.CommandData Command) {
		DiscordContextmenuList.add(Command);
	}
}
