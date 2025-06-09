package su.rumishistem.rumiabot.aichan.API;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import su.rumishistem.rumi_java_lib.SmartHTTP.HTTP_REQUEST;
import su.rumishistem.rumi_java_lib.SmartHTTP.HTTP_RESULT;
import su.rumishistem.rumi_java_lib.SmartHTTP.Type.EndpointFunction;
import su.rumishistem.rumiabot.aichan.MisskeyAPIModoki;
import su.rumishistem.rumiabot.aichan.SERVICE.CreateNote;

public class CreateNoteAPI implements EndpointFunction {
	@Override
	public HTTP_RESULT Run(HTTP_REQUEST r) throws Exception {
		try {
			JsonNode POST_BODY = new ObjectMapper().readTree(r.GetEVENT().getPOST_DATA());
			String ReplyID = null;
			List<File> FileList = new ArrayList<File>();

			if (POST_BODY.get("replyId") != null) {
				ReplyID = POST_BODY.get("replyId").asText();
			}

			if (POST_BODY.get("fileIds") != null) {
				for (int I = 0; I < POST_BODY.get("fileIds").size(); I++) {
					JsonNode FN = POST_BODY.get("fileIds").get(I);
					File F = new File("/tmp/" + FN.asText());
					if (F.exists()) {
						FileList.add(F);
					}
				}
			}

			return new HTTP_RESULT(200, CreateNote.Create(POST_BODY.get("text").asText(), ReplyID, FileList).getBytes(), MisskeyAPIModoki.JSONMime);
		} catch (Exception EX) {
			EX.printStackTrace();
			return new HTTP_RESULT(500, "{}".getBytes(), MisskeyAPIModoki.JSONMime);
		}
	}
}
