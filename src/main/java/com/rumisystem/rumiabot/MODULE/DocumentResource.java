package com.rumisystem.rumiabot.MODULE;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

public class DocumentResource {
	public static String GetDocument(String PATH) throws IOException {
		PATH = PATH.replace("../", "");
		PATH = "/HTML" + PATH;

		if (ResourceExists(PATH)) {
			return GetResourceData(PATH);
		} else if (ResourceExists(PATH + "index.html")) {
			return GetResourceData(PATH + "index.html");
		} else {
			return null;
		}
	}

	private static boolean ResourceExists(String PATH) {
		if (DocumentResource.class.getResourceAsStream(PATH) != null) {
			return true;
		} else {
			return false;
		}
	}

	private static String GetResourceData(String PATH) throws IOException {
		InputStream IS = DocumentResource.class.getResourceAsStream(PATH);
		BufferedReader BR = new BufferedReader(new InputStreamReader(IS));

		StringBuilder SB = new StringBuilder();
		String LINE;
		while ((LINE = BR.readLine()) != null) {
			SB.append(LINE);
			SB.append("\n");
		}

		return SB.toString();
	}
}
