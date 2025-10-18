package su.rumishistem.rumiabot.WsFunction;

import java.io.File;
import org.openqa.selenium.*;
import org.openqa.selenium.firefox.FirefoxDriver;
import org.openqa.selenium.firefox.FirefoxOptions;
import org.openqa.selenium.firefox.FirefoxProfile;

import su.rumishistem.rumiabot.System.CommandRegister;
import su.rumishistem.rumiabot.System.Type.CommandInteraction;
import su.rumishistem.rumiabot.System.Type.CommandOptionRegist;
import su.rumishistem.rumiabot.System.Type.FunctionClass;
import su.rumishistem.rumiabot.System.Type.OptionType;
import su.rumishistem.rumiabot.System.Type.RunCommand;

public class Main implements FunctionClass {
	public static boolean Enabled = false;

	@Override
	public String function_name() {
		return "ウェブサイトスクショ";
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
		CommandRegister.add_command("ws", new CommandOptionRegist[] {
			new CommandOptionRegist("url", OptionType.String, true)
		}, false, new RunCommand() {
			@Override
			public void run(CommandInteraction e) {
				String url = e.get_option_as_string("url");

				//URLをチェック
				if(!(url.startsWith("http://") || url.startsWith("https://"))){
					url = "https://" + url;
				}

				FirefoxProfile FFP = new FirefoxProfile();
				FFP.setPreference("browser.cache.disk.enable", false);
				FFP.setPreference("browser.cache.memory.enable", false);
				FFP.setPreference("browser.cache.offline.enable", false);
				FFP.setPreference("network.http.use-cache", false);
				//ダウンロード無効化
				FFP.setPreference("browser.download.dir", "/tmp/");
				//いたずら対策
				FFP.setPreference("dom.push.enabled", false);
				FFP.setPreference("dom.ipc.processCount", 1);
				FFP.setPreference("dom.storage.enabled", false);
				FFP.setPreference("dom.disable_open_during_load", true);
				FFP.setPreference("dom.serviceWorkers.enabled", false);
				FFP.setPreference("javascript.enabled", false);

				FirefoxOptions BROWSER_OPTION = new FirefoxOptions();
				BROWSER_OPTION.setProfile(FFP);
				BROWSER_OPTION.addArguments("--headless");

				WebDriver DRIVER = new FirefoxDriver(BROWSER_OPTION);
				DRIVER.manage().window().setSize(new Dimension(1980, 1080));

				DRIVER.get(url);

				//サイズのオプションがしていされているなら実行
				/*if(CI.GetCommand().GetOption("size") != null){
					//サイズ指定あり
					if(CI.GetCommand().GetOption("size").GetValueAsString().equals("FULL")){
						JavascriptExecutor JSE = (JavascriptExecutor) DRIVER;

						//ページのサイズを取得
						int PAGE_WIDTH = ((Number) JSE.executeScript("return document.body.scrollWidth")).intValue();
						int PAGE_HEIGHT = ((Number) JSE.executeScript("return document.body.scrollHeight")).intValue();

						//取得したサイズを適応する
						DRIVER.manage().window().setSize(new Dimension(PAGE_WIDTH, PAGE_HEIGHT));
					}
				}*/

				File SCREENSHOT = ((TakesScreenshot)DRIVER).getScreenshotAs(OutputType.FILE);

				//返答
				e.add_file(SCREENSHOT);
				e.reply("スクショしたよ");

				//終了
				DRIVER.quit();
			}
		});
	}
}
