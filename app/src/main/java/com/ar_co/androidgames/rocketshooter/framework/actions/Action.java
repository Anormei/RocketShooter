package com.ar_co.androidgames.rocketshooter.framework.actions;

import com.ar_co.androidgames.rocketshooter.framework.Sprite;

public abstract class Action {

    public Sprite sprite;
    public Step[] steps;
    public int step = 0;

    public boolean finished = false;

    public Action(Sprite sprite, int steps){
        this.sprite = sprite;
        this.steps = new Step[steps+1];
        this.steps[steps] = new Step(){

            @Override
            public void perform(float deltaTime) {
                //empty
            }
        };
    }

    public void rewind(){
        step = 0;
        finished = false;
    }

    protected abstract void initSteps();

}
