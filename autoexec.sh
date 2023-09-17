#!/bin/bash
while true; do
  node ./SRC/Main.js
  if [ $? -eq 0 ]; then
    echo "プログラムは正常に終了しました。"
    break
  else
    echo "プログラムはエラーで終了しました。再起動します..."
  fi
done