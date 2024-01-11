from http.server import BaseHTTPRequestHandler, HTTPServer

#カスタムハンドラを作成
class HTTP_Handler(BaseHTTPRequestHandler):
	#リクエストが来たらこの関数が実行される
	def do_GET(SELF):
		REQUEST_URI = SELF.path
		if(REQUEST_URI == "/"):
			F = open("/home/rumisan/source/RumiaBot/SRC/HTTP/CONTENTS/index.html", "r", encoding="UTF-8")
			SELF.send_response(200)
			SELF.send_header("Content-type", "text/html; charset=UTF-8")
			SELF.end_headers()
			SELF.wfile.write(F.read().encode("utf-8"))
			F.close()

#HTTP鯖を起動する関数
def CREATE_HTTP_SERVER(HOST:str, PORT:int):
	#サーバーを作成してハンドラを登録
	SERVER = HTTPServer((HOST, PORT), HTTP_Handler)

	#ログを吐く
	print(f"Start HTTP server\n{HOST}:{PORT}")

	#サーバーを開始
	SERVER.serve_forever()