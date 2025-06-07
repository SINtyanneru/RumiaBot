package su.rumishistem.rumiabot.Joke;

import java.net.MalformedURLException;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.UUID;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.events.interaction.ModalInteractionEvent;
import net.dv8tion.jda.api.events.interaction.command.MessageContextInteractionEvent;
import net.dv8tion.jda.api.events.interaction.component.EntitySelectInteractionEvent;
import net.dv8tion.jda.api.interactions.components.ActionRow;
import net.dv8tion.jda.api.interactions.components.selections.EntitySelectMenu;
import net.dv8tion.jda.api.interactions.components.selections.EntitySelectMenu.SelectTarget;
import net.dv8tion.jda.api.interactions.components.text.TextInput;
import net.dv8tion.jda.api.interactions.components.text.TextInputStyle;
import net.dv8tion.jda.api.interactions.modals.Modal;
import su.rumishistem.rumi_java_lib.FETCH;
import su.rumishistem.rumi_java_lib.FETCH_RESULT;
import su.rumishistem.rumiabot.System.Discord.MODULE.DiscordWebHook;
import su.rumishistem.rumiabot.System.Discord.MODULE.NameParse;

public class reply_cam {
	private static HashMap<String, Message> ReplyCam_Message = new HashMap<String, Message>();
	private static HashMap<String, String> ReplyCam_UserName = new HashMap<String, String>();
	private static HashMap<String, String> ReplyCam_UserIconURL = new HashMap<String, String>();
	private static HashMap<String, String> ReplyCam_Text = new HashMap<String, String>();

	public static void RunContextmenu(MessageContextInteractionEvent Interaction) {
		if (Interaction.getName().equals("reply-cam")) {
			String ID = UUID.randomUUID().toString();
			ReplyCam_Message.put(ID, Interaction.getTarget());
			Interaction.reply("ユーザーを指定しろ").addActionRow(EntitySelectMenu.create("reply-cam_user?" + ID, SelectTarget.USER).build()).setEphemeral(true).queue();
		}
	}

	public static void ReturnUserSelect(EntitySelectInteractionEvent Interaction) {
		if (Interaction.getComponentId().startsWith("reply-cam_user")) {
			String ID = Interaction.getComponentId().split("\\?")[1];
			if (ReplyCam_Message.get(ID) == null) return;

			ReplyCam_UserName.put(ID, new NameParse(Interaction.getMentions().getMembers().get(0)).getDisplayName());
			if (Interaction.getMentions().getUsers().get(0).getAvatarUrl() != null) {
				ReplyCam_UserIconURL.put(ID, Interaction.getMentions().getUsers().get(0).getAvatarUrl());
			} else {
				ReplyCam_UserIconURL.put(ID, Interaction.getMentions().getUsers().get(0).getDefaultAvatarUrl());
			}

			TextInput Text = TextInput.create("text", "本文", TextInputStyle.SHORT).setPlaceholder("うんこ").setRequired(true).build();
			Modal M = Modal.create("reply-cam-modal?" + ID, "cam").addComponents(ActionRow.of(Text)).build();
			Interaction.replyModal(M).queue();
		}
	}

	public static void ReturnModal(ModalInteractionEvent Interaction) throws InterruptedException, JsonProcessingException, MalformedURLException {
		if (Interaction.getModalId().startsWith("reply-cam-modal")) {
			String ID = Interaction.getModalId().split("\\?")[1];
			if (ReplyCam_Message.get(ID) == null) return;
			if (ReplyCam_UserName.get(ID) == null) return;
			if (ReplyCam_UserIconURL.get(ID) == null) return;

			ReplyCam_Text.put(ID, Interaction.getValue("text").getAsString());

			DiscordWebHook WH = new DiscordWebHook(ReplyCam_Message.get(ID).getChannel().asTextChannel());
			FETCH Ajax = new FETCH(WH.getURL());
			Ajax.SetHEADER("Content-Type", "application/json");

			LinkedHashMap<String, Object> PostBody = new LinkedHashMap<String, Object>();
			PostBody.put("username", ReplyCam_UserName.get(ID));
			PostBody.put("avatar_url", ReplyCam_UserIconURL.get(ID));
			PostBody.put("content", ReplyCam_Text.get(ID));

			//リプライ
			HashMap<String, Object> ReferenceHM = new HashMap<String, Object>();
			ReferenceHM.put("message_id", ReplyCam_Message.get(ID).getId());
			ReferenceHM.put("fail_if_not_exists", false);
			PostBody.put("message_reference", ReferenceHM);
			
			System.out.println(new ObjectMapper().writeValueAsString(PostBody));

			FETCH_RESULT AjaxResult = Ajax.POST(new ObjectMapper().writeValueAsString(PostBody).getBytes());
			System.out.println(AjaxResult.GetString());

			Interaction.reply("Done").setEphemeral(true).queue();
		}
	}
}
