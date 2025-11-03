package su.rumishistem.rumiabot.MakeItQuote;

import static su.rumishistem.rumiabot.System.Main.get_discord_bot;

import java.awt.FontFormatException;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;

import javax.imageio.ImageIO;

import net.dv8tion.jda.api.components.actionrow.ActionRow;
import net.dv8tion.jda.api.components.selections.SelectOption;
import net.dv8tion.jda.api.components.selections.StringSelectMenu;
import net.dv8tion.jda.api.components.selections.StringSelectMenu.Builder;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.MessageType;
import net.dv8tion.jda.api.entities.User;
import net.dv8tion.jda.api.entities.channel.concrete.TextChannel;
import net.dv8tion.jda.api.events.interaction.command.MessageContextInteractionEvent;
import net.dv8tion.jda.api.events.interaction.component.StringSelectInteractionEvent;
import net.dv8tion.jda.api.utils.FileUpload;
import net.dv8tion.jda.api.utils.messages.MessageEditBuilder;
import su.rumishistem.rumiabot.System.Module.NameParse;

public class DiscordMiq {
	public static void RunMessageContext(MessageContextInteractionEvent Interaction) throws MalformedURLException, IOException, FontFormatException {
		Message MSG = Interaction.getTarget();
		MakeItQuote miq = new MakeItQuote();

		if (!MSG.isWebhookMessage()) {
			Member MEM = MSG.getMember();
			User U = MEM.getUser();
			BufferedImage IconImage = ImageIO.read(ResolveIconURL(U));

			miq.setUserID(U.getName());
			miq.setUserName(new NameParse(MEM).getDisplayName());
			miq.setIcon(IconImage);
			miq.setText(MSG.getContentRaw());
		} else {
			miq.setUserID(MSG.getAuthor().getName());
			miq.setUserName(MSG.getAuthor().getName());
			miq.setIcon(ImageIO.read(ResolveIconURL(MSG.getAuthor())));
			miq.setText(MSG.getContentRaw());
		}

		//ファイルをアップロード
		File F = miq.Gen();
		FileUpload FU = FileUpload.fromData(F);
		F.delete();

		//応答
		Interaction.getHook().sendFiles(FU).setComponents(ActionRow.of(GenIconColorSelect(MSG.getId(), MSG.getChannelId(), null))).queue();
	}

	public static void ChangeSetting(StringSelectInteractionEvent Interaction) throws MalformedURLException, IOException, FontFormatException {
		String ResolveData = Interaction.getComponentId().split("\\?")[1];
		String MessageID = ResolveData.split(":")[0];
		String ChannelID = ResolveData.split(":")[1];
		GrayScaleConvert.ConvertMode IconColorMode = null;

		TextChannel Channel = get_discord_bot().get_primary_bot().getTextChannelById(ChannelID);
		Message MSG = null;
		if (Channel != null) {
			MSG = Channel.retrieveMessageById(MessageID).complete();
			if (MSG == null) {
				Interaction.getHook().editOriginal("メッセージ「" + MessageID + "」を見つけれなかった").queue();;
				return;
			}
		} else {
			Interaction.getHook().editOriginal("テキストチャンネル「" + ChannelID + "」を見つけれなかった").queue();;
			return;
		}

		//アイコンの色変換モード
		if (!Interaction.getValues().get(0).equals("FULL")) {
			for (GrayScaleConvert.ConvertMode Mode:GrayScaleConvert.ConvertMode.values()) {
				if (Mode.name().equals(Interaction.getValues().get(0))) {
					IconColorMode = Mode;
					break;
				}
			}
		}

		//アイコン
		BufferedImage IconImage = ImageIO.read(ResolveIconURL(MSG.getAuthor()));
		if (IconColorMode != null) {
			IconImage = GrayScaleConvert.toGrayScale(IconImage, IconColorMode);
		}

		MakeItQuote miq = new MakeItQuote();
		miq.setUserID(MSG.getAuthor().getName());
		miq.setUserName(new NameParse(MSG.getMember()).getDisplayName());
		miq.setIcon(IconImage);
		miq.setText(MSG.getContentRaw());

		//ファイルをアップロード
		File F = miq.Gen();
		FileUpload FU = FileUpload.fromData(F);
		F.delete();

		//元のメッセージを編集する
		MessageEditBuilder MEB = new MessageEditBuilder();
		MEB.setFiles(FU);
		MEB.setComponents(ActionRow.of(GenIconColorSelect(MSG.getId(), MSG.getChannelId(), IconColorMode)));
		Interaction.getMessage().editMessage(MEB.build()).queue();

		//インタラクションのアレを消し飛ばす
		Interaction.getHook().deleteOriginal().queue();
	}

	//ユーザーのアイコンを持ってくる
	private static URL ResolveIconURL(User U) throws MalformedURLException {
		String IconURL = U.getEffectiveAvatarUrl() + "?size=4096";
		return new URL(IconURL);
	}

	//アイコンの色設定を生成する
	private static StringSelectMenu GenIconColorSelect(String MessageID, String ChannelID, GrayScaleConvert.ConvertMode Mode) {
		Builder B = StringSelectMenu.create("miq-icon-color-select?" + MessageID + ":" + ChannelID);

		if (Mode == null) {
			B.addOptions(SelectOption.of("フルカラー", "FULL").withDefault(true));
		} else {
			B.addOption("フルカラー", "FULL");
		}

		for (GrayScaleConvert.ConvertMode Row:GrayScaleConvert.ConvertMode.values()) {
			String Name = Row.name();

			switch (Row) {
				case NTSC_601:
					Name = "NTSC601";
					break;
				case BT_709:
					Name = "BT.709";
					break;
				case BT_2020:
					Name = "BT.2020";
					break;
				case AVERAGE:
					Name = "加重無し";
					break;
				case WEIGHTED_ROOT:
					Name = "加重レート平均";
					break;
			}

			SelectOption Option = SelectOption.of(Name, Row.name());
			if (Mode != null) {
				if (Row == Mode) {
					Option = Option.withDefault(true);
				}
			}
			B.addOptions(Option);
		}

		StringSelectMenu IconColorSelect = B.build();

		return IconColorSelect;
	}
}
