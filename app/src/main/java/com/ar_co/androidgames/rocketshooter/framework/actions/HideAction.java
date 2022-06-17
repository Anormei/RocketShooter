package com.ar_co.androidgames.rocketshooter.framework.actions;

import android.util.Log;

import com.ar_co.androidgames.rocketshooter.framework.Sprite;

public class HideAction extends Action{

    public HideAction(Sprite sprite){
        super(sprite, 1);
        initSteps();
    }

    @Override
    protected void initSteps() {
        steps[0] = new Step(){

            @Override
            public void perform(float deltaTime) {
                Log.i("HideAction", "Hidden");
                sprite.alpha = 0;
                finished = true;
                step++;
            }
        };
    }
}
