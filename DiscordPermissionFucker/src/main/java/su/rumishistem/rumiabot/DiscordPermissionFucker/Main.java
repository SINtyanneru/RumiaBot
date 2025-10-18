package su.rumishistem.rumiabot.DiscordPermissionFucker;

import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Locale;
import java.util.UUID;

import com.fasterxml.jackson.databind.ObjectMapper;

import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.channel.concrete.TextChannel;
import su.rumishistem.rumi_java_lib.RESOURCE.RESOURCE_MANAGER;
import su.rumishistem.rumi_java_lib.SmartHTTP.HTTP_REQUEST;
import su.rumishistem.rumi_java_lib.SmartHTTP.HTTP_RESULT;
import su.rumishistem.rumi_java_lib.SmartHTTP.Type.EndpointEntrie.Method;
import su.rumishistem.rumi_java_lib.SmartHTTP.Type.EndpointFunction;
import su.rumishistem.rumiabot.System.CommandRegister;
import su.rumishistem.rumiabot.System.Type.CommandInteraction;
import su.rumishistem.rumiabot.System.Type.CommandOptionRegist;
import su.rumishistem.rumiabot.System.Type.FunctionClass;
import su.rumishistem.rumiabot.System.Type.OptionType;
import su.rumishistem.rumiabot.System.Type.RunCommand;

public class Main implements FunctionClass{
	private static HashMap<String, String> IDTable = new HashMap<String, String>();

	@Override
	public String function_name() {
		return "Discord権限Fucker";
	}
	@Override
	public String function_version() {
		return "1.0";
	}
	@Override
	public String function_author() {
		return "るみ";
	}

	@Override
	public void init() {
		CommandRegister.add_command("pf", new CommandOptionRegist[] {
			new CommandOptionRegist("id", OptionType.String, true)
		}, false, new RunCommand() {
			@Override
			public void run(CommandInteraction e) throws Exception {
				String ID = UUID.randomUUID().toString();
				String CID = e.get_option_as_string("id");

				TextChannel Ch = su.rumishistem.rumiabot.System.Main.get_discord_bot().get_primary_bot().getTextChannelById(CID);
				if (Ch == null) {
					e.reply("...?");
					return;
				}

				IDTable.put(ID, CID);

				e.reply("https://bot.rumi-room.net/pf?ID=" + ID);
			}
		});

		su.rumishistem.rumiabot.System.Main.get_http().get().SetRoute("/user/pf", new EndpointFunction() {
			@Override
			public HTTP_RESULT Run(HTTP_REQUEST r) throws Exception {
				String BODY = new String(new RESOURCE_MANAGER(Main.class).getResourceData("/index.html"));
				return new HTTP_RESULT(200, BODY.getBytes(), "text/html; charset=UTF-8");
			}
		});

		su.rumishistem.rumiabot.System.Main.get_http().get().SetRoute("/user/api/pf", Method.GET, new EndpointFunction() {
			@Override
			public HTTP_RESULT Run(HTTP_REQUEST r) throws Exception {
				if (r.GetEVENT().getURI_PARAM().get("ID") == null) {
					return new HTTP_RESULT(400, "{\"STATUS\": false}".getBytes(), "application/json; charset=UTF-8");
				}

				//Channel Ch = DISCORD_BOT.getChannelById(Channel.class, r.GetEVENT().getURI_PARAM().get("ID"));
				TextChannel Ch = su.rumishistem.rumiabot.System.Main.get_discord_bot().get_primary_bot().getTextChannelById(IDTable.get(r.GetEVENT().getURI_PARAM().get("ID")));

				if (Ch == null) {
					return new HTTP_RESULT(404, "{\"STATUS\": false}".getBytes(), "application/json; charset=UTF-8");
				}

				/*if (!(Ch.getType() == ChannelType.TEXT || Ch.getType() == ChannelType.GUILD_PUBLIC_THREAD || Ch.getType() == ChannelType.GUILD_PRIVATE_THREAD)) {
					return new HTTP_RESULT(404, "{\"STATUS\": false}".getBytes(), "application/json; charset=UTF-8");
				}*/

				LinkedHashMap<String, Object> ChannelData = new LinkedHashMap<String, Object>();
				ChannelData.put("ID", Ch.getId());
				ChannelData.put("NAME", Ch.getName());

				LinkedHashMap<String, Object> Return = new LinkedHashMap<String, Object>();
				Return.put("STATUS", true);
				Return.put("GUILD", null);
				Return.put("CHANNEL", ChannelData);
				return new HTTP_RESULT(200, new ObjectMapper().writeValueAsString(Return).getBytes(), "application/json; charset=UTF-8");
			}
		});
		
		su.rumishistem.rumiabot.System.Main.get_http().get().SetRoute("/user/api/pf_his", Method.GET, new EndpointFunction() {
			@Override
			public HTTP_RESULT Run(HTTP_REQUEST r) throws Exception {
				if (r.GetEVENT().getURI_PARAM().get("ID") == null) {
					return new HTTP_RESULT(400, "{\"STATUS\": false}".getBytes(), "application/json; charset=UTF-8");
				}

				TextChannel Ch = su.rumishistem.rumiabot.System.Main.get_discord_bot().get_primary_bot().getTextChannelById(IDTable.get(r.GetEVENT().getURI_PARAM().get("ID")));

				if (Ch == null) {
					return new HTTP_RESULT(404, "{\"STATUS\": false}".getBytes(), "application/json; charset=UTF-8");
				}

				List<LinkedHashMap<String, Object>> HistoryData = new ArrayList<LinkedHashMap<String,Object>>();
				for (Message M:Ch.getHistory().retrievePast(100).complete()) {
					LinkedHashMap<String, Object> Item = new LinkedHashMap<String, Object>();

					LinkedHashMap<String, Object> Message = new LinkedHashMap<String, Object>();
					Message.put("TEXT", M.getContentRaw());
					Message.put("DATE", M.getTimeCreated().format(DateTimeFormatter.ofPattern("yyyy-MM-dd h:m:s", Locale.JAPANESE)));
					Item.put("MESSAGE", Message);

					LinkedHashMap<String, Object> User = new LinkedHashMap<String, Object>();
					User.put("ID", M.getAuthor().getId());
					User.put("NAME", M.getAuthor().getName());
					User.put("ICON", M.getAuthor().getAvatarUrl());
					Item.put("USER", User);

					HistoryData.add(Item);
				}

				LinkedHashMap<String, Object> Return = new LinkedHashMap<String, Object>();
				Return.put("STATUS", true);
				Return.put("HISTORY", HistoryData);
				return new HTTP_RESULT(200, new ObjectMapper().writeValueAsString(Return).getBytes(), "application/json; charset=UTF-8");
			}
		});
	}
}
