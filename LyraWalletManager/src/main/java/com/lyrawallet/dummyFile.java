package com.lyrawallet;

import android.os.Handler;

import java.util.Timer;
import java.util.TimerTask;

public class dummyFile {
    void oneTimer() {
        Timer timer = new Timer();
        timer.scheduleAtFixedRate(new TimerTask() {
            @Override
            public void run () {
            }
        },1000,1000);
    }
    void oneShot() {
        new Handler().postDelayed(new Runnable() {
            public void run() {
            }
        }, 100);
    }
}
