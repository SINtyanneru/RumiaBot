package su.rumishistem.rumiabot.System.Module;

import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.User;

public class NameParse {
	private User U;
	private Member M;

	public NameParse(User U) {
		this.U = U;
	}

	public NameParse(Member M) {
		this.M = M;
		this.U = M.getUser();
	}


	public String getDisplayName() {
		if (M != null && M.getNickname() != null) {
			return M.getNickname();
		} else if (U.getGlobalName() != null) {
			return U.getGlobalName();
		} else {
			return U.getName();
		}
	}
}
