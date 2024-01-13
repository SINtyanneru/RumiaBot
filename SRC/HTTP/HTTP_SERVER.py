import os
import datetime
import json
from MODULES_PYTHON.AJAX import AJAX
from MODULES_PYTHON.PRINT import PRINT
from SQL import RUN
from aiohttp import web

CONTENTS_OWNER_PATH = os.getcwd() + "/SRC/HTTP/CONTENTS"

async def HTTP_HANDLE(REQ:web.Request):
	REQUEST_URI = REQ.url.path.replace("../", "")
	#ログを吐く
	PRINT(f"[ HTTP_SERVER ]Request:{REQUEST_URI}")

	#API
	if(REQUEST_URI.startswith("/API")):
		return web.Response(text="{\"STATUS\":true}",headers={"Content-type":"application/json; charset=UTF-8"}, status=200)
	elif(REQUEST_URI.startswith("/user")):#ユーザー
		#Misskeyログイン
		if(REQUEST_URI.startswith("/user/login/misskey/")):
			print(RUN("SELECT * FROM `SNS`", []))
			AJAX_RESULT = AJAX("https://rumiserver.com/API/ACCOUNT/ACCOUNT_GET?UID=Kazemidori_x86", {"HEADER":{}})
			#AJAXが成功したか
			if(AJAX_RESULT is not None):
				AJAX_RESULT = json.loads(AJAX_RESULT)
				return web.Response(text=f"はい",headers={"Content-type":"text/plain; charset=UTF-8"}, status=200)
			else:#失敗
				return web.Response(text=f"AJAXに失敗しました",headers={"Content-type":"text/plain; charset=UTF-8"}, status=500)
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