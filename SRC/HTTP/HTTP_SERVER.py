from http.server import BaseHTTPRequestHandler, HTTPServer
from threading import Thread
import os

CONTENTS_OWNER_PATH = os.getcwd() + "/SRC/HTTP/CONTENTS"

#カスタムハンドラを作成
class HTTP_Handler(BaseHTTPRequestHandler):
	#リクエストが来たらこの関数が実行される
	def do_GET(SELF):
		REQUEST_URI = SELF.path.replace("../", "")
		#リクエストURIによって内容を買える
		if(REQUEST_URI.startswith("/API")):#API
			SELF.send_response(200)
			SELF.send_header("Content-type", "application/json; charset=UTF-8")
			SELF.end_headers()
			SELF.wfile.write("{\"STATUS\":true}".encode("UTF-8"))
		elif(REQUEST_URI.startswith("/user")):
			SELF.send_response(200)
			SELF.send_header("Content-type", "text/html; charset=UTF-8")
			SELF.end_headers()
			SELF.wfile.write("作ってる".encode("UTF-8"))
		else:#どれでもないので
			#ファイルを読み込む
			CONTENTS = FILE_LOAD(REQUEST_URI)
			#ファイルが有るか
			if(CONTENTS is not None):#ある
				#ステータスコード
				SELF.send_response(200)
				#拡張子によってヘッダーのMEMEを買える
				if(REQUEST_URI.endswith(".css")):
					#CSS
					SELF.send_header("Content-type", "text/css; charset=UTF-8")
					SELF.end_headers()
				else:#HTML
					SELF.send_header("Content-type", "text/html; charset=UTF-8")
					SELF.end_headers()
				SELF.wfile.write(CONTENTS.encode("UTF-8"))
			else:#ファイルがない
				SELF.send_response(404)
				SELF.send_header("Content-type", "text/html; charset=UTF-8")
				SELF.end_headers()
				SELF.wfile.write("404 Page not found".encode("UTF-8"))

#HTTP鯖を起動する関数
def CREATE_HTTP_SERVER(HOST:str, PORT:int):
	#サーバーを作成してハンドラを登録
	SERVER = HTTPServer((HOST, PORT), HTTP_Handler)

	#ログを吐く
	print(f"Start HTTP server\n{HOST}:{PORT}")

	#サーバーを開始
	HTTP_SERVER_TH = Thread(target=SERVER.serve_forever)
	HTTP_SERVER_TH.start()

def FILE_LOAD(PATH:str):
	print("HTTP Reqest:" + CONTENTS_OWNER_PATH + PATH)
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