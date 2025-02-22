package su.rumishistem.rumiabot.System.TYPE;

import static su.rumishistem.rumiabot.System.Main.MisskeyBOT;
import static su.rumishistem.rumiabot.System.Main.DISCORD_BOT;
import net.dv8tion.jda.api.entities.Member;
import su.rumishistem.rumi_java_lib.Misskey.TYPE.User;

public class MessageUser {
	private Member DiscordMember;
	private User MisskeyUser;

	public MessageUser(Member DiscordMember, User MisskeyUser) {
		this.DiscordMember = DiscordMember;
		this.MisskeyUser = MisskeyUser;
	}

	public boolean isMe() {
		if (DiscordMember != null) {
			//Discord
			if (DiscordMember.getUser().getId().equals(DISCORD_BOT.getSelfUser().getId())) {
				return true;
			} else {
				return false;
			}
		} else {
			//Misskey
			//TODO:Misskeyのも実装しろ
			return false;
		}
	}

	public String GetName() {
		if (DiscordMember != null) {
			//Discord
			if (DiscordMember.getNickname() != null) {
				//ニックネーム
				return DiscordMember.getNickname();
			} else {
				//グローバル名
				return DiscordMember.getUser().getGlobalName();
			}
		} else {
			//Misskey
			return MisskeyUser.getNAME();
		}
	}

	public String GetIconURL() {
		if (DiscordMember != null) {
			//Discord
			return DiscordMember.getAvatarUrl();
		} else {
			//Misskey
			return MisskeyUser.getICON_URL();
		}
	}
}
