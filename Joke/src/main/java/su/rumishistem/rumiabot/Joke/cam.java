package su.rumishistem.rumiabot.Joke;

import java.io.File;
import java.util.UUID;
import java.util.concurrent.CountDownLatch;

import net.dv8tion.jda.api.entities.*;
import net.dv8tion.jda.api.entities.Message.Attachment;
import net.dv8tion.jda.api.requests.restaction.WebhookMessageCreateAction;
import net.dv8tion.jda.api.utils.FileUpload;
import su.rumishistem.rumiabot.System.Discord.MODULE.*;
import su.rumishistem.rumiabot.System.TYPE.CommandInteraction;

public class cam {
	public static void Command(CommandInteraction CI) throws Exception {
		Member M = CI.GetDiscordInteraction().getOption("user").getAsMember();
		String Text = CI.GetDiscordInteraction().getOption("text").getAsString();

		DiscordWebHook WH = new DiscordWebHook(CI.GetDiscordInteraction().getChannel().asTextChannel());

		WebhookMessageCreateAction<Message> MSG = WH.Send().sendMessage(Text);
		MSG.setUsername(new NameParse(M).getDisplayName());
		MSG.setAvatarUrl(M.getUser().getAvatarUrl());

		if (CI.GetDiscordInteraction().getOption("file") != null) {
			Attachment AM = CI.GetDiscordInteraction().getOption("file").getAsAttachment();
			CountDownLatch CDL = new CountDownLatch(1);

			AM.getProxy().downloadToFile(new File("/tmp/" + UUID.randomUUID().toString() + "." + AM.getFileExtension()))
				.thenAccept(F->{
					MSG.addFiles(FileUpload.fromData(F));
					CDL.countDown();
				}).exceptionally(EX->{
					CDL.countDown();
					return null;
				});

			CDL.await();
		}

		MSG.queue();

		CI.Reply("Done");
	}
}
