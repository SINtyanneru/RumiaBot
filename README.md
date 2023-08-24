# るみさんBOT
___
## なんだこのBOTは？！
元ルーミアちゃんBOT<BR>
このBOTは、わたしがほしいと思った機能を搭載するBOTです()<BR>
なので完全に自分用ｗｗ<BR>
## ビルド方法
```sh
source ./build.sh
```
を実行(LINUXの場合)<BR>
そして./build/libsに、[Config.json]を作成<BR>
```json
{
	"TOKEN":"Discordのトークン",
	"ID":"BOTのID",
	"SQL_HOST":"SQLのホスト",
	"SQL_USER":"SQLのユーザー名",
	"SQL_PASS":"SQLのパスワード",
	"GOOGLE_API_KEY":"GoogleAPIのAPIキー",
	"GOOGLE_API_ENGINE_ID":"Google検索APIのえんじんDI",
	"ADMIN_ID":"BOTの管理者のID",
	"ADMIN_PREFIX":"管理用のプレフィクス"
}
```
これでおｋ！<BR>
あとはjava -jar ./rumiabot-1.0-SNAPSHOT-allできどうするだけ！<BR>
あとは楽しんでください()<BR>
## ライセンス
いつもどおり、「るみしすてむアプリケーションライセンス V1.0」です！