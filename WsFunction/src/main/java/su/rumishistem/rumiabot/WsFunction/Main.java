package su.rumishistem.rumiabot.WsFunction;

import static su.rumishistem.rumiabot.System.FunctionModuleLoader.AddCommand;

import java.io.File;
import org.openqa.selenium.*;
import org.openqa.selenium.firefox.FirefoxDriver;
import org.openqa.selenium.firefox.FirefoxOptions;
import org.openqa.selenium.firefox.FirefoxProfile;
import su.rumishistem.rumiabot.System.TYPE.CommandData;
import su.rumishistem.rumiabot.System.TYPE.CommandInteraction;
import su.rumishistem.rumiabot.System.TYPE.CommandOption;
import su.rumishistem.rumiabot.System.TYPE.CommandOptionType;
import su.rumishistem.rumiabot.System.TYPE.FunctionClass;
import su.rumishistem.rumiabot.System.TYPE.ReceiveMessageEvent;

public class Main implements FunctionClass {
	private static final String FUNCTION_NAME = "ウェブサイトスクショ";
	private static final String FUNCTION_VERSION = "1.0";
	private static final String FUNCTION_AUTOR = "Rumisan";

	public static boolean Enabled = false;

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
		AddCommand(new CommandData("ws", new CommandOption[] {
			new CommandOption("url", CommandOptionType.String, null, true)
		}, false));
	}

	@Override
	public void ReceiveMessage(ReceiveMessageEvent e) {}

	@Override
	public boolean GetAllowCommand(String Name) {
		return Name.equals("ws");
	}

	@Override
	public void RunCommand(CommandInteraction CI) throws Exception {
		String URL = CI.GetCommand().GetOption("url").GetValueAsString();

		//URLをチェック
		if(!(URL.startsWith("http://") || URL.startsWith("https://"))){
			URL = "https://" + URL;
		}

		FirefoxProfile FFP = new FirefoxProfile();
		FFP.setPreference("browser.cache.disk.enable", false);
		FFP.setPreference("browser.cache.memory.enable", false);
		FFP.setPreference("browser.cache.offline.enable", false);
		FFP.setPreference("network.http.use-cache", false);

		FirefoxOptions BROWSER_OPTION = new FirefoxOptions();
		BROWSER_OPTION.setProfile(FFP);
		BROWSER_OPTION.addArguments("--headless");

		WebDriver DRIVER = new FirefoxDriver(BROWSER_OPTION);
		DRIVER.manage().window().setSize(new Dimension(1980, 1080));

		DRIVER.get(URL);

		//サイズのオプションがしていされているなら実行
		if(CI.GetCommand().GetOption("size") != null){
			//サイズ指定あり
			if(CI.GetCommand().GetOption("size").GetValueAsString().equals("FULL")){
				JavascriptExecutor JSE = (JavascriptExecutor) DRIVER;

				//ページのサイズを取得
				int PAGE_WIDTH = ((Number) JSE.executeScript("return document.body.scrollWidth")).intValue();
				int PAGE_HEIGHT = ((Number) JSE.executeScript("return document.body.scrollHeight")).intValue();

				//取得したサイズを適応する
				DRIVER.manage().window().setSize(new Dimension(PAGE_WIDTH, PAGE_HEIGHT));
			}
		}

		File SCREENSHOT = ((TakesScreenshot)DRIVER).getScreenshotAs(OutputType.FILE);

		//返答
		CI.AddFile(SCREENSHOT);
		CI.Reply("スクショしたよ");

		//終了
		DRIVER.quit();
	}
}
