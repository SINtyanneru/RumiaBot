package su.rumishistem.rumiabot.DiscordWelcomeFuckyouMessage;

import java.sql.SQLException;
import java.sql.SQLIntegrityConstraintViolationException;
import java.util.function.Consumer;

import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.entities.Invite;
import net.dv8tion.jda.api.events.guild.member.GuildMemberJoinEvent;
import net.dv8tion.jda.api.events.guild.member.GuildMemberRemoveEvent;
import su.rumishistem.rumi_java_lib.SQL;

public class JoinLog {
	public static void join(GuildMemberJoinEvent e, Consumer<Invite> callback) throws SQLException, InterruptedException {
		//記録
		try {
			SQL.UP_RUN("INSERT INTO `DISCORD_USER_JOIN` (`GID`, `UID`, `DATE`, `INVITE_CODE`, `INVITE_UID`) VALUES (?, ?, NOW(), NULL, NULL)", new Object[] {
				e.getGuild().getId(),
				e.getUser().getId()
			});
		} catch (SQLIntegrityConstraintViolationException ex) {
			//もみ消す
		} catch (SQLException ex) {
			ex.printStackTrace();
		}

		if (!e.getGuild().getSelfMember().hasPermission(Permission.MANAGE_SERVER)) {
			callback.accept(null);
			return;
		}

		e.getGuild().retrieveInvites().queue(InvList->{
			Invite UseInvCode = null;

			//使われた招待コードを探す
			for (Invite Inv:InvList) {
				int OldUse = Main.invite_table.get(e.getGuild().getId()).get(Inv.getCode());
				int NewUse = Inv.getUses();
				if (NewUse > OldUse) {
					UseInvCode = Inv;
					break;
				}
			}

			//招待コード同期
			Main.invite_sync(e.getGuild());

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
						e.getGuild().getId(),
						e.getUser().getId()
					});

					callback.accept(UseInvCode);
				} catch (SQLException ex) {
					ex.printStackTrace();
				}
			}
		});
	}

	public static void leave(GuildMemberRemoveEvent e) throws SQLException {
		//削除
		SQL.UP_RUN("DELETE FROM `DISCORD_USER_JOIN` WHERE `GID` = ? AND `UID` = ?;", new Object[] {
			e.getGuild().getId(),
			e.getUser().getId()
		});
	}
}
