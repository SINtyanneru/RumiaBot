package su.rumishistem.rumiabot.LanguageOmikuzhi;

import static su.rumishistem.rumiabot.System.FunctionModuleLoader.AddCommand;

import java.util.Random;

import su.rumishistem.rumiabot.System.TYPE.CommandData;
import su.rumishistem.rumiabot.System.TYPE.CommandInteraction;
import su.rumishistem.rumiabot.System.TYPE.CommandOption;
import su.rumishistem.rumiabot.System.TYPE.FunctionClass;
import su.rumishistem.rumiabot.System.TYPE.ReceiveMessageEvent;
import su.rumishistem.rumiabot.System.TYPE.SourceType;

public class Main implements FunctionClass{
	private static final String FUNCTION_NAME = "ランキング";
	private static final String FUNCTION_VERSION = "1.0";
	private static final String FUNCTION_AUTOR = "Rumisan";

	private static final Language[] language_list = new Language[] {
		new Language("日本語", "日本語"),
		new Language("Esperanto", "エスペラント"),
		new Language("Jinghpaw", "チンポ語"),
		new Language("Namfau", "アナル語"),
		new Language("普通话", "標準中国語"),
		new Language("臺灣華語", "台湾華語"),
		new Language("Tâi-oân-oē", "台湾語"),
		new Language("廣東話", "広東語"),
		new Language("اللغة العربية", "アラビア語"),
		new Language("ދިވެހި", "ディベヒ語"),
		new Language("ᮘᮞ ᮞᮥᮔ᮪ᮓ", "スンダ語"),
		new Language("ਪੰਜਾਬੀ", "パンジャーブ語"),
		new Language("हिंदी", "ヒンディー語"),
		new Language("Prākr̥tam", "プラークリット")
	};

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
		AddCommand(new CommandData("language_omikuzhi", new CommandOption[] {}, false));
	}

	@Override
	public void ReceiveMessage(ReceiveMessageEvent e) {}

	@Override
	public boolean GetAllowCommand(String Name) {
		return Name.equals("language_omikuzhi");
	}

	@Override
	public void RunCommand(CommandInteraction CI) throws Exception {
		Random rnd = new Random();
		int select = rnd.nextInt(0, language_list.length);
		Language lang = language_list[select];

		if (CI.GetSource() == SourceType.Misskey) {
			StringBuilder sb = new StringBuilder();
			sb.append("$[x2 $[bg.color=00AAFF $[fg.color=FFFFFF $[ruby "+lang.name+" "+lang.ruby+"]]]]");
			sb.append("\n");
			sb.append("https://eth.rumiserver.com/play/acagcidyeo6x0b5i");

			CI.Reply(sb.toString());
		} else if (CI.GetSource() == SourceType.Discord) {
			CI.Reply(language_list[select].name + "("+language_list[select].ruby+")");
		}
	}
}
