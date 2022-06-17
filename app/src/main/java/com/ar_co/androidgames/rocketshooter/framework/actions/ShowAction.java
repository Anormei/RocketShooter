package com.ar_co.androidgames.rocketshooter.framework.actions;

import android.util.Log;

import com.ar_co.androidgames.rocketshooter.framework.Sprite;

public class ShowAction extends Action{

    public ShowAction(Sprite sprite){
        super(sprite, 1);
        initSteps();
    }

    @Override
    protected void initSteps() {
        steps[0] = new Step(){

            @Override
            public void perform(float deltaTime) {
                sprite.alpha = 1;
                finished = true;
                step++;
            }
        };
    }
}
