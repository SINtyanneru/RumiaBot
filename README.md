# るみさんBOT

---

## なんだこのBOTは？！

元ルーミアちゃんBOT<BR>
このBOTは、わたしがほしいと思った機能を搭載するBOTです()<BR>
なので完全に自分用ｗｗ<BR>

## セットアップ方法

まずはNodeJSとnpmを入れてね<BR>

```sh
source ./setup.sh
```

を実行(LINUXの場合)<BR>
そして[Config.json]を作成<BR>

```json
{
	"TOKEN": "Discordのトークン",
	"ID": "BOTのID",
	"SQL_HOST": "SQLのホスト",
	"SQL_USER": "SQLのユーザー名",
	"SQL_PASS": "SQLのパスワード",
	"GOOGLE_API_KEY": "GoogleAPIのAPIキー",
	"GOOGLE_API_ENGINE_ID": "Google検索APIのえんじんDI",
	"ADMIN_ID": "BOTの管理者のID",
	"ADMIN_PREFIX": "管理用のプレフィクス",
	"SNS": [
		{
			"ID": "自由にIDを",
			"NAME": "インスタンス名",
			"DOMAIN": "ドメイン(httpsを含まない)",
			"API": "MisskeyやマストドンのAPIキー",
			"TYPE": "MISSKEYかMASTODON"
		}
	]
}
```

これでおｋ！<BR>
あとはsource start.shできどうするだけ！<BR>
あとは楽しんでください()<BR>

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
