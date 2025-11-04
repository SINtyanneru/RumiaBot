package su.rumishistem.rumiabot.Gutenmorgen.Type;

public class WetherData {
	private double now_temprature = 0;
	private double min_temprature = 0;
	private double max_temprature = 0;
	private WetherCode wether;

	public WetherData(double now_temp, double min_temp, double max_temp, WetherCode wether) {
		this.now_temprature = now_temp;
		this.min_temprature = min_temp;
		this.max_temprature = max_temp;
		this.wether = wether;
	}

	public double get_now_temp() {
		return now_temprature;
	}

	public double get_max_temp() {
		return max_temprature;
	}

	public double get_min_temp() {
		return min_temprature;
	}

	public WetherCode get_wether() {
		return wether;
	}
}
