package su.rumishistem.rumiabot.VerifyPanelFunction;

import java.awt.Color;
import java.io.IOException;
import java.sql.SQLException;
import java.util.UUID;

import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.entities.Role;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.interactions.components.buttons.Button;
import su.rumishistem.rumi_java_lib.ArrayNode;
import su.rumishistem.rumi_java_lib.SQL;

public class CreatePanel {
	public static void Create(SlashCommandInteractionEvent INTERACTION) throws IOException, SQLException {
		if(INTERACTION.getMember().getPermissions().contains(Permission.MANAGE_ROLES)){
			String ID = UUID.randomUUID().toString();
			Role ROLE = INTERACTION.getOption("role").getAsRole();
			String GID = INTERACTION.getGuild().getId();
			String CID = INTERACTION.getChannel().getId();
			//そのロールをさわれるか(そのロールをロールを付与出来るか & 自分の一番最高権限でそのロールを付与できるか)
			if(INTERACTION.getGuild().getSelfMember().canInteract(ROLE) && INTERACTION.getGuild().getSelfMember().getRoles().get(0).canInteract(ROLE)){
				//すでにパネルが存在するかチェックするやつ
				ArrayNode RESULT = SQL.RUN("SELECT * FROM `VERIFY_PANEL` WHERE `GID` = ? AND `CID` = ?;", new Object[] {GID, CID});
				if (RESULT.asArrayList().size() == 0) {
					//まだ無いので作成する
					SQL.UP_RUN("INSERT INTO `VERIFY_PANEL` (`ID`, `ROLE`, `GID`, `CID`) VALUES (?, ?, ?, ?);", new Object[] {
						ID,
						ROLE.getId(),
						GID,
						CID
					});

					EmbedBuilder EB = new EmbedBuilder();
					EB.setColor(Color.CYAN);
					EB.setTitle("認証 ATESTADO");

					INTERACTION.getChannel().sendMessageEmbeds(EB.build()).addActionRow(Button.primary("verify_panel?id=" + ID, "認証")).queue();
					INTERACTION.getHook().deleteOriginal().queue();
				} else {
					//存在するので、もっかい出す
					ArrayNode PANEL_DATA = RESULT.get(0);

					EmbedBuilder EB = new EmbedBuilder();
					EB.setColor(Color.CYAN);
					EB.setTitle("認証 ATESTADO");

					INTERACTION.getChannel().sendMessageEmbeds(EB.build()).addActionRow(Button.primary("verify_panel?id=" + PANEL_DATA.getData("ID").asString(), "認証")).queue();

					INTERACTION.getHook().editOriginal("すでにあるので、呼び出しました").queue();
				}
			} else {
				INTERACTION.getHook().editOriginal("ごめん、私そのロールを操作できないわ").queue();
			}
		} else {
			INTERACTION.getHook().editOriginal("お前は権限を持っていません").queue();
		}
	}
}
