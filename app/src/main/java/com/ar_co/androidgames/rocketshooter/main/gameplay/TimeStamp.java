package com.ar_co.androidgames.rocketshooter.main.gameplay;

public abstract class TimeStamp {

    boolean complete;
    float time;

    public TimeStamp(float time){
        this.time = time;
        complete = false;
    }

    public void receiveTime(float time){
        if(time >= this.time){
            perform();
        }
    }

    public abstract void perform();

}
