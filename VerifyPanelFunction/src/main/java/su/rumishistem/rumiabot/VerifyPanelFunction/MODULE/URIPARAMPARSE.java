package su.rumishistem.rumiabot.VerifyPanelFunction.MODULE;

import java.util.HashMap;

public class URIPARAMPARSE {
	public static HashMap<String, String> URI_PARAM_PARSE(String URI){
		HashMap<String, String> RESULT = new HashMap<>();

		String[] SPLIT_URI = URI.split("\\?")[1].split("&");

		for(int I = 0; I < SPLIT_URI.length; I++){
			String KEY = SPLIT_URI[I].split("=")[0];
			String VAL = SPLIT_URI[I].split("=")[1];

			RESULT.put(KEY, VAL);
		}

		return RESULT;
	}
}
