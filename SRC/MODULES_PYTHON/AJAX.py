#HTTPリクエストを送るのだ

import urllib.request
import sys
from MODULES_PYTHON.PRINT import PRINT

def AJAX(URL:str, SETTING:dict):
	try:
		PRINT("[ AJAX ]Request:" + URL)
		#ヘッダー
		HEADER:dict = {}
		METHOD:str = "GET"

		#設定を読み込むm
		if(SETTING["HEADER"] is not None):
			HEADER = SETTING["HEADER"]
		if(SETTING["METHOD"] is not None):
			METHOD = SETTING["METHOD"]

		#リクエストを送る
		REQ = urllib.request.Request(URL, headers=HEADER, method=METHOD)
		with urllib.request.urlopen(REQ) as RES:
			PRINT("[ AJAX ]Status:" + str(RES.status))
			RESULT = RES.read()
		return RESULT
	except Exception as e:
		print(e)
		return None