package com.ar_co.androidgames.rocketshooter.main.gameplay;

import android.util.SparseArray;

import com.ar_co.androidgames.rocketshooter.framework.GLGame;
import com.ar_co.androidgames.rocketshooter.framework.Pool;
import com.ar_co.androidgames.rocketshooter.main.enemy.Enemy;

import java.util.HashMap;
import java.util.Random;

public abstract class Mission {

    protected final GLGame game;
    public float missionTime = 0;
    protected static final Random random = new Random();
    protected float timeElapsed = 0;

    protected GameHandler gameHandler;
    protected TimeStamp[] timeStamps;
    protected SparseArray<Pool<Enemy>> spawner = new SparseArray<>();

    private int curr = 0;

    public Mission(final GLGame game){
        this.game = game;
    }

    public abstract void init();

    public void setGameHandler(GameHandler gameHandler){
        this.gameHandler = gameHandler;
    }

    public void update(float deltaTime){
        missionTime += deltaTime;
        timeElapsed += deltaTime;

        if(curr < timeStamps.length) {
            TimeStamp timeStamp = timeStamps[curr];

            timeStamp.receiveTime(timeElapsed);

            if (timeStamp.complete) {
                timeElapsed = 0;
                curr++;
            }
        }
    }

    public abstract void despawnEnemy(Enemy enemy);

    public boolean isFinished(){
        return curr >= timeStamps.length;
    }


}
