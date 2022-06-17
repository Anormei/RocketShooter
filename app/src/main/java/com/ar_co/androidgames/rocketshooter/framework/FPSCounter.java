package com.ar_co.androidgames.rocketshooter.framework;

import android.util.Log;

public class FPSCounter {
    long startTime = System.nanoTime();
    int frames = 0;

    public void logFrame(){
        frames++;
        if(System.nanoTime() - startTime > 1000000000){
            Log.d("FPSCounter", "fps: " + frames);
            logMsg();
            frames = 0;
            startTime = System.nanoTime();
        }
    }

    public void sendMsg(){

    }

    public void logMsg(){

    }
}

