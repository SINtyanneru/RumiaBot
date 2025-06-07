package su.rumishistem.rumiabot.Joke;

import static su.rumishistem.rumiabot.System.FunctionModuleLoader.AddCommand;
import java.io.File;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.UUID;
import java.util.concurrent.CountDownLatch;

import com.fasterxml.jackson.databind.ObjectMapper;

import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.User;
import net.dv8tion.jda.api.entities.Message.Attachment;
import net.dv8tion.jda.api.events.interaction.ModalInteractionEvent;
import net.dv8tion.jda.api.events.interaction.command.MessageContextInteractionEvent;
import net.dv8tion.jda.api.events.interaction.component.ButtonInteractionEvent;
import net.dv8tion.jda.api.events.interaction.component.EntitySelectInteractionEvent;
import net.dv8tion.jda.api.interactions.commands.Command.Type;
import net.dv8tion.jda.api.interactions.commands.build.Commands;
import net.dv8tion.jda.api.interactions.components.ActionRow;
import net.dv8tion.jda.api.interactions.components.buttons.Button;
import net.dv8tion.jda.api.interactions.components.selections.EntitySelectMenu;
import net.dv8tion.jda.api.interactions.components.selections.EntitySelectMenu.SelectTarget;
import net.dv8tion.jda.api.interactions.components.text.TextInput;
import net.dv8tion.jda.api.interactions.components.text.TextInputStyle;
import net.dv8tion.jda.api.interactions.modals.Modal;
import net.dv8tion.jda.api.requests.restaction.WebhookMessageCreateAction;
import net.dv8tion.jda.api.utils.FileUpload;
import su.rumishistem.rumi_java_lib.FETCH;
import su.rumishistem.rumi_java_lib.FETCH_RESULT;
import su.rumishistem.rumi_java_lib.LOG_PRINT.LOG_TYPE;
import su.rumishistem.rumiabot.System.Discord.MODULE.DiscordWebHook;
import su.rumishistem.rumiabot.System.Discord.MODULE.NameParse;
import su.rumishistem.rumiabot.System.TYPE.CommandData;
import su.rumishistem.rumiabot.System.TYPE.CommandInteraction;
import su.rumishistem.rumiabot.System.TYPE.CommandOption;
import su.rumishistem.rumiabot.System.TYPE.CommandOptionType;
import su.rumishistem.rumiabot.System.TYPE.DiscordEvent;
import su.rumishistem.rumiabot.System.TYPE.DiscordFunction;
import su.rumishistem.rumiabot.System.TYPE.FunctionClass;
import su.rumishistem.rumiabot.System.TYPE.ReceiveMessageEvent;
import su.rumishistem.rumiabot.System.TYPE.SourceType;
import su.rumishistem.rumiabot.System.TYPE.DiscordEvent.EventType;

public class Main implements FunctionClass {
	private static final String FUNCTION_NAME = "よけ";
	private static final String FUNCTION_VERSION = "1.0";
	private static final String FUNCTION_AUTOR = "Rumisan";

	private static HashMap<String, Message> ReplyCam_Message = new HashMap<String, Message>();
	private static HashMap<String, String> ReplyCam_UserName = new HashMap<String, String>();
	private static HashMap<String, String> ReplyCam_UserIconURL = new HashMap<String, String>();
	private static HashMap<String, String> ReplyCam_Text = new HashMap<String, String>();

	public static boolean Enabled = false;

	@Override
	public String FUNCTION_NAME() {
		return FUNCTION_NAME;
	}
	@Override
	public String FUNCTION_VERSION() {
		return FUNCTION_VERSION;
	}
	@Override
	public String FUNCTION_AUTOR() {
		return FUNCTION_AUTOR;
	}


	@Override
	public void Init() {
		AddCommand(new CommandData("cam", new CommandOption[] {
			new CommandOption("user", CommandOptionType.User, null, true),
			new CommandOption("text", CommandOptionType.String, null, true),
			new CommandOption("file", CommandOptionType.File, null, false)
		}, true));
	}

	@Override
	public void DiscordEventReceive(DiscordEvent e) throws Exception {
		if (e.GetType() == EventType.BOTReady) {
			//コンテキストメニュー
			su.rumishistem.rumiabot.System.Main.DISCORD_BOT.upsertCommand(
				Commands.context(Type.MESSAGE, "reply-cam")
			).queue();
			su.rumishistem.rumi_java_lib.LOG_PRINT.Main.LOG(LOG_TYPE.OK, "cam用のコンテキストメニューを登録した");
		}
	}

	@Override
	public void ReceiveMessage(ReceiveMessageEvent e) {
		if (e.GetSource() == SourceType.Discord) {
			//設定の有効化をチェック
			if (!e.GetMessage().CheckDiscordGuildFunctionEnabled(DiscordFunction.Joke)) {
				return;
			}

			//そういうのよくないよ
			SouiunoYokunaiyo.Main(e);
			//きも
			Kimo.Main(e);
			//住所
			Zhuusho.Main(e);
			//うんこ
			Unko.Main(e);
		}
	}

	@Override
	public boolean GetAllowCommand(String Name) {
		return (
			Name.equals("cam") ||

			Name.equals("Message:reply-cam") ||
			Name.equals("EntitySelect:reply-cam_user") ||
			Name.equals("Modal:reply-cam-modal")
		);
	}

	@Override
	public void RunCommand(CommandInteraction CI) throws Exception {
		if (CI.GetSource() != SourceType.Discord) {
			CI.Reply("Discordのみで使用可能です");
			return;
		}

		cam.Command(CI);
	}

	@Override
	public void RunMessageContext(MessageContextInteractionEvent Interaction) throws Exception {
		if (Interaction.getName().equals("reply-cam")) {
			String ID = UUID.randomUUID().toString();
			ReplyCam_Message.put(ID, Interaction.getTarget());
			Interaction.reply("ユーザーを指定しろ").addActionRow(EntitySelectMenu.create("reply-cam_user?" + ID, SelectTarget.USER).build()).setEphemeral(true).queue();
		}
	}

	@Override
	public void ReturnEntitySelect(EntitySelectInteractionEvent Interaction) throws Exception {
		if (Interaction.getComponentId().startsWith("reply-cam_user")) {
			String ID = Interaction.getComponentId().split("\\?")[1];
			if (ReplyCam_Message.get(ID) == null) return;

			ReplyCam_UserName.put(ID, new NameParse(Interaction.getMentions().getMembers().get(0)).getDisplayName());
			if (Interaction.getMentions().getMembers().get(0).getAvatarUrl() != null) {
				ReplyCam_UserIconURL.put(ID, Interaction.getMentions().getMembers().get(0).getAvatarUrl());
			} else {
				ReplyCam_UserIconURL.put(ID, Interaction.getMentions().getMembers().get(0).getDefaultAvatarUrl());
			}

			TextInput Text = TextInput.create("text", "本文", TextInputStyle.SHORT).setPlaceholder("うんこ").setRequired(true).build();
			Modal M = Modal.create("reply-cam-modal?" + ID, "cam").addComponents(ActionRow.of(Text)).build();
			Interaction.replyModal(M).queue();
		}
	}

	@Override
	public void ReturnModal(ModalInteractionEvent Interaction) throws Exception {
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
