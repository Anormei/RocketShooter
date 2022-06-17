package com.ar_co.androidgames.rocketshooter.framework;

import com.ar_co.androidgames.rocketshooter.interfaces.Game;

public abstract class Screen {
    private final Game GAME;

    public Screen(Game game){
        GAME = game;
    }

    public Game getGame(){
        return GAME;
    }

    public abstract void onStart();

    public abstract void update(float deltaTime);

    public abstract void present(float deltaTime);

    public abstract void pause();

    public abstract void resume();

    public abstract void dispose();

    public void rawDraw(float deltaTime){}


    public boolean onBackPressed(){
        return false;
    }
}
