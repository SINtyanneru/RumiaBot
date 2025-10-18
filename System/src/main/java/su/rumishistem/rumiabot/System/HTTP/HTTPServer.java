package su.rumishistem.rumiabot.System.HTTP;

import su.rumishistem.rumi_java_lib.SmartHTTP.SmartHTTP;

public class HTTPServer {
	private SmartHTTP sh;

	public HTTPServer(int port) {
		sh = new SmartHTTP(port);
	}

	public SmartHTTP get() {
		return sh;
	}

	public void start() throws InterruptedException {
		sh.Start();
	}
}
