#!/bin/bash

# デフォルト値の設定
DEFAULT_ADMIN_PREFIX="EXEC_"

# ユーザーに入力を求める関数
ask() {
  local prompt="$1"
  local var_name="$2"
  local default_value="$3"

  while true; do
    if [ -z "$default_value" ]; then
      read -p "$prompt: " "$var_name"
    else
      read -p "$prompt [$default_value]: " input
      if [ -z "$input" ]; then
        eval "$var_name=\"$default_value\""
      else
        eval "$var_name=\"$input\""
      fi
    fi

    # 必須プロパティのアサーション
    if [ -z "${!var_name}" ]; then
      echo "エラー: $prompt は必須プロパティです。再入力してください。"
    else
      break
    fi
  done
}

# メインの設定ファイルを作成
config_file="config.json"

echo "#############################################\n設定ファイルを作成します。\n#############################################"
echo "\n"
# ユーザーにオプションを選択させる
PS3="どれにしますか？※元のConfig.jsonは上書きされます
> "
options=("手動で入力" "ダミーファイルを作成" "何もしない")
select opt in "${options[@]}"; do
  case $REPLY in
  1) # 手動で入力
    echo "#############################################\n手動で作成します。Enterを押すと一部の入力が必須の項目以外、空のまま進みます。\n#############################################"
    # TOKEN (必須)
    ask "TOKEN (必須)" TOKEN

    # SQL_HOST (任意)
    ask "SQL_HOST" SQL_HOST "NULL"

    # SQL_USER (任意)
    ask "SQL_USER" SQL_USER "NULL"

    # SQL_PASS (任意)
    ask "SQL_PASS" SQL_PASS "NULL"

    # GOOGLE_API_KEY (任意)
    ask "GOOGLE_API_KEY" GOOGLE_API_KEY "NULL"

    # GOOGLE_API_ENGINE_ID (任意)
    ask "GOOGLE_API_ENGINE_ID" GOOGLE_API_ENGINE_ID "NULL"

    # SNS (スルー)
    echo "SNS: スキップ"
    echo "SNSは複雑なので、ここでは処理しません。"

    # ADMIN_ID (必須)
    ask "ADMIN_ID (必須)" ADMIN_ID

    # ADMIN_PREFIX (デフォルトはEXEC_)
    ask "ADMIN_PREFIX (デフォルトはEXEC_)" ADMIN_PREFIX "$DEFAULT_ADMIN_PREFIX"

    # DISABLE (スルー)
    echo "DISABLE: スキップ"
    echo "無効化機能は普通必要ないため、ここでは処理しません。"
    # 設定ファイルを生成
    cat <<EOL >"$config_file"
{
    "TOKEN": "$TOKEN",
    "SQL_HOST": "$SQL_HOST",
    "SQL_USER": "$SQL_USER",
    "SQL_PASS": "$SQL_PASS",
    "GOOGLE_API_KEY": "$GOOGLE_API_KEY",
    "GOOGLE_API_ENGINE_ID": "$GOOGLE_API_ENGINE_ID",
    "SNS": [],
    "ADMIN_ID": "$ADMIN_ID",
    "ADMIN_PREFIX": "$ADMIN_PREFIX",
    "DISABLE":[]
}
EOL

    echo "設定ファイル $config_file が作成されました。"

    break
    ;;
  2) # ダミーファイルを作成
    cat <<EOL >"$config_file"
{
    "TOKEN": "required",
    "SQL_HOST": "",
    "SQL_USER": "",
    "SQL_PASS": "",
    "GOOGLE_API_KEY": "",
    "GOOGLE_API_ENGINE_ID": "",
    "SNS": [],
    "ADMIN_ID": ["required"],
    "ADMIN_PREFIX": "EXEC_",
    "DISABLE":[]
}
EOL
    echo "#############################################\n自動で $config_file が作成されました。実際に使用する時に必須の値を埋めてください\n######################################################"
    break
    ;;
  3) # 何もしない
    echo "#############################################\n終了するを選んだので、終了します。\n#############################################"
    break
    ;;
  *) echo "$REPLY は選択肢にありません。1~3のうちどれかを選んでください。" ;;
  esac
done
