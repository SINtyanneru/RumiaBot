package su.rumishistem.rumiabot.Joke;

import net.dv8tion.jda.api.entities.*;
import net.dv8tion.jda.api.requests.restaction.WebhookMessageCreateAction;
import su.rumishistem.rumiabot.System.Module.DiscordWebHook;
import su.rumishistem.rumiabot.System.Module.NameParse;
import su.rumishistem.rumiabot.System.Type.CommandInteraction;

public class cam {
	public static void Command(CommandInteraction e) throws Exception {
		Member M = (Member) e.get_option("user");
		String Text = e.get_option_as_string("text");
		Text = Text.replace("@", "[AD]");

		DiscordWebHook WH = new DiscordWebHook(e.get_discprd_event().getChannel().asTextChannel());

		WebhookMessageCreateAction<Message> MSG = WH.Send().sendMessage(Text);
		MSG.setUsername(new NameParse(M).getDisplayName());
		MSG.setAvatarUrl(M.getUser().getAvatarUrl());

		/*
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
		}*/

		MSG.queue();

		e.reply("Done");
	}
}
