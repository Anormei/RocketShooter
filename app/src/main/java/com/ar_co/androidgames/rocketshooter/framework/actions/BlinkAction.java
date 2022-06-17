package com.ar_co.androidgames.rocketshooter.framework.actions;

import android.util.Log;

import com.ar_co.androidgames.rocketshooter.framework.Sprite;

public class BlinkAction extends Action{

    private float blinkInterval;
    private float appearInterval;
    private float time;

    private int count;
    private int repeat;


    private float currAlpha;


    public BlinkAction(Sprite sprite, float blinkInterval, float appearInterval, int repeat){
        super(sprite, 3);
        this.blinkInterval = blinkInterval;
        this.appearInterval = appearInterval;
        this.repeat = repeat;
        this.count = 0;
        this.time = 0;
        initSteps();
    }

    @Override
    protected void initSteps() {
        steps[0] = new Step() {
            @Override
            public void perform(float deltaTime) {
                currAlpha = sprite.alpha;
                step++;
            }
        };

        steps[1] = new Step(){

            @Override
            public void perform(float deltaTime) {
                time += deltaTime;
                if(time >= appearInterval){
                    time -= appearInterval;
                    sprite.alpha = 0;
                    step++;
                }
            }
        };

        steps[2] = new Step(){

            @Override
            public void perform(float deltaTime) {
                time += deltaTime;

                if(time >= blinkInterval){
                    time -= blinkInterval;
                    sprite.alpha = currAlpha;
                    step++;
                    if(count < repeat - 1){
                        count++;
                        BlinkAction.this.rewind();

                    }else{
                        count = 0;
                        finished = true;
                        step++;
                    }
                }
            }
        };

        /*steps[3] = new Step(){

            @Override
            public void perform(float deltaTime) {
                if(count < repeat - 1){
                    count++;
                    BlinkAction.this.rewind();

                }else{
                    count = 0;
                    complete = true;
                    step++;
                }
            }
        };*/
    }
}
