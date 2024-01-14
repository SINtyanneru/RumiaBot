import os
import datetime
import json
from MODULES_PYTHON.AJAX import AJAX
from MODULES_PYTHON.PRINT import PRINT
from SQL import RUN
from aiohttp import web
from MODULES_PYTHON.CONFIG import CONFIG_LOAD

CONTENTS_OWNER_PATH = os.getcwd() + "/SRC/HTTP/CONTENTS"

async def HTTP_HANDLE(REQ:web.Request):
	try:
		REQUEST_URI = REQ.url.path.replace("../", "")
		#ログを吐く
		PRINT(f"[ HTTP_SERVER ]Request:{REQUEST_URI}")

		#API
		if(REQUEST_URI.startswith("/API")):
			return web.Response(text="{\"STATUS\":true}",headers={"Content-type":"application/json; charset=UTF-8"}, status=200)
		elif(REQUEST_URI.startswith("/user")):#ユーザー
			#Misskeyログイン
			if(REQUEST_URI.startswith("/user/login/misskey/")):
				USER_INFO:list = REQUEST_URI.replace("/user/login/misskey/", "").split("/")     #ユーザーじょうほう
				USER_SESSION:str = REQ.query.get("session")                                     #セッションID
				SNS_SETTING:dict = SNS_SETTING_GET(CONFIG_LOAD()["SNS"], USER_INFO[0])          #SNSの設定

				#Noneちぇっく
				if((USER_SESSION is not None) and (USER_INFO[0] is not None) and (USER_INFO[1] is not None) and (SNS_SETTING is not None)):
					AJAX_RESULT = AJAX("https://" + SNS_SETTING["DOMAIN"] + "/api/miauth/" + USER_SESSION + "/check", {"HEADER": {}, "METHOD":"POST"})
					#AJAXが成功したか
					if(AJAX_RESULT is not None):
						AJAX_RESULT = json.loads(AJAX_RESULT)
						print(AJAX_RESULT)
						if(AJAX_RESULT["ok"]):
							if(len(RUN("SELECT * FROM `USER` WHERE `DID` = %s;", [USER_INFO[1]]))):
								print("書き換えています")
								RUN("UPDATE `USER` SET `SNS_TOKEN` = %s WHERE `USER`.`DID` = %s;", [AJAX_RESULT["token"], USER_INFO[1]])
							else:
								print("インサート")
								RUN("INSERT INTO `USER` (`ID`, `DID`, `NAME`, `SNS_TOKEN`) VALUES (NULL, %s, %s, %s);", [USER_INFO[1], "名無し", USER_INFO[0] + "/" + AJAX_RESULT["token"]])
							return web.Response(text=f"はい\nあなたは{USER_INFO[0]}のインスタンスを使用していて、{USER_INFO[1]}というDiscordIDですね",headers={"Content-type":"text/plain; charset=UTF-8"}, status=200)
						else:
							return web.Response(text=f"APIがエラーを吐きました",headers={"Content-type":"text/plain; charset=UTF-8"}, status=500)
					else:
						return web.Response(text=f"AJAXがエラー",headers={"Content-type":"text/plain; charset=UTF-8"}, status=500)
				else:
					return web.Response(text=f"パラメーターがNoneです",headers={"Content-type":"text/plain; charset=UTF-8"}, status=500)
			else:#どれでもない
				return web.Response(text=f"お前は：{REQUEST_URI.replace('/user', '')}にアクセスした\n{datetime.datetime.today()}",headers={"Content-type":"text/plain; charset=UTF-8"}, status=200)
		else:#管理画面
			#ファイルを読み込む
			CONTENTS = FILE_LOAD(REQUEST_URI)
			#ファイルが有るか
			if(CONTENTS is not None):#ある
				#拡張子によってヘッダーのMEMEを買える
				if(REQUEST_URI.endswith(".css")):
					#CSS
					return web.Response(text=CONTENTS,headers={"Content-type":"text/css; charset=UTF-8"}, status=200)
				else:#HTML
					return web.Response(text=CONTENTS,headers={"Content-type":"text/html; charset=UTF-8"}, status=200)
			else:#ファイルがない
				return web.Response(text=f"404 Page not found",headers={"Content-type":"text/plain; charset=UTF-8"}, status=404)
	except Exception as EX:#エラー
		return web.Response(text=f"500 Server err\n{EX}",headers={"Content-type":"text/plain; charset=UTF-8"}, status=500)

#HTTP鯖を起動する関数
def CREATE_HTTP_SERVER(HOST:str, PORT:int):
	#ログを吐く
	PRINT(f"Start HTTP server {HOST}:{PORT}")

	APP = web.Application()

	#ルートせってい(/{tail:.*}で/*ということらしい)
	APP.add_routes([web.get("/{tail:.*}", HTTP_HANDLE)])

	#HTTP鯖を開始
	web.run_app(APP, port=PORT)

def FILE_LOAD(PATH:str):
	PRINT("HTTP Reqest:" + CONTENTS_OWNER_PATH + PATH)
	#パスの先はファイルか
	if(os.path.isfile(CONTENTS_OWNER_PATH + PATH)):
		F = open(CONTENTS_OWNER_PATH + PATH)
		CONTENTS = F.read()
		F.close()
		return CONTENTS
	#ファイルじゃない
	elif(os.path.exists(CONTENTS_OWNER_PATH + "/index.html")):
		F = open(CONTENTS_OWNER_PATH + PATH + "index.html")
		CONTENTS = F.read()
		F.close()
		return CONTENTS
	#そもそもファイル自体無い
	else:
		return None
	
#SNSを検索
def SNS_SETTING_GET(SNS_SETTING, ID):
	for ROW in SNS_SETTING:
		#IDが一致したら
		if(ROW["ID"] == ID):
			#変えす
			return ROW
	#そんなSNSはない！
	return None