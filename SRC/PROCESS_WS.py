from asyncio import sleep
import socket

CONNECTION = None

async def PWS_MAIN():
	CONNECTION = socket.socket(socket.AF_INET, socket.SOCK_STREAM)
	CONNECTION.connect(("localhost", 3001))
	CONNECTION.send("HELLO;PY".encode("utf-8"))
	await sleep(1)
	CONNECTION.send("DISCORD;MSG_SEND;1164919064678903848;やりたいこと：Telnetのメッセージ受信のイベントリスナーの追加と破棄".encode("utf-8"))
	await sleep(1)
	CONNECTION.close()