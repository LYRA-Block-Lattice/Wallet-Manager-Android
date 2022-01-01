package com.lyrawallet;

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
}
