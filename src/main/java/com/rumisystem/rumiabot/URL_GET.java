package com.rumisystem.rumiabot;

import net.dv8tion.jda.api.events.message.MessageReceivedEvent;

import java.util.concurrent.CompletableFuture;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class URL_GET {
    public static void Main(MessageReceivedEvent e){
        String TEXT = e.getMessage().getContentRaw();

        CompletableFuture.runAsync(() -> {
            // 正規表現パターンを定義
            String REGEX = "\\b(https?|ftp|file)://[-a-zA-Z0-9+&@#/%?=~_|!:,.;]*[-a-zA-Z0-9+&@#/%=~_|]";
            Pattern PATTERN = Pattern.compile(REGEX);

            // マッチャーを作成してテキストを検索
            Matcher MATCHER = PATTERN.matcher(TEXT);

            // URLを抽出して表示
            while (MATCHER.find()) {
                String url = MATCHER.group();
                System.out.println("URL: " + url);
                e.getMessage().reply("含まれているURL" + url).queue();
            }
        });
    }
}
