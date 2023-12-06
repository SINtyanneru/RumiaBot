import { Builder } from "selenium-webdriver";
import chrome from "selenium-webdriver/chrome.js";
import firefox from "selenium-webdriver/firefox.js";
import FS from "fs";
import { RUMI_HAPPY_BIRTHDAY } from "../MODULES/RUMI_HAPPY_BIRTHDAY.js";
import { CONFIG } from "../MODULES/CONFIG.js";
export class WS {
	/** @param {import("discord.js").CommandInteraction} INTERACTION */
	constructor(INTERACTION) {
		this.E = INTERACTION;
	}

	async main() {
		let E = this.E;
		if (CONFIG.DISABLE?.includes("ws")) {
			return E.editReply("運営者の意向により、この機能は無効化されています！");
		}
		let REQUEST_URL = undefined;
		let BROWSER_NAME = E.options.getString("browser_name");
		let BROWSER_NAME_FF = null;
		let BROWSER_NAME_TEXT = null;

		//ブラウザ名を標準でFireFoxに
		if (!BROWSER_NAME) {
			BROWSER_NAME = "firefox";
		}

		if (E.options.getString("url").startsWith("http")) {
			REQUEST_URL = new URL(E.options.getString("url"));
		} else {
			REQUEST_URL = new URL("http://" + E.options.getString("url"));
		}

		//URLのポートが不正じゃないか
		if (!(REQUEST_URL.port === "80" || REQUEST_URL.port === "443" || REQUEST_URL.port === "")) {
			//なんでポート番号がURLになかったらStringNullなんだよしかもなんでStringなんだよ頭大丈夫か開発者
			E.editReply("ポート番号が不正です");
			return;
		}

		if (REQUEST_URL) {
			try {
				//Chromeのオプションを設定
				let BROWSER_OPTION = null;

				if (BROWSER_NAME) {
					switch (BROWSER_NAME) {
						case "firefox":
							BROWSER_OPTION = new firefox.Options();
							BROWSER_OPTION.addArguments("--headless"); //ヘッドレスモードで実行
							BROWSER_NAME_TEXT = "FireFox";
							BROWSER_NAME_FF = "firefox";
							break;
						case "floorp":
							BROWSER_OPTION = new firefox.Options();
							BROWSER_OPTION.addArguments("--headless"); //ヘッドレスモードで実行
							BROWSER_OPTION.addArguments("--user-agent=Mozilla/5.0 (X11; Linux x86_64; rv:102.0) Gecko/20100101 Floorp/10.13.0");
							BROWSER_NAME_TEXT = "Floorp";
							BROWSER_NAME_FF = "firefox";
							break;
						case "rumisan":
							BROWSER_OPTION = new firefox.Options();
							BROWSER_OPTION.addArguments("--headless"); //ヘッドレスモードで実行
							BROWSER_OPTION.addArguments("--user-agent=Mozilla/5.0 (X11; Linux x86_64; rv:102.0; rumisan:" + RUMI_HAPPY_BIRTHDAY() + ".0) Gecko/20100101 Firefox/102.0");
							BROWSER_NAME_TEXT = "るみさん";
							break;
						case "chrome":
							BROWSER_OPTION = new chrome.Options();
							BROWSER_OPTION.addArguments("--headless"); //ヘッドレスモードで実行
							BROWSER_OPTION.addArguments("--user-agent=Mozilla/5.0 (X11; Linux x86_64; rv:102.0; rumisan:" + RUMI_HAPPY_BIRTHDAY() + ".0) Gecko/20100101 Firefox/102.0");
							BROWSER_NAME_TEXT = "Chrome";
							BROWSER_NAME_FF = "chrome";
							break;
						default:
							E.editReply("ブラウザ名が無効です");
							break;
					}
				} else {
					await E.editReply("ブラウザ名が無効です");
					return;
				}

				//WebDriverのインスタンスを作成
				let DRIVER = null;
				if (BROWSER_NAME_FF === "chrome") {
					//Chromeの場合(Chrome切り捨てたい)
					DRIVER = new Builder().forBrowser(BROWSER_NAME_FF).setChromeOptions(BROWSER_OPTION).build();
				} else {
					//FireFoxの場合
					DRIVER = new Builder().forBrowser(BROWSER_NAME_FF).setFirefoxOptions(BROWSER_OPTION).build();
				}

				//ヰンドウサイズ
				await DRIVER.manage().window().setRect({ width: 1980, height: 1080 });
				//ウェブサイトにアクセス
				await DRIVER.get(REQUEST_URL.toString());
				//スクショする
				await DRIVER.takeScreenshot().then(async SCREENSHOT_DATA => {
					try {
						FS.writeFileSync("./DOWNLOAD/" + E.member.id + ".png", SCREENSHOT_DATA, "base64");

						await E.editReply({
							content: "おｋ、" + BROWSER_NAME_TEXT + "で撮影したよ",
							files: ["./DOWNLOAD/" + E.member.id + ".png"]
						});
						return;
					} catch (EX) {
						console.error("[ ERR ][ WebScreenShot ]", EX);
						E.editReply("接続できませんでした！" + EX);
						return;
					}
				});
			} catch (EX) {
				console.error("[ ERR ][ WebScreenShot ]", EX);
				await E.editReply("接続できませんでした！");
				return;
			}
		} else {
			await E.editReply("URLが指定されていません");
			return;
		}
	}
}
