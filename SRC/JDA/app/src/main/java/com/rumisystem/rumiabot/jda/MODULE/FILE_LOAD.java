package com.rumisystem.rumiabot.jda.MODULE;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;

public class FILE_LOAD {
	private File FILE = null;

	public FILE_LOAD(String PATH){
		File SELECT_FILE = new File(PATH);
		if(SELECT_FILE.exists()) {
			FILE = SELECT_FILE;
		} else {
			throw new RuntimeException("File not found");
		}
	}

	public String LOAD(){
		try{
			FileReader FR = new FileReader(FILE);
			BufferedReader BR = new BufferedReader(FR);

			StringBuilder FILE_CONTENTS = new StringBuilder();

			String FILE_CONTENTS_TEMP;
			while ((FILE_CONTENTS_TEMP = BR.readLine()) != null) {
				FILE_CONTENTS.append(FILE_CONTENTS_TEMP);
			}
			BR.close();

			return FILE_CONTENTS.toString();
		}catch (Exception EX){
			System.err.println("FILE_LOAD err");
			EX.printStackTrace();
			return null;
		}
	}
}
