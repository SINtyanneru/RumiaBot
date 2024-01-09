from http.server import BaseHTTPRequestHandler, HTTPServer

#カスタムハンドラを作成
class MyHandler(BaseHTTPRequestHandler):
	#リクエストが来たらこの関数が実行される
	def do_GET(self):
		REQUEST_URI = self.path
		self.send_response(200)
		self.send_header('Content-type', 'text/html')
		self.end_headers()
		self.wfile.write(b"python de HTTP")

#サーバーのアドレスとポートを指定
HOST = "0.0.0.0"
PORT = 8085

#サーバーを作成してハンドラを登録
server = HTTPServer((HOST, PORT), MyHandler)

print(f"Starting server on http://{HOST}:{PORT}")

#サーバーを開始
server.serve_forever()
