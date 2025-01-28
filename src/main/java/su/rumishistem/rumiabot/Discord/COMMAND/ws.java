package su.rumishistem.rumiabot.Discord.COMMAND;

import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.utils.FileUpload;

import static su.rumishistem.rumiabot.Main.CONFIG_DATA;

import java.io.File;
import org.openqa.selenium.*;
import org.openqa.selenium.firefox.FirefoxDriver;
import org.openqa.selenium.firefox.FirefoxOptions;
import org.openqa.selenium.firefox.FirefoxProfile;

public class ws {
	public static void Main(SlashCommandInteractionEvent INTERACTION) {
		try{
			String URL = INTERACTION.getOption("url").getAsString();

			//URLをチェック
			if(!(URL.startsWith("http://") || URL.startsWith("https://"))){
				URL = "https://" + URL;
			}

			FirefoxProfile FFP = new FirefoxProfile();
			//キャッシュ無効化
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
			//JS無効化
			if (INTERACTION.getOption("js") == null) {
				FFP.setPreference("javascript.enabled", false);
			} else {
				if (!(
						CONFIG_DATA.get("ADMIN").asString("ROOT_DISCORD_ID").contains(INTERACTION.getMember().getUser().getId().toString())
						||
						CONFIG_DATA.get("ADMIN").asString("ADMIN_DISCORD_ID").contains(INTERACTION.getMember().getUser().getId().toString())
					)) {
					INTERACTION.getHook().editOriginal("お前はJavaScriptの使用が許可されていません！").queue();
					return;
				}
			}

			FirefoxOptions BROWSER_OPTION = new FirefoxOptions();
			BROWSER_OPTION.setProfile(FFP);
			BROWSER_OPTION.addArguments("--headless");

			WebDriver DRIVER = new FirefoxDriver(BROWSER_OPTION);
			DRIVER.manage().window().setSize(new Dimension(1980, 1080));

			DRIVER.get(URL);

			//サイズのオプションがしていされているなら実行
			if(INTERACTION.getOption("size") != null){
				//サイズ指定あり
				if(INTERACTION.getOption("size").getAsString().equals("FULL")){
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
			INTERACTION.getHook().editOriginal("スクショしたよ").setAttachments(FileUpload.fromData(SCREENSHOT)).queue();

			//終了
			DRIVER.quit();
		} catch (Exception EX) {
			System.out.println("WS Errr");
			EX.printStackTrace();
			INTERACTION.getHook().editOriginal("無理でした").queue();
		}
	}
}
