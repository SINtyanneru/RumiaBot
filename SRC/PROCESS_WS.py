from asyncio import sleep
import socket
import uuid

CONNECTION:socket.socket = None

async def PWS_MAIN():
	global CONNECTION
	CONNECTION = socket.socket(socket.AF_INET, socket.SOCK_STREAM)
	CONNECTION.connect(("localhost", 3001))
	print(await SEND_MSG("HELLO;PY"))
	await sleep(1)
	print(await SEND_MSG("DISCORD;MSG_SEND;1164919064678903848;PythonがらJSにメッセージ送信命令"))
	await sleep(1)
	CONNECTION.close()

async def SEND_MSG(TEXT:str):
	global CONNECTION
	#UUIDを生成
	JOB_UUID = str(uuid.uuid4())

	#送信
	CONNECTION.send((JOB_UUID + ";" + TEXT).encode("utf-8"))

	#ぐるぐる
	while True:
		DATA = CONNECTION.recv(1024)
		if not DATA:
			break
		MSG = DATA.decode("utf-8").split(";")
		if(MSG[0] == JOB_UUID):
			return MSG