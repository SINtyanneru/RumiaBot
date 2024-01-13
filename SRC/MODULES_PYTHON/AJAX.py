#HTTPリクエストを送るのだ

from urllib import request

def AJAX(URL:str, SETTING:dict):
	try:
		#ヘッダー
		HEADER = {}
		METHOD = "GET"

		#設定を読み込むm
		if(SETTING["HEADER"] is not None):
			HEADER = SETTING["HEADER"]
		if(SETTING["METHOD"] is not None):
			METHOD = SETTING["METHOD"]

		#リクエストを送る
		REQ = request.Request(URL, headers=HEADER, method=METHOD)
		with request.urlopen(REQ) as RES:
			RESULT = RES.read()
		return RESULT
	except:
		return None