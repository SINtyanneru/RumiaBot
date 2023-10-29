#!/bin/bash
# ChatGPT作
# 仕様変更で保存するときのファイル名変わったけど、統一したいなら実行してね
# ファイル名に追加するプリフィックス
prefix="quote_"

# ディレクトリのパス
directory="./DOWNLOAD/MIQ"

# ディレクトリ内のPNGファイルを処理
for file in "$directory"/*.png; do
	# ファイルの拡張子を取得
	extension="${file##*.}"

	# ファイル名からディレクトリ部分を取り除いてファイル名のみを取得
	file_name="${file##*/}"

	# ファイル名がプリフィックスで始まっていない場合のみ処理を行う
	if [[ ! "$file_name" =~ ^${prefix}.* ]]; then
		# プリフィックスをファイル名の最初に追加した新しいファイル名を生成
		new_name="${prefix}${file_name}"

		# ファイル名を変更
		mv "$file" "${directory}/${new_name}"
		echo "ファイル名を変更しました： $file -> ${directory}/${new_name}"
	fi
done
