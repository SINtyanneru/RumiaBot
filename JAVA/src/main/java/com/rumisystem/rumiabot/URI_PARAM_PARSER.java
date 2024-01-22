package com.rumisystem.rumiabot;

import java.util.HashMap;

public class URI_PARAM_PARSER {
	public HashMap<String, String> URI_PARAM_DATA = new HashMap();

	//解析する
	public URI_PARAM_PARSER(String URI){
		if(URI.split("\\?").length != 0){
			String[] URI_PARAM_SPLIT = URI.split("\\?")[1].split("&");
			for(String PARAM:URI_PARAM_SPLIT){
				String KEY = PARAM.split("=")[0];
				String VAL = PARAM.split("=")[1];
				URI_PARAM_DATA.put(KEY, VAL);
			}
		}else {
			System.err.println("URIパラメーターがありません");
		}
	}

	//解析結果を返す
	public HashMap<String, String> PARSE(){
		return URI_PARAM_DATA;
	}
}
