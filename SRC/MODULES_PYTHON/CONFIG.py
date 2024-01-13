import os
import sys
import json
from MODULES_PYTHON.PRINT import PRINT

CONFIG_DATA:dict = None

#設定ファイルを読みこむ
def CONFIG_LOAD():
	global CONFIG_DATA
	try:
		if(CONFIG_DATA is None):
			PRINT("設定ファイルを読みこでいます")
			#設定ファイルが有るか
			if(os.path.exists(os.getcwd() + "/Config.json")):
				F = open(os.getcwd() + "/Config.json", "r")
				CONTENTS = F.read()
				CONFIG_DATA = json.loads(CONTENTS)
				F.close()
				return CONFIG_DATA
			else:#設定ファイルがないので強制終了
				sys.exit(1)
		else:
			return CONFIG_DATA
	except Exception as EX:
		sys.stderr.write(EX)
		sys.exit(1)