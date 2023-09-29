import { Builder } from "selenium-webdriver";
import chrome from "selenium-webdriver/chrome.js";
import FS from "fs";
import { RUMI_HAPPY_BIRTHDAY } from "../MODULES/RUMI_HAPPY_BIRTHDAY.js";
import { CONFIG } from "../MODULES/CONFIG.js";
export class WS {
	constructor(INTERACTION) {
		this.E = INTERACTION;
	}

	async main() {
		let E = this.E;
		if (CONFIG.DISABLE?.includes("ws")) {
			return E.editReply("運営者の意向により、この機能は無効化されています！");
		}
		let URL = E.options.getString("url");
		let BROWSER_NAME = E.options.getString("browser_name");
		let BROWSER_NAME_TEXT = "Chrome";

		if (URL !== undefined && URL !== null) {
			//URLの整形
			if (!URL.startsWith("http")) {
				URL = "http://" + URL;
			}

			//Chromeのオプションを設定
			const chromeOptions = new chrome.Options();
			chromeOptions.addArguments("--headless"); // ヘッドレスモードで実行
			chromeOptions.addArguments("--window-size=1980,1080"); // ウィンドウのサイズを設定

			if (BROWSER_NAME !== undefined && BROWSER_NAME !== null) {
				switch (BROWSER_NAME) {
					case "firefox":
						chromeOptions.addArguments("--user-agent=Mozilla/5.0 (X11; Linux x86_64; rv:102.0) Gecko/20100101 Firefox/102.0");
						BROWSER_NAME_TEXT = "FireFox";
						break;
					case "floorp":
						chromeOptions.addArguments("--user-agent=Mozilla/5.0 (X11; Linux x86_64; rv:102.0) Gecko/20100101 Floorp/10.13.0");
						BROWSER_NAME_TEXT = "Floorp";
						break;
					case "rumisan":
						chromeOptions.addArguments("--user-agent=Mozilla/5.0 (X11; Linux x86_64; rv:102.0) Gecko/20100101 Firefox/102.0 Rumisan/" + RUMI_HAPPY_BIRTHDAY() + ".0");
						BROWSER_NAME_TEXT = "るみさん";
						break;
					default:
						E.editReply("ブラウザ名が無効です");
						break;
				}
			}

			try {
				//WebDriverのインスタンスを作成
				const driver = new Builder().forBrowser("chrome").setChromeOptions(chromeOptions).build();

				//ウェブサイトにアクセス
				driver
					.get(URL)
					.then(() => {
						//スクリーンショットを撮影
						return driver.takeScreenshot();
					})
					.then(screenshotData => {
						try {
							FS.writeFileSync("./DOWNLOAD/" + E.member.id + ".png", screenshotData, "base64");

							E.editReply({
								content: "おｋ：" + BROWSER_NAME_TEXT + "で撮影",
								files: ["./DOWNLOAD/" + E.member.id + ".png"]
							});
						} catch (EX) {
							E.editReply("接続できませんでした！" + EX);
						}
					})
					.catch(() => {
						E.editReply("接続できませんでした！");
						//WebDriverを終了
						driver.quit();
					})
					.finally(() => {
						//WebDriverを終了
						driver.quit();
					});
			} catch (EX) {
				E.editReply("接続できませんでした！");
			}
		} else {
			E.editReply("URLが指定されていません");
		}
	}
}
