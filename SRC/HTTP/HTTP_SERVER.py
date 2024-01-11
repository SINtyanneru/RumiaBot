from http.server import BaseHTTPRequestHandler, HTTPServer

#カスタムハンドラを作成
class HTTP_Handler(BaseHTTPRequestHandler):
	#リクエストが来たらこの関数が実行される
	def do_GET(SELF):
		REQUEST_URI = SELF.path
		SELF.send_response(200)
		SELF.send_header('Content-type', 'text/html')
		SELF.end_headers()
		SELF.wfile.write(b"python de HTTP")

def CREATE_HTTP_SERVER(HOST:str, PORT:int):
	#サーバーを作成してハンドラを登録
	SERVER = HTTPServer((HOST, PORT), HTTP_Handler)

	print(f"Starting server on http://{HOST}:{PORT}")

	#サーバーを開始
	SERVER.serve_forever()