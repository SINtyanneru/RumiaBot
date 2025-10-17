package su.rumishistem.rumiabot.System;

import static su.rumishistem.rumi_java_lib.LOG_PRINT.Main.LOG;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.net.URL;
import java.net.URLClassLoader;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.jar.JarFile;
import java.util.stream.Collectors;

import su.rumishistem.rumi_java_lib.LOG_PRINT.LOG_TYPE;
import su.rumishistem.rumiabot.System.Type.FunctionClass;

public class FunctionLoader {
	private static List<FunctionClass> function_list = new ArrayList<>();

	public static void load() throws IOException, ClassNotFoundException, InstantiationException, IllegalAccessException, IllegalArgumentException, InvocationTargetException, NoSuchMethodException, SecurityException {
		List<Path> list = Files.list(Path.of(Paths.get("").toAbsolutePath().toString(), "FUNCTION"))
			.filter(PATH->PATH.toString().endsWith(".jar"))
			.collect(Collectors.toList());

		//ロード
		for (Path f:list) {
			LOG(LOG_TYPE.PROCESS, "Loading:" + f.getFileName().toString());
			JarFile jar = new JarFile(new File(f.toString()));
			URLClassLoader class_loader = new URLClassLoader(new URL[] {new File(f.toString()).toURI().toURL()}, FunctionLoader.class.getClassLoader());

			//Manifestがあるか？
			if (jar.getManifest() == null) {
				LOG(LOG_TYPE.PROCESS_END_FAILED, "");
				LOG(LOG_TYPE.FAILED, "MANIFESTが無い為、モジュールを読み込めませんでした");
			}

			//メインクラス名をゲットする
			String main_class = jar.getManifest().getMainAttributes().getValue("Main-Class");
			if (main_class == null) {
				LOG(LOG_TYPE.PROCESS_END_FAILED, "");
				LOG(LOG_TYPE.FAILED, "MainClassが設定されていない為、モジュールを読み込めませんでした");
			}

			//メインクラス取得
			Class<?> function_main_class = class_loader.loadClass(main_class);
			Object function_instance = function_main_class.getDeclaredConstructor().newInstance();

			//メインクラスの型をチェック
			if (!(function_instance instanceof FunctionClass)) {
				LOG(LOG_TYPE.PROCESS_END_FAILED, "");
				LOG(LOG_TYPE.FAILED, "メインクラスはFunctionClassではありませんでした、そのためモジュールを読み込めませんでした");
			}

			//追加
			FunctionClass function = (FunctionClass) function_instance;
			function_list.add(function);

			LOG(LOG_TYPE.PROCESS_END_OK, "");
			LOG(LOG_TYPE.OK, "読み込みました:" + function.function_name() + " V" + function.function_version() + " by " + function.function_author());

			//終了時処理
			Runtime.getRuntime().addShutdownHook(new Thread(new Runnable() {
				@Override
				public void run() {
					try {
						class_loader.close();
						jar.close();
					} catch (IOException ex) {
						//あ？
					}
				}
			}));
		}

		LOG(LOG_TYPE.OK, function_list.size() + "個の機能を読み込みました");

		//初期化
		for (FunctionClass function:function_list) {
			function.init();
			LOG(LOG_TYPE.OK, function.function_name() + "初期化Ok");
		}
	}

	public static FunctionClass[] get_list() {
		FunctionClass[] list = new FunctionClass[function_list.size()];
		for (int i = 0; i < function_list.size(); i++) {
			list[i] = function_list.get(i);
		}
		return list;
	}
}
