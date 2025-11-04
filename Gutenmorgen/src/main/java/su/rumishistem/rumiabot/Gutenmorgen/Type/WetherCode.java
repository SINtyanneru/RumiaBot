package su.rumishistem.rumiabot.Gutenmorgen.Type;

public enum WetherCode {
	快晴,
	晴れ,
	一部曇り,
	曇り,
	霧,
	霧雨,
	雨,
	雪,
	俄雨,
	雹,
	雷雨,
	None;

	public static WetherCode get_from_code(int code) {
		switch (code) {
			case 0:
				return WetherCode.快晴;
			case 1:
				return WetherCode.晴れ;
			case 2:
				return WetherCode.一部曇り;
			case 3:
				return WetherCode.曇り;
			case 49:
				return WetherCode.霧;
			case 59:
				return WetherCode.霧雨;
			case 69:
				return WetherCode.雨;
			case 79:
				return WetherCode.雪;
			case 84:
				return WetherCode.俄雨;
			case 94:
				return WetherCode.雹;
			case 99:
				return WetherCode.雷雨;

			default:
				return WetherCode.None;
		}
	}
}
