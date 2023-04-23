package com.rumisystem.rumiabot.Command;

import com.fasterxml.jackson.databind.SequenceWriter;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;

import java.io.File;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.OutputType;
import org.openqa.selenium.TakesScreenshot;
import org.openqa.selenium.chrome.ChromeOptions;
import org.apache.commons.io.FileUtils;
import io.github.bonigarcia.wdm.WebDriverManager;


public class ws {
    public static void Main(MessageReceivedEvent e){
        try{
            String[] cmd = e.getMessage().getContentRaw().split(" ");//コマンドの全てを取得

            e.getMessage().reply("スクショ中、、、、").queue();

            // WebDriverのインスタンスを作成
            WebDriverManager.chromedriver().setup();
            ChromeOptions options = new ChromeOptions();
            options.addArguments("--headless"); // ヘッドレスモードでブラウザを起動
            WebDriver driver = new ChromeDriver(options);

            // ウィンドウサイズを設定
            driver.manage().window().setSize(new org.openqa.selenium.Dimension(1980, 1080));

            // ブラウザを開く
            driver.get(cmd[1]);

            // スクリーンショットを撮る
            File screenshot = ((TakesScreenshot)driver).getScreenshotAs(OutputType.FILE);

            // スクリーンショットを送信する
            try {
                //FileUtils.copyFile(screenshot, new File("screenshot.png"));
                e.getMessage().reply(screenshot).queue();
            } catch (Exception ex) {
                e.getMessage().reply("エラー" + ex.getMessage()).queue();
            }

            // ブラウザを閉じる
            driver.quit();
        }catch (Exception ex){
            e.getMessage().reply("エラー" + ex.getMessage()).queue();
        }
    }
}
