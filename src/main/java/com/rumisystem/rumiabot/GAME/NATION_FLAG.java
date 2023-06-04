package com.rumisystem.rumiabot.GAME;

import net.dv8tion.jda.api.events.message.MessageReceivedEvent;

import java.lang.reflect.Array;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.text.SimpleDateFormat;
import java.util.*;

public class NATION_FLAG {
    public static List<NATION_FLAG_SAVE> SAVE = new ArrayList<>();//セーブデータの配列
    public static List<NATION_FLAG_LIST> NATION_LIST = new ArrayList<>() {{//国のリスト
        //初期化と同時に国を追加
        add(NATION_LIST_ADD("JP", "日本国", List.of("日本", "Japan", "japan"), 1, "日本国は、東アジアに位置する議会制民主主義国家!\n首都は無いけど、事実上の首都は東京だよ。\n公用語もないけど、事実上日本語が公用語だよ！\nあと日本に有る消滅危機言語は「アイヌ語/沖縄語/八丈語/奄美語/国頭語/宮古語/八重山語/与那国語」だよ！"));
        add(NATION_LIST_ADD("USA", "アメリカ合衆国", List.of("アメリカ", "America", "america"), 1, "アメリカ合衆国は、北アメリカに位置する連邦共和制国家！\n首都はワシントンD.Cだよ！"));
        add(NATION_LIST_ADD("RUS", "ロシア連邦", List.of("ロシア", "Russia", "russia"), 1, "ロシア連邦は、ユーラシア大陸北部に位置する連邦共和制国家！\n首都はモスクワ！公用語はロシア語！\n意外と知られてないけど、ロシアは連邦国家だよ〜\nあと間違われがちだけどソ連とロシアは全くの別物！強いて言うならロシア・ソビエト社会主義共和国が今のロシアに近いかな？"));
    }};
    public static void Main(MessageReceivedEvent e) throws NoSuchAlgorithmException {
        String[] cmd = e.getMessage().getContentRaw().split(" ");
        if(cmd.length == 1){//Nullチェックも兼ねている
            e.getMessage().reply("使い方が違う！").queue();
            return;
        }
        //ゲーム自体の処理
        if(cmd[1].equals("START")){//スタート
            // 現在の時刻を取得
            Date now = new Date();
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            String currentTime = sdf.format(now);

            // MD5ハッシュ値に変換
            MessageDigest md = MessageDigest.getInstance("MD5");
            md.update(currentTime.getBytes());
            byte[] digest = md.digest();

            // バイト配列を16進数文字列に変換
            StringBuilder sb = new StringBuilder();
            for (byte b : digest) {
                sb.append(String.format("%02x", b & 0xff));
            }
            String md5Hash = sb.toString();

            //ゲームを始めると言う
            e.getMessage().reply("国旗当てゲーム始めるよ！\n回答「r.国旗当てゲーム ANS " + md5Hash + " 国の名前」\nってしてね！\n国の名前は正式名称でお願いね！(一般的な略称も可能)").queue();


            // LEVELが1のオブジェクトを抽出
            List<NATION_FLAG_LIST> LEVEL_EQUALS_OBJ = new ArrayList<>();
            for (NATION_FLAG_LIST row : NATION_LIST) {
                if (row.LEVEL == Integer.parseInt(cmd[2])) {
                    LEVEL_EQUALS_OBJ.add(row);
                }
            }

            // ランダムなオブジェクトを選択
            Random random = new Random();
            if (!LEVEL_EQUALS_OBJ.isEmpty()) {
                int randomIndex = random.nextInt(LEVEL_EQUALS_OBJ.size());
                NATION_FLAG_LIST randomObject = LEVEL_EQUALS_OBJ.get(randomIndex);

                //セーブに保存
                NATION_FLAG_SAVE SAVE_TEMP = new NATION_FLAG_SAVE();
                SAVE_TEMP.ID = md5Hash;
                SAVE_TEMP.LEVEL = Integer.parseInt(cmd[2]);
                SAVE_TEMP.ANS = randomObject.ID;
                SAVE_TEMP.UID = e.getMessage().getAuthor().getId();
                SAVE.add(SAVE_TEMP);

                //国旗の画像を貼る
                e.getMessage().reply("国旗だよ\nhttps://rumiserver.com/Asset/FLAGS/" + randomObject.ID + ".png").queue();
            } else {
                System.out.println("LEVELが1のオブジェクトは存在しません。");
            }
        }else if(cmd[1].equals("ANS")){//回答
            for (NATION_FLAG_SAVE save: SAVE) {
                if(save.ID.equals(cmd[2]) && save.UID.equals(e.getMessage().getAuthor().getId())){//その問題があるか
                    //正解しているかをチェックする機構
                    for(NATION_FLAG_LIST ROW:NATION_LIST){
                        if(save.ANS.equals(ROW.ID) && cmd[3].equals(ROW.NAME)){//問題は合っているか
                            //合ってる！
                            e.getMessage().reply("正解！\n" + ROW.DESC).queue();
                            return;
                        }else {
                            for(String ROW_SHORT:ROW.SHORT_NAME){
                                if(cmd[3].equals(ROW_SHORT)){
                                    //合ってる！
                                    e.getMessage().reply("正解！\n" + ROW.DESC).queue();
                                    return;
                                }
                            }
                        }
                    }
                    //間違ってる！
                    e.getMessage().reply("残念！正解は「" + save.ANS + "」でした！").queue();
                    return;
                }
            }
            //ここに来るということは、セーブが存在しなかったということだ！
            e.getMessage().reply("エラー！そのセーブデータは存在しません！").queue();
        }else {
            e.getMessage().reply("使い方が違う！").queue();
        }
    }

    public static NATION_FLAG_LIST NATION_LIST_ADD(String ID, String NAME, List<String> SHORT_NAME, int LEVEL, String DESC){
        NATION_FLAG_LIST LIST_TEMP = new NATION_FLAG_LIST();
        LIST_TEMP.ID = ID;
        LIST_TEMP.NAME = NAME;
        LIST_TEMP.SHORT_NAME = SHORT_NAME;
        LIST_TEMP.LEVEL = LEVEL;
        LIST_TEMP.DESC = DESC;
        return LIST_TEMP;
    }
}

class NATION_FLAG_SAVE{
    public String ID;
    public int LEVEL;
    public String ANS;
    public String UID;
}

class NATION_FLAG_LIST{
    public String ID;
    public String NAME;
    public List<String> SHORT_NAME;
    public int LEVEL;
    public String DESC;
}