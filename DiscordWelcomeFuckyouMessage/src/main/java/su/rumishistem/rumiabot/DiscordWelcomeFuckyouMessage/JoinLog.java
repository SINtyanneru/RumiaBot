package su.rumishistem.rumiabot.DiscordWelcomeFuckyouMessage;

import java.sql.SQLException;
import java.util.concurrent.CountDownLatch;

import net.dv8tion.jda.api.entities.Invite;
import net.dv8tion.jda.api.events.guild.member.GuildMemberJoinEvent;
import net.dv8tion.jda.api.events.guild.member.GuildMemberRemoveEvent;
import su.rumishistem.rumi_java_lib.SQL;
import su.rumishistem.rumiabot.System.Discord.DiscordBOT;
import su.rumishistem.rumiabot.System.TYPE.DiscordEvent;

public class JoinLog {
	public static Invite join(DiscordEvent e) throws SQLException, InterruptedException {
		Invite[] invite_code = {null};
		GuildMemberJoinEvent JE = (GuildMemberJoinEvent) e.GetEventClass();

		//記録
		try {
			SQL.UP_RUN("INSERT INTO `DISCORD_USER_JOIN` (`GID`, `UID`, `DATE`, `INVITE_CODE`, `INVITE_UID`) VALUES (?, ?, NOW(), NULL, NULL)", new Object[] {
				e.GetGuild().getId(),
				JE.getUser().getId()
			});
		} catch (SQLException ex) {
			ex.printStackTrace();
		}

		CountDownLatch cdl = new CountDownLatch(1);

		e.GetGuild().retrieveInvites().queue(InvList->{
			Invite UseInvCode = null;

			//使われた招待コードを探す
			for (Invite Inv:InvList) {
				int OldUse = DiscordBOT.InviteTable.get(e.GetGuild().getId()).get(Inv.getCode());
				int NewUse = Inv.getUses();
				if (NewUse > OldUse) {
					UseInvCode = Inv;
					break;
				}
			}

			//招待コード同期
			DiscordBOT.GetGuildInvite(e.GetGuild());

			//使った招待コードが判明したら処理
			if (UseInvCode != null) {
				try {
					//SQL書き換え
					SQL.UP_RUN("""
							UPDATE
								`DISCORD_USER_JOIN`
							SET
								`INVITE_CODE` = ?,
								`INVITE_UID` = ?
							WHERE
								`DISCORD_USER_JOIN`.`GID` = ?
							AND
								`DISCORD_USER_JOIN`.`UID` = ?;
						""", new Object[] {
						UseInvCode.getCode(),
						UseInvCode.getInviter().getId(),
						e.GetGuild().getId(),
						JE.getUser().getId()
					});

					invite_code[0] = UseInvCode;
				} catch (Exception EX) {
					EX.printStackTrace();
				}
			}

			cdl.countDown();
		});

		cdl.await();
		return invite_code[0];
	}

	public static void leave(DiscordEvent e) throws SQLException {
		GuildMemberRemoveEvent RE = (GuildMemberRemoveEvent) e.GetEventClass();

		//削除
		SQL.UP_RUN("DELETE FROM `DISCORD_USER_JOIN` WHERE `GID` = ? AND `UID` = ?;", new Object[] {
			e.GetGuild().getId(),
			RE.getUser().getId()
		});
	}
}
