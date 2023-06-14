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
            String URL;
            if(Objects.isNull(e.getInteraction().getOption("url"))){//Nullチェック、未だにJAVAのNullチェックがわからん
                e.getInteraction().reply("URLが指定されていないのだ！").queue();
                return;
            }else {
                URL = e.getInteraction().getOption("url").getAsString();
            }
            // WebDriverのインスタンスを作成
            WebDriverManager.chromedriver().setup();
            ChromeOptions options = new ChromeOptions();
            options.addArguments("--headless"); // ヘッドレスモードでブラウザを起動
            WebDriver driver = new ChromeDriver(options);

            // ウィンドウサイズを設定
            driver.manage().window().setSize(new org.openqa.selenium.Dimension(1980, 1080));

            // ブラウザを開く
            driver.get(URL);

            // スクリーンショットを撮る
            File screenshot = ((TakesScreenshot)driver).getScreenshotAs(OutputType.FILE);

            // スクリーンショットを送信する
            try {
                e.getInteraction().replyFiles(FileUpload.fromData(screenshot)).queue();
            } catch (Exception ex) {
                e.getInteraction().reply("エラー" + ex.getMessage()).queue();
            }

            // ブラウザを閉じる
            driver.quit();
        }catch (Exception ex){
            e.getInteraction().reply("エラー" + ex.getMessage()).queue();
        }
    }
}
