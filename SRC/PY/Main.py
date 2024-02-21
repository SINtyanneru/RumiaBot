from HTTP.HTTP_SERVER import CREATE_HTTP_SERVER
from MODULES_PYTHON.PRINT import PRINT
from MODULES_PYTHON.CONFIG import CONFIG_LOAD
from PROCESS_WS import PWS_MAIN
import asyncio

async def main():
	PRINT("RumiaBOT Python Script\n")

	CONFIG_DATA:dict = CONFIG_LOAD()

	await PWS_MAIN()
	await asyncio.get_event_loop().run_in_executor(None, CREATE_HTTP_SERVER, "0.0.0.0", 3000)

#main関数を実行
asyncio.run(main())