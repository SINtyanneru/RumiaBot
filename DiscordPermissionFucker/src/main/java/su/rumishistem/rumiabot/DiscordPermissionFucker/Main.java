package su.rumishistem.rumiabot.DiscordPermissionFucker;

import static su.rumishistem.rumiabot.System.Main.SH;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Locale;
import java.util.UUID;

import com.fasterxml.jackson.databind.ObjectMapper;

import static su.rumishistem.rumiabot.System.FunctionModuleLoader.AddCommand;
import static su.rumishistem.rumiabot.System.Main.DISCORD_BOT;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.channel.concrete.TextChannel;
import su.rumishistem.rumi_java_lib.RESOURCE.RESOURCE_MANAGER;
import su.rumishistem.rumi_java_lib.SmartHTTP.HTTP_REQUEST;
import su.rumishistem.rumi_java_lib.SmartHTTP.HTTP_RESULT;
import su.rumishistem.rumi_java_lib.SmartHTTP.Type.EndpointEntrie.Method;
import su.rumishistem.rumi_java_lib.SmartHTTP.Type.EndpointFunction;
import su.rumishistem.rumiabot.System.TYPE.CommandData;
import su.rumishistem.rumiabot.System.TYPE.CommandInteraction;
import su.rumishistem.rumiabot.System.TYPE.CommandOption;
import su.rumishistem.rumiabot.System.TYPE.CommandOptionType;
import su.rumishistem.rumiabot.System.TYPE.FunctionClass;
import su.rumishistem.rumiabot.System.TYPE.ReceiveMessageEvent;

public class Main implements FunctionClass{
	private static final String FUNCTION_NAME = "Discord権限Fucker";
	private static final String FUNCTION_VERSION = "1.0";
	private static final String FUNCTION_AUTOR = "Rumisan";

	private static HashMap<String, String> IDTable = new HashMap<String, String>();

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
		AddCommand(new CommandData("pf", new CommandOption[] {
			new CommandOption("id", CommandOptionType.String, null, true)
		}, true));

		SH.SetRoute("/user/pf", new EndpointFunction() {
			@Override
			public HTTP_RESULT Run(HTTP_REQUEST r) throws Exception {
				String BODY = new String(new RESOURCE_MANAGER(Main.class).getResourceData("/index.html"));
				return new HTTP_RESULT(200, BODY.getBytes(), "text/html; charset=UTF-8");
			}
		});

		SH.SetRoute("/user/api/pf", Method.GET, new EndpointFunction() {
			@Override
			public HTTP_RESULT Run(HTTP_REQUEST r) throws Exception {
				if (r.GetEVENT().getURI_PARAM().get("ID") == null) {
					return new HTTP_RESULT(400, "{\"STATUS\": false}".getBytes(), "application/json; charset=UTF-8");
				}

				//Channel Ch = DISCORD_BOT.getChannelById(Channel.class, r.GetEVENT().getURI_PARAM().get("ID"));
				TextChannel Ch = DISCORD_BOT.getTextChannelById(IDTable.get(r.GetEVENT().getURI_PARAM().get("ID")));

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
		
		SH.SetRoute("/user/api/pf_his", Method.GET, new EndpointFunction() {
			@Override
			public HTTP_RESULT Run(HTTP_REQUEST r) throws Exception {
				if (r.GetEVENT().getURI_PARAM().get("ID") == null) {
					return new HTTP_RESULT(400, "{\"STATUS\": false}".getBytes(), "application/json; charset=UTF-8");
				}

				TextChannel Ch = DISCORD_BOT.getTextChannelById(IDTable.get(r.GetEVENT().getURI_PARAM().get("ID")));

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

	@Override
	public void ReceiveMessage(ReceiveMessageEvent e) {}

	@Override
	public boolean GetAllowCommand(String Name) {
		return Name.equals("pf");
	}

	@Override
	public void RunCommand(CommandInteraction CI) throws Exception {
		String ID = UUID.randomUUID().toString();
		String CID = CI.GetCommand().GetOption("id").GetValueAsString();

		TextChannel Ch = DISCORD_BOT.getTextChannelById(CID);
		if (Ch == null) {
			CI.Reply("...?");
			return;
		}

		IDTable.put(ID, CID);

		CI.Reply("https://rumiabot.rumiserver.com/pf?ID=" + ID);
	}
}
