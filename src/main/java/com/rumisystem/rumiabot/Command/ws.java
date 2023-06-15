package com.rumisystem.rumiabot.Command;

import com.fasterxml.jackson.databind.SequenceWriter;
import com.rumisystem.rumiabot.Main;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;

import java.io.File;
import java.util.Objects;

import net.dv8tion.jda.api.interactions.InteractionHook;
import net.dv8tion.jda.api.utils.FileUpload;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.OutputType;
import org.openqa.selenium.TakesScreenshot;
import org.openqa.selenium.chrome.ChromeOptions;
import org.apache.commons.io.FileUtils;
import io.github.bonigarcia.wdm.WebDriverManager;


public class ws {
    public static void Main(SlashCommandInteractionEvent e){
        try{
            e.deferReply().queue();

            String URL;
            if(Objects.isNull(e.getInteraction().getOption("url"))){//Nullチェック、未だにJAVAのNullチェックがわからん
                e.getHook().editOriginal("URLが指定されていないのだ！").queue();
                return;
            }else {
                URL = e.getInteraction().getOption("url").getAsString();
            }

            if (!URL.startsWith("http://") && !URL.startsWith("https://")) {
                URL = "http://" + URL;
            }

            // WebDriverのインスタンスを作成
            WebDriverManager.chromedriver().setup();
            ChromeOptions options = new ChromeOptions();
            options.addArguments("--headless"); // ヘッドレスモードでブラウザを起動

            String BROWSER_NAME = "";

            if(!Objects.isNull(e.getInteraction().getOption("browser_name"))){//ブラウザ名指定があるか
                switch (e.getInteraction().getOption("browser_name").getAsString()){
                    case "FireFox":
                        options.addArguments("--user-agent=Mozilla/5.0 (X11; Linux x86_64; rv:102.0) Gecko/20100101 Firefox/102.0");
                        BROWSER_NAME = "ブラウザ名：FireFox";
                        break;
                    case "Rumisan":
                        options.addArguments("--user-agent=Mozilla/5.0 (X11; Linux x86_64; rv:102.0) Gecko/20100101 Firefox/102.0 Rumisan/1.0");
                        BROWSER_NAME = "ブラウザ名：るみさん";
                        break;
                    case "Floorp":
                        options.addArguments("--user-agent=Mozilla/5.0 (X11; Linux x86_64; rv:102.0) Gecko/20100101 Floorp/10.13.0");
                        BROWSER_NAME = "ブラウザ名：Floorp";
                        break;

                    default:
                        e.getHook().editOriginal("ブラウザ名が無効です").queue();
                        return;
                }
            }

            WebDriver driver = new ChromeDriver(options);

            // ウィンドウサイズを設定
            driver.manage().window().setSize(new org.openqa.selenium.Dimension(1980, 1080));

            // ブラウザを開く
            driver.get(URL);

            // スクリーンショットを撮る
            File screenshot = ((TakesScreenshot)driver).getScreenshotAs(OutputType.FILE);

            // スクリーンショットを送信する
            try {
                e.getHook().editOriginal("スクショしたのだ！：「" + driver.getTitle() + "」\n" + BROWSER_NAME).queue();
                e.getChannel().sendFiles(FileUpload.fromData(screenshot)).queue();
            } catch (Exception ex) {
                e.getHook().editOriginal("アップロード時にエラーが発生しましたのだ！").queue();
            }

            // ブラウザを閉じる
            driver.quit();
        }catch (Exception ex){
            e.getHook().editOriginal("取得時にエラーが発生したのだ！").queue();
        }
    }
}
