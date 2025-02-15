package su.rumishistem.rumiabot.CalcFunction;

import su.rumishistem.rumiabot.System.TYPE.CommandInteraction;
import su.rumishistem.rumiabot.System.TYPE.FunctionClass;
import su.rumishistem.rumiabot.System.TYPE.ReceiveMessageEvent;
import jakarta.el.ELProcessor;

public class Main implements FunctionClass {
	private static final String FUNCTION_NAME = "計算機";
	private static final String FUNCTION_VERSION = "1.0";
	private static final String FUNCTION_AUTOR = "Rumisan";

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
	}
	@Override
	public void ReceiveMessage(ReceiveMessageEvent e) {
		//↓ELProcessorが開発者がデバッグすらして無さそうなゴミライブラリだったので封印
		/*
		String Text = e.GetMessage().GetText();
		if (Text.startsWith(">") && Text.endsWith("=")) {
			//整形
			Text = Text.replaceAll("^>", "");
			Text = Text.replaceAll("=$", "");

			//なぜかコイツ、全部のクラスにアクセスできてしまう
			if (Text.matches("[^().+-/*%]*")) {
				//計算式実行
				ELProcessor EP = new ELProcessor();
				//EP.defineBean("Math", SafeMath.class);

				Object RESULT = EP.eval(Text);

				if (RESULT != null) {
					e.GetMessage().Reply(RESULT.toString());
				} else {
					e.GetMessage().Reply("計算できませんでした");
				}
			} else {
				e.GetMessage().Reply("不正な計算式");
			}
		}*/
	}
	@Override
	public boolean GetAllowCommand(String Name) {
		return false;
	}
	@Override
	public void RunCommand(CommandInteraction CI) throws Exception {
	}
}
