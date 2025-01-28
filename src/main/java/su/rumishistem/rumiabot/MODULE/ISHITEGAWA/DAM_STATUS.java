package su.rumishistem.rumiabot.MODULE.ISHITEGAWA;

import java.time.LocalDateTime;

public class DAM_STATUS {
	private LocalDateTime DATE = LocalDateTime.now();
	private float POSOS = 0;
	private float IN = 0;
	private float OUT = 0;

	public DAM_STATUS(LocalDateTime DATE, float POSOS, float IN, float OUT) {
		this.DATE = DATE;
		this.POSOS = POSOS;
		this.IN = IN;
		this.OUT = OUT;
	}

	public LocalDateTime getDATE() {
		return DATE;
	}

	public float getPOSOS() {
		return POSOS;
	}

	public float getIN() {
		return IN;
	}

	public float getOUT() {
		return OUT;
	}
}
