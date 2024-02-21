# るみさんBOT

---

## なんだこのBOTは？！

元ルーミアちゃんBOT<BR>
このBOTは、わたしがほしいと思った機能を搭載するBOTです()<BR>
なので完全に自分用ｗｗ<BR>

## セットアップ方法

まずはNodeJSとnpmとGradle入れてね<BR>
そしたらJAVAディレクトリ内のソースコードをビルドしてね<BR>
そしたらライブラリを入れてね<BR>
```sh
npm i
```
を実行<BR>
そして[Config.json]を生成するか自分で作ってね<BR>

```json
{
	"DISCORD": {
		"TOKEN": "BOTのトークン"
	},
	"SQL": {
		"SQL_CONNECT": true,//接続するか
		"SQL_HOST": "SQLのホスト",
		"SQL_USER": "SQLのユーザー",
		"SQL_PASS": "パスワード",
		"SQL_DB": "データベース名"
	},
	"GOOGLE_SEARCH": {
		"GOOGLE_API_KEY": "GoogleのAPIキー",
		"GOOGLE_API_ENGINE_ID": "検索エンジンID"
	},
	"SNS": [
		{
			"ID": "接続先インスタンスのID",
			"NAME": "名前(自由)",
			"DOMAIN": "ドメイン",
			"API": "APIキー",
			"TYPE": "MISSKEYもしくはMASTODON"
		}
	],
	"ADMIN": {
		"ADMIN_ID": [
			"管理者のDiscordのID"
		],
		"ADMIN_PREFIX": "管理用コマンドのプレフィクス",
		"DISABLE": [],
		"BLOCK": [
			"ブロックするユーザー"
		]
	}
}
```

次に起動<BR>
```sh
npm run debug
```
もしくは
```sh
npm run start
```

## ライブラリ

fs<BR>
discord.js(V13)<BR>
child_process<BR>
net<BR>
ws<BR>
http<BR>
https<BR>
selenium-webdriver<BR>
selenium-webdriver/chrome<BR>

## ライセンス

いつもどおり、「るみしすてむアプリケーションライセンス V1.0」です！

## ディレクトリ構成

SRCにBOTのコードが、JSにJavaScript、PYにPython、JAVAにJAVAがあります<BR>
JAVAフォルダは、BOTのコードを実行するやつ