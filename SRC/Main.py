from HTTP.HTTP_SERVER import CREATE_HTTP_SERVER
from MODULES_PYTHON.PRINT import PRINT

def main():
	PRINT("RumiaBOT Python Script\n")

	CREATE_HTTP_SERVER("0.0.0.0", 3000)

#main関数を実行
main()