package su.rumishistem.rumiabot.aichan.API;

import java.io.File;
import java.io.FileOutputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.UUID;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import su.rumishistem.rumi_java_lib.FormData;
import su.rumishistem.rumi_java_lib.SmartHTTP.HTTP_REQUEST;
import su.rumishistem.rumi_java_lib.SmartHTTP.HTTP_RESULT;
import su.rumishistem.rumi_java_lib.SmartHTTP.Type.EndpointFunction;
import su.rumishistem.rumiabot.aichan.MisskeyAPIModoki;

public class CreateFileAPI implements EndpointFunction {
	@Override
	public HTTP_RESULT Run(HTTP_REQUEST r) throws Exception {
		try {
			Matcher MTC = Pattern.compile("boundary=(.*);?").matcher(r.GetEVENT().getHEADER_DATA().get("CONTENT-TYPE"));
			if (MTC.find()) {
				String Boundary = MTC.group(1);
				FormData FD = new FormData(r.GetEVENT().getPOST_DATA_BIN(), Boundary.getBytes());
				String ID = UUID.randomUUID().toString();

				//ファイルに書き込む
				Files.createFile(Path.of("/tmp/" + ID));
				FileOutputStream FOS = new FileOutputStream(new File("/tmp/" + ID));
				FOS.write(FD.GetFile("file"));
				FOS.flush();
				FOS.close();

				return new HTTP_RESULT(200, ("{\"id\":\"" + ID + "\"}").getBytes(), MisskeyAPIModoki.JSONMime);
			} else {
				return new HTTP_RESULT(500, "{}".getBytes(), MisskeyAPIModoki.JSONMime);
			}
		} catch (Exception EX) {
			EX.printStackTrace();
			return new HTTP_RESULT(500, "{}".getBytes(), MisskeyAPIModoki.JSONMime);
		}
	}
}
