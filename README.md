# るみさんBOT
[https://bot.rumi-room.net/site/](https://bot.rumi-room.net/site/)

# 構造
  [rumiabot][ai][rumina]
        [Base System]
[        るみさんbot        ]
            ↓↑
Misskey Discord RumiChat Telegram

# BaseSystem->Botコマンド
基本構文
```
/コマンド オプションたち <コマンドID>
```
応答(ない場合もある)
```
<コマンドID> ステータスコード
```

## Misskey
### ノート変更
```
MISSKEY NOTE テキスト(Base64) リプライ先 引用先 公開範囲 ローカルのみか
```
公開範囲はDMであれば自動的にDMとなる
例：/MISSKEY NOTE 接続しました null null HOME false

```
・PUBLIC
公開投稿
・HOME
ホーム投稿
・DM
DM投稿
```

## Discord
### アクティビティ変更
```
DISCORD ACTIVITY アクティビティ名 テキスト(Base64) URL(Base64)
```
例：/DISCORD ACTIVITY WATCHING 貴様
```
・PLAYING
〜をプレイ中
・WATCHING
〜を視聴中
・STREAMING
〜を配信中
```

### ステータス変更
```
DISCORD STATUS ステータス
```
例：/DISCORD STATUS ONLINE
```
・ONLINE
オンライン
・IDLE
退席中
・DO_NOT_DISTURB
取り込み中
・OFFLINE
オフライン
```