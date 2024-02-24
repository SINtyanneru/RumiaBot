package com.rumisystem.rumiabot.jda.COMMAND;

import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.utils.FileUpload;
import org.openqa.selenium.Dimension;
import org.openqa.selenium.OutputType;
import org.openqa.selenium.TakesScreenshot;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.firefox.FirefoxDriver;
import org.openqa.selenium.firefox.FirefoxOptions;

import java.io.File;

public class ws {
	public static void main(SlashCommandInteractionEvent INTERACTION) {
		try{
			String URL = INTERACTION.getOption("url").getAsString();

			FirefoxOptions BROWSER_OPTION = new FirefoxOptions();
			BROWSER_OPTION.addArguments("--headless");

			WebDriver DRIVER = new FirefoxDriver(BROWSER_OPTION);
			DRIVER.manage().window().setSize(new Dimension(1980, 1080));

			DRIVER.get(URL);

			File SCREENSHOT = ((TakesScreenshot)DRIVER).getScreenshotAs(OutputType.FILE);

			//返答
			INTERACTION.getHook().editOriginal("スクショしたよ").setAttachments(FileUpload.fromData(SCREENSHOT)).queue();

			//終了
			DRIVER.quit();
		} catch (Exception EX) {
			EX.printStackTrace();
			INTERACTION.getHook().editOriginal("無理でした").queue();
		}
	}
}
