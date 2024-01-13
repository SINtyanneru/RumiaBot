#なぜかJAVAで出力を検知できないので、なんか有ったときのために作った
import sys

def PRINT(TEXT:str):
	sys.stdout.write(f"{TEXT}\n")