package com.rumisystem.rumiabot.jda.MODULE;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;

public class FILE_WRITER {

	public FILE_WRITER(String PATH, String DATA){
		try{
			File FILE = new File(PATH);
			if(!FILE.exists()) {
				FILE.createNewFile();
			}

			//書き込む
			BufferedWriter WRITER = new BufferedWriter(new FileWriter(FILE));

			//書き込み
			WRITER.write(DATA);

			//メモリ開放
			WRITER.close();
		}catch (Exception EX){
			EX.printStackTrace();
		}
	}
}
