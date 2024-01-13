#HTTPリクエストを送るのだ

from urllib import request

def AJAX(URL:str, SETTING:dict):
	try:
		#ヘッダー
		HEADER = {}

		#設定を読み込むm
		if(SETTING["HEADER"] is not None):
			HEADER = SETTING["HEADER"]

		#リクエストを送る
		REQ = request.Request(URL, headers=HEADER)
		with request.urlopen(REQ) as RES:
			RESULT = RES.read()
		return RESULT
	except:
		return None