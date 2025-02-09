package com.example.spbtex;

public class SafeClick {

    private static final long DELAY = 3000L;  // 3秒未満のイベントは無視
    private long oldTime = 0L;           // 前回イベント実施時刻

    public boolean isPassTime(){
        return isPassTime(DELAY);
    }

    public boolean isPassTime(long delay) {
        // 今の時間を覚える
        long time = System.currentTimeMillis();

        // 前回の時間と比較してdelay秒以上経っていないと無視
        if (time - oldTime < delay) {
            return false;
        }

        // 一定時間経過したら実行可能とする
        oldTime = time;
        return true;
    }
}
