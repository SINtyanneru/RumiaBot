package su.rumishistem.rumiabot.System.TYPE;

public class ThreadPoolStatus {
	public int active = 0;
	public int max = 0;

	public ThreadPoolStatus(int active, int max) {
		this.active = active;
		this.max = max;
	}
}
