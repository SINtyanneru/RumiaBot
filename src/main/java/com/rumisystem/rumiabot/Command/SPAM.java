package com.rumisystem.rumiabot.Command;

import net.dv8tion.jda.api.entities.channel.concrete.TextChannel;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import java.util.Timer;
import java.util.TimerTask;

public class SPAM {
    private static int COUNT = 0;
    private static int MAX = 0;
    private static int TIME = 0;
    private static Timer TIMER;
    private static TimerTask TASK;
    private static TextChannel CH;

    public static void Main(SlashCommandInteractionEvent e){
        String TEXT = e.getOption("lat").getAsString();
        CH = (TextChannel) e.getChannel();
        MAX = e.getOption("count").getAsInt();
        TIME = e.getOption("time").getAsInt() * 1000;

        e.getInteraction().reply(e.getUser().getName() + "が実行「" + TEXT + "」したのだ！\n合計「" + MAX + "」回で、「" + (TIME / 1000) + "秒」間隔でスパムするのだ！").queue();

        TIMER = new Timer();
        TASK = new TimerTask() {
            @Override
            public void run() {
                COUNT++;
                // 実行したい処理をここに記述します
                CH.sendMessage(TEXT).queue();

                if (COUNT >= MAX) {
                    stopTask();
                }
            }
        };

        // 1000ミリ秒後から5000ミリ秒間隔でタスクを実行します
        TIMER.schedule(TASK, TIME, TIME);
    }

    private static void stopTask() {
        TIMER.cancel();
        TASK.cancel();
        CH.sendMessage("完了したのだ！").queue();
    }
}
