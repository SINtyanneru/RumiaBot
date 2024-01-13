import mysql.connector

SQL_CONNECTION:mysql.connector.connection.MySQLConnection = None

#SQLに接続する
def CONNECT(HOST:str, USER:str, PASS:str, DB:str):
	global SQL_CONNECTION
	SQL_CONNECTION = mysql.connector.connect(
		host=HOST,
		user=USER,
		password=PASS,
		database=DB
	)

#SQL文を実行する
def RUN(SQL_SCRIPT:str, PARAM:list):
	global SQL_CONNECTION
	if(SQL_CONNECTION is not None):
		#カーソル作成
		CURSOR = SQL_CONNECTION.cursor()

		SQL_SCRIPT = SQL_SCRIPT.format(PARAM)

		CURSOR.execute(SQL_SCRIPT)

		return CURSOR.fetchall()
	else:
		return None
