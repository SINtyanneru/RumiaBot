from http.server import BaseHTTPRequestHandler, HTTPServer

#カスタムハンドラを作成
class MyHandler(BaseHTTPRequestHandler):
	#リクエストが来たらこの関数が実行される
	def do_GET(SELF):
		REQUEST_URI = SELF.path
		SELF.send_response(200)
		SELF.send_header('Content-type', 'text/html')
		SELF.end_headers()
		SELF.wfile.write(b"python de HTTP")

#サーバーのアドレスとポートを指定
HOST = "0.0.0.0"
PORT = 8085

#サーバーを作成してハンドラを登録
SERVER = HTTPServer((HOST, PORT), MyHandler)

print(f"Starting server on http://{HOST}:{PORT}")

#サーバーを開始
SERVER.serve_forever()
