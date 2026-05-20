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

# Bot->BaseSystemイベント
JSONをBase64として付属

Discordでサーバーに参加した
```
@DISCORD MESSAGE_RECEIVE {
	"GUILD_ID": "サーバーID"
}
```

Discordでサーバーから脱退した
```
@DISCORD GUILD_LEAVE {
	"GUILD_ID": "サーバーID"
}
```

Discordでサーバーにメンバーが参加
```
@DISCORD GUILD_MEMBER_JOIN {
	"GUILD_ID": "サーバーID",
	"USER_ID": "ユーザーID"
}
```

Discordでサーバーにメンバーが脱退
```
@DISCORD GUILD_MEMBER_LEAVE {
	"GUILD_ID": "サーバーID",
	"USER_ID": "ユーザーID"
}
```

Discordでメッセージを受信
```
@DISCORD MESSAGE_RECEIVE {
	"GUILD_ID": "サーバーID",
	"CHANNEL_ID": "チャンネルID",
	"USER_ID": "ユーザーID",
	"MESSAGE_ID": "メッセージID",
	"MESSAGE_TEXT": "メッセージの本文"
}
```

Discordでスラッシュコマンド実行
```
@DISCORD COMMAND_INTERACTION {
	"ID": "インタラクションID",
	"GUILD_ID": "サーバーID",
	"CHANNEL_ID": "チャンネルID",
	"USER_ID": "ユーザーID",

	"COMMAND_NAME": "コマンド名",
	"COMMAND_OPTION": {
		"オプション名": "値",
		"添付ファイルの場合": {
			"NAME": "ファイル名",
			"SIZE": ファイルサイズ(Long or Int),
			"TYPE": "ファイルタイプ(MimeType)",
			"URL": "ファイルURL"
		}
	}
}
```

Misskeyでジョブキューの処理状況に更新があった
```
@MISSKEY JOBQUEUE {
	DELIVER_PROCESS": "配送ジョブの処理中数",
	DELIVER_ACTIVE": "配送ジョブ",
	DELIVER_WAITING": "配送ジョブの処理待ち",
	DELIVER_DELAYED": "配送ジョブの処理失敗",
	INBOX_PROCESS": "Inboxジョブの処理中数",
	INBOX_ACTIVE": "Inboxジョブ",
	INBOX_WAITING": "Inboxジョブの処理待ち",
	INBOX_DELAYED": "Inboxジョブの処理失敗"
}
```

Misskeyでサーバーステータスに更新
```
@MISSKEY JOBQUEUE {
	"CPU_USE": "CPU使用率",
	"MEM_USE": "メモリ使用量",
	"MEM_FREE": "メモリ空き",
	"NET_RX": "ネットワーク受信",
	"NET_TX": "ネットワーク送信"
}
```