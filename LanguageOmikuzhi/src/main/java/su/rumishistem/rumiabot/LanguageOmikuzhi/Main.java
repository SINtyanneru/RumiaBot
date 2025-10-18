package su.rumishistem.rumiabot.LanguageOmikuzhi;

import java.util.Random;

import su.rumishistem.rumiabot.System.CommandRegister;
import su.rumishistem.rumiabot.System.Type.CommandInteraction;
import su.rumishistem.rumiabot.System.Type.CommandOptionRegist;
import su.rumishistem.rumiabot.System.Type.FunctionClass;
import su.rumishistem.rumiabot.System.Type.RunCommand;
import su.rumishistem.rumiabot.System.Type.SourceType;

public class Main implements FunctionClass{
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
	public String function_name() {
		return "言語おみくじ";
	}
	@Override
	public String function_version() {
		return "0.5";
	}
	@Override
	public String function_author() {
		return "るみ";
	}

	@Override
	public void init() {
		CommandRegister.add_command("language_omikuzhi", new CommandOptionRegist[] {}, false, new RunCommand() {
			@Override
			public void run(CommandInteraction e) throws Exception {
				Random rnd = new Random();
				int select = rnd.nextInt(0, language_list.length);
				Language lang = language_list[select];

				if (e.get_source() == SourceType.Misskey) {
					StringBuilder sb = new StringBuilder();
					sb.append("$[x2 $[bg.color=00AAFF $[fg.color=FFFFFF $[ruby "+lang.name+" "+lang.ruby+"]]]]");
					sb.append("\n");
					sb.append("https://eth.rumiserver.com/play/acagcidyeo6x0b5i");

					e.reply(sb.toString());
				} else {
					e.reply(language_list[select].name + "("+language_list[select].ruby+")");
				}
			}
		});
	}
}
